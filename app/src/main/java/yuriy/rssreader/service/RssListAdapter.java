package yuriy.rssreader.service;


import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import yuriy.rssreader.R;
import yuriy.rssreader.data.ReaderContract.RSSEntry;

public final class RssListAdapter extends CursorAdapter{

    RssListAdapter(final Context context, final Cursor cursor, final boolean autoRequery) {
        super(context, cursor, autoRequery);
        cursor.moveToFirst();
    }

    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return inflater.inflate(R.layout.entry_in_list_read, parent, false);

    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {


        final TextView itemTitle = (TextView) view.findViewById(R.id.item_title);
        final TextView channelTitle = (TextView) view.findViewById(R.id.channel_title);
        final TextView itemPubDate = (TextView) view.findViewById(R.id.item_pubDate);
        final TextView itemLink = (TextView) view.findViewById(R.id.item_link);


        itemTitle.setText(cursor.getString(cursor.getColumnIndex(RSSEntry.COLUMN_NAME_ITEM_TITLE)));
        channelTitle.setText(cursor.getString(cursor.getColumnIndex(RSSEntry.COLUMN_NAME_CHANNEL_TITLE)));
        itemPubDate.setText(cursor.getString(cursor.getColumnIndex(RSSEntry.COLUMN_NAME_ITEM_PUB_DATE)));
        itemLink.setText(cursor.getString(cursor.getColumnIndex(RSSEntry.COLUMN_NAME_ITEM_LINK)));

        final boolean itemWasRead = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(RSSEntry.COLUMN_NAME_BEEN_VIEWVED)));
        if (itemWasRead) {

            itemTitle.setTextColor(ColorStateList.valueOf(Color.BLACK));
            view.setBackgroundColor(Color.argb(0, 150, 150, 150));
        } else {
            itemTitle.setTextColor(ColorStateList.valueOf(Color.BLUE));
            view.setBackgroundColor(Color.argb(150, 150, 150, 150));
        }

    }



}
