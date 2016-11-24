package yuriy.rssreader.widget.services;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import yuriy.rssreader.R;

public final class ListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

final class ListRemoteViewsFactory extends RemoteViewsFactoryAdapter {
    private final Context context;
    private final Intent intent;

    ListRemoteViewsFactory(final Context context, final Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.entry_in_list_widget);

        remoteViews.setTextViewText(R.id.item_title_listView_entry_widget, position+"");
        remoteViews.setTextViewText(R.id.item_pubDate_listView_entry_widget, "date here");
        remoteViews.setTextViewText(R.id.channel_title_listView_entry_widget, "channel title");

        return remoteViews;
    }

    @Override
    public int getCount() {
        return 3;
    }
}

