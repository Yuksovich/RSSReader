package yuriy.rssreader.widget;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import yuriy.rssreader.R;
import yuriy.rssreader.database.SingleRSSEntry;
import yuriy.rssreader.widget.services.ListWidgetService;

import java.util.ArrayList;

public final class RssWidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {

        updateWidget(context, null);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    static void updateWidget(final Context context, @Nullable final ArrayList<SingleRSSEntry> newEntries) {

        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        final Intent serviceIntent = new Intent(context, ListWidgetService.class);
        if (newEntries != null) {
            serviceIntent.putParcelableArrayListExtra(ListWidgetService.NEW_ENTRIES, newEntries);
        }
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(R.id.list_view_widget, serviceIntent);
        remoteViews.setEmptyView(R.id.list_view_widget, R.id.empty_text_view_widget);
        final AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);

        widgetManager.updateAppWidget(new ComponentName(
                context.getPackageName(),
                RssWidgetProvider.class.getName()),
                remoteViews);
    }

}
