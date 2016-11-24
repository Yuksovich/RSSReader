package yuriy.rssreader.controllers.data_input;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import yuriy.rssreader.rssexceptions.NoRSSContentException;

import java.io.IOException;
import java.io.StringReader;

public final class RssOrAtom {
    private static final String ATOM = "feed";
    private static final String ATOM_NAMESPACE = "http://www.w3.org/2005/Atom";
    private static final String RSS = "rss";

    private RssOrAtom() {
        throw new UnsupportedOperationException();
    }

    public static Parsable getParser(final String inputData, final String url)
            throws NoRSSContentException,
            IOException,
            XmlPullParserException,
            NullPointerException {
        final XmlPullParser xmlPullParser = Xml.newPullParser();
        xmlPullParser.setInput(new StringReader(inputData));
        xmlPullParser.next();
        switch (xmlPullParser.getName()) {
            case (ATOM):
                if (xmlPullParser.getNamespace().equals(ATOM_NAMESPACE)) {
                    return new AtomParser(inputData, url);
                } else {
                    throw new NoRSSContentException();
                }
            case (RSS):
                return new RssParser(inputData, url);
            default:
                throw new NoRSSContentException();

        }
    }
}
