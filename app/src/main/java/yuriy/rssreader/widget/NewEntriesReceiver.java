package yuriy.rssreader.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import yuriy.rssreader.database.SingleRSSEntry;

import java.util.ArrayList;


public class NewEntriesReceiver extends BroadcastReceiver {

    public final static String WIDGET_NEW_ENTRIES = "yuriy.rssreader.widget.WIDGET_NEW_ENTRIES";

    public NewEntriesReceiver() {
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (WIDGET_NEW_ENTRIES.equals(intent.getAction())) {
            final ArrayList<SingleRSSEntry> newEntries = intent.getParcelableArrayListExtra(WIDGET_NEW_ENTRIES);
            RssWidgetProvider.updateWidget(context, newEntries);
        }
    }
}
