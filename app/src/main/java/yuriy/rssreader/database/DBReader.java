package yuriy.rssreader.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import yuriy.rssreader.rssexceptions.DatabaseIsEmptyException;

import java.io.Closeable;
import java.util.ArrayList;

public final class DBReader implements Closeable {
    private final ArrayList<SingleRSSEntry> listOfEntries;
    private final SQLiteDatabase database;
    private final RssDBOpenHelper dbOpenHelper;
    private Cursor cursor;

    private static final String[] WITHOUT_ARGUMENTS = null;
    private final static String SELECTION_BY_ITEM_LINK = RSSEntryColumns.COLUMN_NAME_ITEM_LINK + " = ?";
    private final static String SORT_ORDER = RSSEntryColumns.COLUMN_NAME_ITEM_PUB_DATE + " DESC";
    private final static String[] COLUMNS_ALL = null;
    private final static String SELECTION_ALL = null;
    private final static String[] SELECTION_ARGS_ALL = null;
    private final static String GROUP_BY_ALL = null;
    private final static String HAVING_ALL = null;
    private final static String LIMIT_ALL = null;

    public DBReader(final Context context) throws SQLException {
        dbOpenHelper = new RssDBOpenHelper(context);
        listOfEntries = new ArrayList<>();
        database = dbOpenHelper.getReadableDatabase();
    }

    public ArrayList<SingleRSSEntry> read() throws SQLException, DatabaseIsEmptyException {
        cursor = getCursor(WITHOUT_ARGUMENTS);

        if (cursor.moveToFirst()) {
            for (cursor.moveToFirst(); cursor.isAfterLast(); cursor.moveToNext()) {

                final String channelTitle = cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_CHANNEL_TITLE));
                final String channelImageURL = cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_CHANNEL_IMAGE_URL));
                final String channelDescription = cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_CHANNEL_DESCRIPTION));
                final String itemLink = cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_ITEM_LINK));
                final String itemTitle = cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_ITEM_TITLE));
                final String itemDescription = cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_ITEM_DESCRIPTION));
                final String itemPubDate = cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_ITEM_PUB_DATE));
                final String itemBeenViewed = cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_BEEN_VIEWED));

                listOfEntries.add(new SingleRSSEntry.Builder()
                        .channelTitle(channelTitle)
                        .channelImageURL(channelImageURL)
                        .channelDescription(channelDescription)
                        .itemLink(itemLink)
                        .itemTitle(itemTitle)
                        .itemDescription(itemDescription)
                        .itemPubDate(itemPubDate)
                        .itemBeenViewed(itemBeenViewed)
                        .build());
            }
            closeCursor();

            return listOfEntries;

        } else {
            closeCursor();
            throw new DatabaseIsEmptyException();
        }
    }

    public SingleRSSEntry readSingleEntry(String itemLink) throws SQLException, DatabaseIsEmptyException {
        //TODO: check for possible itemLink without array
        cursor = getCursor(new String[]{itemLink});
        if (cursor.moveToFirst()) {
            return new SingleRSSEntry.Builder()
                    .channelTitle(cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_CHANNEL_TITLE)))
                    .channelImageURL(cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_CHANNEL_IMAGE_URL)))
                    .channelDescription(cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_CHANNEL_DESCRIPTION)))
                    .itemLink(cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_ITEM_LINK)))
                    .itemTitle(cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_ITEM_TITLE)))
                    .itemDescription(cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_ITEM_DESCRIPTION)))
                    .itemPubDate(cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_ITEM_PUB_DATE)))
                    .itemBeenViewed(cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_BEEN_VIEWED)))
                    .build();
        } else {
            closeCursor();
            throw new DatabaseIsEmptyException();
        }


    }

    public Cursor getCursor(final String... selectionArgs) throws DatabaseIsEmptyException {

        if (selectionArgs == null) {
            final Cursor currentCursor = database.query(RSSEntryColumns.TABLE_NAME, COLUMNS_ALL, SELECTION_ALL, SELECTION_ARGS_ALL, GROUP_BY_ALL, HAVING_ALL, SORT_ORDER, LIMIT_ALL);
            if (currentCursor.moveToFirst()) {
                return currentCursor;
            } else {
                throw new DatabaseIsEmptyException();
            }
        } else {
            return database.query(RSSEntryColumns.TABLE_NAME, COLUMNS_ALL, SELECTION_BY_ITEM_LINK, selectionArgs, GROUP_BY_ALL, HAVING_ALL, SORT_ORDER, LIMIT_ALL);
        }

    }

    void closeCursor() {
        if (cursor != null) {
            cursor.close();
        }
    }

    public void close() {
        dbOpenHelper.close();
    }
}
