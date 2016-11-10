package yuriy.rssreader.database;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import yuriy.rssreader.rssexceptions.DatabaseIsEmptyException;

import java.util.ArrayList;

final class DuplicateChecker {
    private final static String[] CURSOR_ALL_ENTRIES = null;

    private final Context context;

    DuplicateChecker(final Context context) throws SQLException {
        this.context = context;
    }

    ArrayList<SingleRSSEntry> cropDuplicateEntries(final ArrayList<SingleRSSEntry> entriesList) throws SQLException {

        Cursor cursor = null;
        DBReader dbReader = null;

        final ArrayList<SingleRSSEntry> croppedData = new ArrayList<>(entriesList);

        try {
            dbReader = new DBReader(context);
            cursor = dbReader.getCursor(CURSOR_ALL_ENTRIES);

            for (SingleRSSEntry entry : entriesList) {
                cursor.moveToFirst();
                while (true) {
                    String dbEntryPubDate = cursor.getString(cursor.getColumnIndex(TableColumns.COLUMN_NAME_ITEM_PUB_DATE));
                    String dbEntryItemLink = cursor.getString(cursor.getColumnIndex(TableColumns.COLUMN_NAME_ITEM_LINK));
                    if (entry.getItemLink().equals(dbEntryItemLink)) {
                        croppedData.remove(entry);
                        break;
                    }
                    if (entry.getItemPubDate().compareTo(dbEntryPubDate) > 0) {
                        break;
                    }
                    if (cursor.isLast()) {
                        break;
                    } else {
                        cursor.moveToNext();
                    }
                }
            }
            return croppedData;
        } catch (DatabaseIsEmptyException e) {
            return entriesList;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (dbReader != null) {
                dbReader.close();
            }
        }




    }
}
