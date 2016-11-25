package yuriy.rssreader.widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import yuriy.rssreader.MainActivity;
import yuriy.rssreader.R;
import yuriy.rssreader.widget.services.ListWidgetService;

public final class RssWidgetProvider extends AppWidgetProvider {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        super.onReceive(context, intent);


    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {

        for (int widgetId : appWidgetIds) {
            final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            final Intent serviceIntent = new Intent(context, ListWidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

            remoteViews.setRemoteAdapter(R.id.list_view_widget, serviceIntent);
            remoteViews.setEmptyView(R.id.list_view_widget, R.id.empty_text_view_widget);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);

            setOnRefreshButton(remoteViews, context);


        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void setOnRefreshButton(final RemoteViews remoteViews, final Context context) {
        if (remoteViews == null || context == null) {
            return;
        }
        /*final Intent intent = new Intent(context, DatabaseOperationService.class);
        intent.setAction(DatabaseOperationService.REQUEST_ENTRIES_ACTION);
        final PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);*/
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.refreshButton_widget, pendingIntent);
    }

}
