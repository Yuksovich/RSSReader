package yuriy.rssreader.widget.services;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import yuriy.rssreader.R;
import yuriy.rssreader.database.SingleRSSEntry;
import yuriy.rssreader.ui.entry_activity.SingleRssViewActivity;

import java.util.ArrayList;

import static yuriy.rssreader.MainActivity.ITEM_LINK;

public final class ListWidgetService extends RemoteViewsService {
    private static ArrayList<SingleRSSEntry> newEntries;

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
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
            if (position < newEntries.size()) {
                final SingleRSSEntry newEntry = newEntries.get(position);
                remoteViews.setTextViewText(R.id.item_title_listView_entry_widget, newEntry.getItemTitle());
                remoteViews.setTextViewText(R.id.item_pubDate_listView_entry_widget, newEntry.getItemPubDate());
                remoteViews.setTextViewText(R.id.channel_title_listView_entry_widget, newEntry.getChannelTitle());
                remoteViews.setOnClickFillInIntent(
                        R.id.rss_entry_widget,
                        new Intent(context, SingleRssViewActivity.class)
                                .putExtra(ITEM_LINK, newEntry.getItemLink()));
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

    public static void setEntries(final ArrayList<SingleRSSEntry> newEntries) {
        ListWidgetService.newEntries = newEntries;
    }

}

