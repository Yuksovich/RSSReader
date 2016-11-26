package yuriy.rssreader.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Closeable;
import java.util.ArrayList;


public final class DBWriter implements Closeable {

    private final static String FALSE_FLAG = "false";
    private static final String TRUE_FLAG = "true";
    private static final String WHERE_CLAUSE_ITEM_LINK = TableColumns.COLUMN_NAME_ITEM_LINK + " = ?";
    private static final String WHERE_CLAUSE_CHANNEL_LINK = TableColumns.COLUMN_NAME_CHANNEL_URL + " = ?";
    private final DatabaseOpenHelper dbOpenHelper;
    private final Context context;
    private int newEntriesCount = 0;
    private SQLiteDatabase database;
    private ArrayList<SingleRSSEntry> newEntries;

    public DBWriter(@NonNull final Context context) {
        this.context = context;
        dbOpenHelper = new DatabaseOpenHelper(context);
    }

    public void populate(final ArrayList<SingleRSSEntry> dataArrayList, final String channelUrl) throws SQLException {
        if (channelUrl == null || dataArrayList == null || dataArrayList.isEmpty()) {
            return;
        }
        final DuplicateChecker duplicateChecker = new DuplicateChecker(context);
        final ArrayList<SingleRSSEntry> croppedDataArrayList = duplicateChecker.cropDuplicateEntries(dataArrayList);
        if(croppedDataArrayList.isEmpty()){
            return;
        }
        newEntries = new ArrayList<>(croppedDataArrayList.size());
        final ContentValues values = new ContentValues();
        database = dbOpenHelper.getWritableDatabase();

        for (SingleRSSEntry entry : croppedDataArrayList) {
            values.put(TableColumns.COLUMN_NAME_CHANNEL_URL, channelUrl);
            values.put(TableColumns.COLUMN_NAME_CHANNEL_TITLE, entry.getChannelTitle());
            values.put(TableColumns.COLUMN_NAME_CHANNEL_DESCRIPTION, entry.getChannelDescription());
            values.put(TableColumns.COLUMN_NAME_CHANNEL_IMAGE_URL, entry.getChannelImageURL());
            values.put(TableColumns.COLUMN_NAME_ITEM_LINK, entry.getItemLink());
            values.put(TableColumns.COLUMN_NAME_ITEM_TITLE, entry.getItemTitle());
            values.put(TableColumns.COLUMN_NAME_ITEM_DESCRIPTION, entry.getItemDescription());
            values.put(TableColumns.COLUMN_NAME_ITEM_PUB_DATE, entry.getItemPubDate());
            values.put(TableColumns.COLUMN_NAME_BEEN_VIEWED, FALSE_FLAG);
            try {
                database.beginTransaction();

                long n = database.insert(TableColumns.TABLE_NAME, TableColumns.COLUMN_NAME_NULLABLE, values);
                if (n != -1) {
                    newEntriesCount++;
                    newEntries.add(entry);
                }
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }
        if (database != null) {
            database.close();
        }

    }

    public void setEntryBeenViewed(final String itemLink) throws SQLException {
        if (itemLink == null) {
            return;
        }
        final ContentValues value = new ContentValues();
        value.put(TableColumns.COLUMN_NAME_BEEN_VIEWED, TRUE_FLAG);
        database = dbOpenHelper.getWritableDatabase();
        database.update(TableColumns.TABLE_NAME, value, WHERE_CLAUSE_ITEM_LINK, new String[]{itemLink});
    }

    public void close() {
        dbOpenHelper.close();
    }

    public int getNewEntriesCount() {
        return newEntriesCount;
    }

    public void deleteEntry(final String itemLink) throws SQLException {
        if (itemLink == null) {
            return;
        }
        try {
            final String[] whereArgs = {itemLink};
            database = dbOpenHelper.getWritableDatabase();
            database.delete(TableColumns.TABLE_NAME, WHERE_CLAUSE_ITEM_LINK, whereArgs);
        } finally {
            if (database != null) {
                database.close();
            }
        }

    }

    public void deleteAll() throws SQLException {
        try {
            database = dbOpenHelper.getWritableDatabase();
            database.delete(TableColumns.TABLE_NAME, null, null);
        } finally {
            if (database != null) {
                database.close();
            }
        }
    }

    public void deleteAllEntriesOfChannel(final String channel) throws SQLException {
        if (channel == null) {
            return;
        }
        try {
            final String[] whereArgs = {channel};
            database = dbOpenHelper.getWritableDatabase();
            database.delete(TableColumns.TABLE_NAME, WHERE_CLAUSE_CHANNEL_LINK, whereArgs);
        } finally {
            if (database != null) {
                database.close();
            }
        }
    }

    public @Nullable ArrayList<SingleRSSEntry> getNewEntries(){
        return newEntries;
    }

}

