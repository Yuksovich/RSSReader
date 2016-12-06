package yuriy.rssreader.widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import yuriy.rssreader.R;
import yuriy.rssreader.services.DatabaseOperationService;
import yuriy.rssreader.ui.entry_activity.SingleRssViewActivity;
import yuriy.rssreader.widget.services.ListWidgetService;

import static yuriy.rssreader.services.DatabaseOperationService.REFRESH_DATABASE_ACTION;

public final class RssWidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {

        updateWidget(context);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    static void updateWidget(final Context context) {

        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        setEntriesClickable(context, remoteViews);
        setRefreshButton(context, remoteViews);

        final Intent serviceIntent = new Intent(context, ListWidgetService.class);

        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(R.id.list_view_widget, serviceIntent);
        remoteViews.setEmptyView(R.id.list_view_widget, R.id.empty_text_view_widget);

        final AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        final ComponentName componentName = new ComponentName(context, RssWidgetProvider.class);

        final int[] ids = widgetManager.getAppWidgetIds(componentName);
        widgetManager.notifyAppWidgetViewDataChanged(ids, R.id.list_view_widget);
        widgetManager.updateAppWidget(componentName, remoteViews);
    }

    private static void setRefreshButton(final Context context, final RemoteViews remoteViews) {

        final Intent intent = new Intent(context, DatabaseOperationService.class);
        intent.setAction(REFRESH_DATABASE_ACTION);
        final PendingIntent pendingIntent =
                PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.refreshButton_widget, pendingIntent);
    }

    private static void setEntriesClickable(final Context context, final RemoteViews remoteViews){
        final Intent intent = new Intent(context, SingleRssViewActivity.class);
        final PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.list_view_widget, pendingIntent);
    }

}
