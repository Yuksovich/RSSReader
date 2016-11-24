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
    final Context context;
    final Intent intent;

    final String[] list = {"one", "two", "three"};

    ListRemoteViewsFactory(final Context context, final Intent intent) {
        this.context = context;
        this.intent = intent;
    }


    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.entry_in_list);


            remoteViews.setTextViewText(R.id.item_title_listView_entry, position + "Hurray");

        return remoteViews;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
}

