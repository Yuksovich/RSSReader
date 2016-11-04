package yuriy.rssreader.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;


public final class RssDBOpenHelper extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String CREATE_TABLE = "CREATE TABLE " +
            RSSEntryColumns.TABLE_NAME + " (" + RSSEntryColumns._ID + " integer primary key autoincrement, " +
            RSSEntryColumns.COLUMN_NAME_CHANNEL_TITLE + TEXT_TYPE + " ," +
            RSSEntryColumns.COLUMN_NAME_CHANNEL_IMAGE_URL + TEXT_TYPE + " ," +
            RSSEntryColumns.COLUMN_NAME_CHANNEL_DESCRIPTION + TEXT_TYPE + " ," +
            RSSEntryColumns.COLUMN_NAME_ITEM_TITLE + TEXT_TYPE + " ," +
            RSSEntryColumns.COLUMN_NAME_ITEM_LINK + TEXT_TYPE + " ," +
            RSSEntryColumns.COLUMN_NAME_ITEM_DESCRIPTION + TEXT_TYPE + " ," +
            RSSEntryColumns.COLUMN_NAME_ITEM_PUB_DATE + TEXT_TYPE + " ," +
            RSSEntryColumns.COLUMN_NAME_BEEN_VIEWED + TEXT_TYPE + ")";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "RssReader.db";
    private static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + RSSEntryColumns.TABLE_NAME;

    public RssDBOpenHelper(final @NonNull Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE);
        onCreate(db);
    }

}
