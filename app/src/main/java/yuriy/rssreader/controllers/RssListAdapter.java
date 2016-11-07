package yuriy.rssreader.controllers;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import yuriy.rssreader.R;
import yuriy.rssreader.database.RSSEntryColumns;

public final class RssListAdapter extends CursorAdapter {

    RssListAdapter(final Context context, final Cursor cursor, final boolean autoRequery) {
        super(context, cursor, autoRequery);
        cursor.moveToFirst();
    }


    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {

        final LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.entry_in_list, parent, false);

    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {


        final TextView itemTitle = (TextView) view.findViewById(R.id.item_title);
        final TextView channelTitle = (TextView) view.findViewById(R.id.channel_title);
        final TextView itemPubDate = (TextView) view.findViewById(R.id.item_pubDate);
        final TextView itemLink = (TextView) view.findViewById(R.id.item_link);


        itemTitle.setText(cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_ITEM_TITLE)));
        channelTitle.setText(cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_CHANNEL_TITLE)));
        itemPubDate.setText(cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_ITEM_PUB_DATE)));
        itemLink.setText(cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_ITEM_LINK)));

        final boolean itemWasRead = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(RSSEntryColumns.COLUMN_NAME_BEEN_VIEWED)));

        if (itemWasRead) {

            itemTitle.setTextColor(context.getResources().getColor(R.color.itemTitleWasRead));
            view.setBackgroundColor(context.getResources().getColor(R.color.itemBackgroundWasRead));
        } else {
            itemTitle.setTextColor(context.getResources().getColor(R.color.itemTitleWasNotRead));
            view.setBackgroundColor(context.getResources().getColor(R.color.item_background_not_read));
        }

    }


}