package yuriy.rssreader.widget.services;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import yuriy.rssreader.R;
import yuriy.rssreader.database.SingleRSSEntry;

import java.util.ArrayList;

public final class ListWidgetService extends RemoteViewsService {
    public final static String NEW_ENTRIES = "yuriy.rssreader.widget.services.NEW_ENTRIES";
    private ArrayList<SingleRSSEntry> newEntries;

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        newEntries = intent.getParcelableArrayListExtra(NEW_ENTRIES);
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }

    final class ListRemoteViewsFactory extends RemoteViewsFactoryAdapter {
        private final Context context;

        ListRemoteViewsFactory(final Context context) {
            this.context = context;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.entry_in_list_widget);
            if (newEntries == null) {
                return remoteViews;
            }
            if(position<newEntries.size()) {
                final SingleRSSEntry newEntry = newEntries.get(position);
                remoteViews.setTextViewText(R.id.item_title_listView_entry_widget, newEntry.getItemTitle());
                remoteViews.setTextViewText(R.id.item_pubDate_listView_entry_widget, newEntry.getItemPubDate());
                remoteViews.setTextViewText(R.id.channel_title_listView_entry_widget, newEntry.getChannelTitle());
            }
            return remoteViews;
        }

        @Override
        public int getCount() {
            if (newEntries != null) {
                return newEntries.size();
            }
            return 0;
        }
    }
}

