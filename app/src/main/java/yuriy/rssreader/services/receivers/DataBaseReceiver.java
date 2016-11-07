package yuriy.rssreader.services.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import yuriy.rssreader.database.SingleRSSEntry;
import yuriy.rssreader.services.DatabaseRefresherService;

import java.util.ArrayList;

public final class DataBaseReceiver extends BroadcastReceiver {
    private ArrayList<SingleRSSEntry> entriesList = null;

    public DataBaseReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        final String message = intent.getStringExtra(action);
        switch (action) {
            case (DatabaseRefresherService.FAIL):
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                break;
            case (DatabaseRefresherService.SUCCESS):
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                break;
            case (DatabaseRefresherService.ON_DATA_RECEIVED):
                entriesList = intent.getParcelableArrayListExtra(DatabaseRefresherService.DATA);
                break;
            default:
                break;
        }
    }

    public ArrayList<SingleRSSEntry> getEntriesList() {
        return entriesList;
    }
}
