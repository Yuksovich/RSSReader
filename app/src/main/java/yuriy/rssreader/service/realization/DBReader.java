package yuriy.rssreader.service.realization;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import yuriy.rssreader.data.ReaderContract.RSSEntry;
import yuriy.rssreader.data.RssDBOpenHelper;
import yuriy.rssreader.data.SingleRSSEntry;
import yuriy.rssreader.rssexceptions.DatabaseIsEmptyException;

import java.util.ArrayList;

public final class DBReader {
    private final ArrayList<SingleRSSEntry> listOfEntries;
    private final SQLiteDatabase database;
    private final RssDBOpenHelper dbOpenHelper;
    private Cursor cursor;

    private static final String WITHOUT_ARGUMENTS = null;

    private final static String SELECTION = RSSEntry.COLUMN_NAME_ITEM_LINK + " = ?";
    private final static String SORT_ORDER = RSSEntry.COLUMN_NAME_ITEM_PUB_DATE + " DESC";
    private final static String [] COLUMNS_ALL = null;
    private final static String SELECTION_ALL = null;
    private final static String [] SELECTION_ARGS_ALL = null;
    private final static String GROUP_BY_ALL = null;
    private final static String HAVING_ALL = null;
    private final static String LIMIT_ALL = null;

    public DBReader(final @NonNull Context context) throws SQLException {
        dbOpenHelper = new RssDBOpenHelper(context);
        listOfEntries = new ArrayList<>();
        database = dbOpenHelper.getReadableDatabase();
    }

    public ArrayList<SingleRSSEntry> read() throws SQLException, DatabaseIsEmptyException {
        return read(WITHOUT_ARGUMENTS);
    }

    public ArrayList<SingleRSSEntry> read(final String... selectionArgs) throws SQLException, DatabaseIsEmptyException {

        cursor = getCursor(selectionArgs);

        if (cursor.moveToFirst()) {
            for (cursor.moveToFirst(); cursor.isAfterLast(); cursor.moveToNext()) {

                listOfEntries.add(new SingleRSSEntry(cursor.getString(cursor.getColumnIndex(RSSEntry.COLUMN_NAME_CHANNEL_TITLE)),
                        cursor.getString(cursor.getColumnIndex(RSSEntry.COLUMN_NAME_CHANNEL_IMAGE_URL)),
                        cursor.getString(cursor.getColumnIndex(RSSEntry.COLUMN_NAME_CHANNEL_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(RSSEntry.COLUMN_NAME_ITEM_LINK)),
                        cursor.getString(cursor.getColumnIndex(RSSEntry.COLUMN_NAME_ITEM_TITLE)),
                        cursor.getString(cursor.getColumnIndex(RSSEntry.COLUMN_NAME_ITEM_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(RSSEntry.COLUMN_NAME_ITEM_PUB_DATE)),
                        cursor.getString(cursor.getColumnIndex(RSSEntry.COLUMN_NAME_BEEN_VIEWVED))));
            }
            closeCursor();

            return listOfEntries;

        } else {
            closeCursor();
            throw new DatabaseIsEmptyException();

        }
    }

    public Cursor getCursor(final String... selectionArgs) throws DatabaseIsEmptyException {

        if (selectionArgs == null) {
            final Cursor currentCursor = database.query(RSSEntry.TABLE_NAME, COLUMNS_ALL, SELECTION_ALL, SELECTION_ARGS_ALL, GROUP_BY_ALL, HAVING_ALL, SORT_ORDER, LIMIT_ALL);
            if (currentCursor.moveToFirst()) {
                return currentCursor;
            } else {
                throw new DatabaseIsEmptyException();
            }
        } else {
            return database.query(RSSEntry.TABLE_NAME, COLUMNS_ALL, SELECTION, selectionArgs, GROUP_BY_ALL, HAVING_ALL, SORT_ORDER, LIMIT_ALL);
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
