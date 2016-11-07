package yuriy.rssreader.database;


import android.provider.BaseColumns;

public final class RSSEntryColumns implements BaseColumns {
    private RSSEntryColumns() {
        throw new UnsupportedOperationException();
    }

    static final String TABLE_NAME = "rssTable";
    public static final String COLUMN_NAME_CHANNEL_TITLE = "channelTitle";
    static final String COLUMN_NAME_CHANNEL_DESCRIPTION = "channelDescription";
    static final String COLUMN_NAME_CHANNEL_IMAGE_URL = "channelImageUrl";
    public static final String COLUMN_NAME_ITEM_LINK = "itemLink";
    public static final String COLUMN_NAME_ITEM_TITLE = "itemTitle";
    static final String COLUMN_NAME_ITEM_DESCRIPTION = "itemDescription";
    public static final String COLUMN_NAME_ITEM_PUB_DATE = "pubDate";
    public static final String COLUMN_NAME_BEEN_VIEWED = "viewed";
    static final String COLUMN_NAME_NULLABLE = "";


}