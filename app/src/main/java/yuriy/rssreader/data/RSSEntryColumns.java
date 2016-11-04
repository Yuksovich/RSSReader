package yuriy.rssreader.data;


import android.provider.BaseColumns;

public abstract class RSSEntryColumns implements BaseColumns {
    private RSSEntryColumns() {

    }

    public static final String TABLE_NAME = "rssTable";
    public static final String COLUMN_NAME_CHANNEL_TITLE = "channelTitle";
    public static final String COLUMN_NAME_CHANNEL_DESCRIPTION = "channelDescription";
    public static final String COLUMN_NAME_CHANNEL_IMAGE_URL = "channelImageUrl";
    public static final String COLUMN_NAME_ITEM_LINK = "itemLink";
    public static final String COLUMN_NAME_ITEM_TITLE = "itemTitle";
    public static final String COLUMN_NAME_ITEM_DESCRIPTION = "itemDescription";
    public static final String COLUMN_NAME_ITEM_PUB_DATE = "pubDate";
    public static final String COLUMN_NAME_BEEN_VIEWED = "viewed";
    public static final String COLUMN_NAME_NULLABLE = "";


}