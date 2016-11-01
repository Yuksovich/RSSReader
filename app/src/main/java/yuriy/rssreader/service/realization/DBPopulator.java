package yuriy.rssreader.service.realization;


import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import yuriy.rssreader.data.ReaderContract.RSSEntry;
import yuriy.rssreader.data.RssDBOpenHelper;
import yuriy.rssreader.data.SingleRSSEntry;

import java.util.ArrayList;


public final class DBPopulator {

    private final RssDBOpenHelper dbOpenHelper;
    private final DuplicateChecker duplicateChecker;

    public DBPopulator(@NonNull final Context context) {
        dbOpenHelper = new RssDBOpenHelper(context);
        duplicateChecker = new DuplicateChecker(context);
    }

    public void populate(@NonNull final ArrayList<SingleRSSEntry> dataArrayList) throws SQLException {


        ArrayList<SingleRSSEntry> croppedDataArrayList = duplicateChecker.cropDuplicateEntries(dataArrayList);
        final SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        final ContentValues values = new ContentValues();

        for (SingleRSSEntry entry : croppedDataArrayList) {
            values.put(RSSEntry.COLUMN_NAME_CHANNEL_TITLE, entry.getChannelTitle());
            values.put(RSSEntry.COLUMN_NAME_CHANNEL_DESCRIPTION, entry.getChannelDescription());
            values.put(RSSEntry.COLUMN_NAME_CHANNEL_IMAGE_URL, entry.getChannelImageURL());
            values.put(RSSEntry.COLUMN_NAME_ITEM_LINK, entry.getItemLink());
            values.put(RSSEntry.COLUMN_NAME_ITEM_TITLE, entry.getItemTitle());
            values.put(RSSEntry.COLUMN_NAME_ITEM_DESCRIPTION, entry.getItemDescription());
            values.put(RSSEntry.COLUMN_NAME_ITEM_PUB_DATE, entry.getItemPubDate());
            values.put(RSSEntry.COLUMN_NAME_BEEN_VIEWVED, "false");
            try {
                database.beginTransaction();
                database.insert(RSSEntry.TABLE_NAME, RSSEntry.COLUMN_NAME_NULLABLE, values);
                database.setTransactionSuccessful();
            }finally {
                database.endTransaction();
            }
        }

    }

    public void close() {
        dbOpenHelper.close();
    }


}
