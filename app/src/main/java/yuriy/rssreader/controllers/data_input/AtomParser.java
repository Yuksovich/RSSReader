package yuriy.rssreader.controllers.data_input;


import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import yuriy.rssreader.database.SingleRSSEntry;
import yuriy.rssreader.rssexceptions.NoRSSContentException;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

final class AtomParser implements Parsable {

    private static final String HREF = "href";
    private static final String NAMESPACE = null;
    private final String receivedStringData;
    private String channelTitle;
    private String channelImageURL;
    private String channelDescription;

    final private static String ITEM_TITLE_TAG = "title";
    final private static String ITEM_LINK_TAG = "link";
    final private static String ITEM_PUBLIC_DATE_TAG = "published";
    final private static String ITEM_DESCRIPTION_TAG = "summary";
    final private static String ITEM_TAG = "entry";
    final private static String ITEM_BEEN_VIEWED_FALSE = "false";
    final private static String CHANNEL_TITLE_TAG = "title";
    final private static String CHANNEL_DESCRIPTION = "subtitle";
    final private static String ATOM_TAG = "feed";
    final private static String CHANNEL_IMAGE_TAG = "logo";
    final private static String INPUT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    final private static String EMPTY_STRING = "";
    final private static String SPACER = " ";

    AtomParser(final String receivedStringData) throws NoRSSContentException, IOException {
        this.receivedStringData = receivedStringData;
        getChannelOrThrow();
    }

    private void getChannelOrThrow() throws NoRSSContentException, IOException {

        try {

            channelTitle = getChannelInfo(CHANNEL_TITLE_TAG);
            channelDescription = getChannelInfo(CHANNEL_DESCRIPTION);
            channelImageURL = getChannelInfo(CHANNEL_IMAGE_TAG);

        } catch (XmlPullParserException e) {
            throw new NoRSSContentException();
        }

    }

    private String getChannelInfo(final String tagType) throws XmlPullParserException, IOException, NoRSSContentException {


        XmlPullParser xmlParser = Xml.newPullParser();
        xmlParser.setInput(new StringReader(receivedStringData));
        int eventType = xmlParser.next();

        if (!(eventType == XmlPullParser.START_TAG && xmlParser.getName().equals(ATOM_TAG))) {
            throw new NoRSSContentException();
        }

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_TAG&&xmlParser.getName().equals(tagType)){
                eventType = xmlParser.next();
                if(eventType==XmlPullParser.TEXT) {
                    return xmlParser.getText();
                }
            }
            eventType = xmlParser.next();
        }
        return EMPTY_STRING;
    }

    @Override
    public ArrayList<SingleRSSEntry> receiveAllItems() throws NoRSSContentException, IOException {

        String itemLink, itemTitle, itemDescription, itemPubDate;
        final ArrayList<SingleRSSEntry> entries = new ArrayList<>();
        try {
            final XmlPullParser xmlParser = Xml.newPullParser();
            xmlParser.setInput(new StringReader(receivedStringData));

            int eventType = xmlParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG && xmlParser.getName().equals(ITEM_TAG)) {
                    itemLink = EMPTY_STRING;
                    itemTitle = EMPTY_STRING;
                    itemDescription = EMPTY_STRING;
                    itemPubDate = EMPTY_STRING;
                    while (!(eventType == XmlPullParser.END_TAG && xmlParser.getName().equals(ITEM_TAG))) {
                        eventType = xmlParser.next();
                        if (eventType == XmlPullParser.START_TAG) {

                            switch (xmlParser.getName()) {

                                case (ITEM_LINK_TAG):
                                    itemLink = xmlParser.getAttributeValue(NAMESPACE, HREF);
                                    break;

                                case (ITEM_TITLE_TAG):
                                    itemTitle = xmlParser.nextText();
                                    break;

                                case (ITEM_DESCRIPTION_TAG):
                                    itemDescription = xmlParser.nextText();
                                    break;

                                case (ITEM_PUBLIC_DATE_TAG):
                                    itemPubDate = xmlParser.nextText();
                                    break;
                            }
                        }
                    }
                    entries.add(new SingleRSSEntry.Builder()
                            .channelTitle(channelTitle)
                            .channelDescription(channelDescription)
                            .channelImageURL(channelImageURL)
                            .itemLink(itemLink)
                            .itemTitle(itemTitle)
                            .itemDescription(itemDescription)
                            .itemPubDate(formatPubDate(itemPubDate))
                            .itemBeenViewed(ITEM_BEEN_VIEWED_FALSE)
                            .build());
                } else {
                    eventType = xmlParser.next();
                }
            }
        } catch (XmlPullParserException e) {
            throw new NoRSSContentException();
        }
        return entries;
    }

    private String formatPubDate(final String inputDate) {
        String formattedDate = inputDate;

        try {
            final SimpleDateFormat dateFormat = new SimpleDateFormat(INPUT_DATE_PATTERN, Locale.ENGLISH);
            final long timeInMillis = dateFormat.parse(inputDate).getTime();

            final Date date = new Date(timeInMillis);
            final Time time = new Time(timeInMillis);

            formattedDate = date.toString() + SPACER + time.toString();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    @Override
    public String getChannelDescription() {
        return channelDescription;
    }
}



