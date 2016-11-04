package yuriy.rssreader.service.controllers;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import yuriy.rssreader.data.RSSEntryColumns;
import yuriy.rssreader.data.SingleRSSEntry;
import yuriy.rssreader.rssexceptions.DatabaseIsEmptyException;

import java.util.ArrayList;

final class DuplicateChecker {
    private final static String[] CURSOR_ALL_ENTRIES = null;
    private final DBReader dbReader;

    DuplicateChecker(final Context context) throws SQLException {
        dbReader = new DBReader(context);
    }

    ArrayList<SingleRSSEntry> cropDuplicateEntries(final ArrayList<SingleRSSEntry> entriesList) {
        final Cursor cursor;
        try {
            cursor = dbReader.getCursor(CURSOR_ALL_ENTRIES);
        } catch (DatabaseIsEmptyException e) {
            return entriesList;
        }

        final ArrayList<SingleRSSEntry> croppedData = new ArrayList<>(entriesList);

        for (SingleRSSEntry entry : entriesList) {
            cursor.moveToFirst();
            while (true) {
                String dbEntryPubDate = cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_ITEM_PUB_DATE));
                String dbEntryItemLink = cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_ITEM_LINK));
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
        dbReader.closeCursor();
        dbReader.close();
        return croppedData;


    }
}
