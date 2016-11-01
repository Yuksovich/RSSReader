package yuriy.rssreader.data;


public final class SingleRSSEntry {

    private final String channelTitle;
    private final String channelImageURL;
    private final String channelDescription;
    private final String itemLink;
    private final String itemTitle;
    private final String itemDescription;
    private final String itemPubDate;
    private final String beenViewed;

    public SingleRSSEntry(final String channelTitle, final String channelImageURL,
                          final String channelDescription, final String itemLink, final String itemTitle,
                          final String itemDescription, final String itemPubDate, final String beenViewed) {

        this.channelTitle = channelTitle;
        this.channelImageURL = channelImageURL;
        this.channelDescription = channelDescription;
        this.itemLink = itemLink;
        this.itemTitle = itemTitle;
        this.itemDescription = itemDescription;
        this.itemPubDate = itemPubDate;
        this.beenViewed = beenViewed;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public String getChannelImageURL() {
        return channelImageURL;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public String getItemLink() {
        return itemLink;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getItemPubDate() {
        return itemPubDate;
    }

    public boolean itemBeenViewed() {
        return "true".equals(beenViewed);
    }
}
