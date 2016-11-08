package yuriy.rssreader.services.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ListView;
import yuriy.rssreader.controllers.RssListAdapter;
import yuriy.rssreader.database.SingleRSSEntry;
import yuriy.rssreader.services.DatabaseRefresherService;
import yuriy.rssreader.utils.ShortToast;

import java.util.ArrayList;

public final class DataBaseReceiver extends BroadcastReceiver {
    private final ListView listView;


    public DataBaseReceiver(final ListView listView) {
        this.listView = listView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        final String message = intent.getStringExtra(action);
        switch (action) {
            case (DatabaseRefresherService.FAIL):
                ShortToast.makeText(context, message);
                break;
            case (DatabaseRefresherService.SUCCESS):
                ShortToast.makeText(context, message);
                break;
            case (DatabaseRefresherService.ON_DATA_RECEIVED):
                final ArrayList<SingleRSSEntry> entriesList = intent.getParcelableArrayListExtra(DatabaseRefresherService.DATA);
                if (entriesList == null) {
                    return;
                }

                RssListAdapter adapter = new RssListAdapter(context, entriesList);
                listView.setAdapter(adapter);
                break;
            default:
                break;
        }
    }

}
