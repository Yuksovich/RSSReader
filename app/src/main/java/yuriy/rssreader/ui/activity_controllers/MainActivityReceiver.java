package yuriy.rssreader.ui.activity_controllers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import yuriy.rssreader.MainActivity;
import yuriy.rssreader.R;
import yuriy.rssreader.controllers.RssListAdapter;
import yuriy.rssreader.database.SingleRSSEntry;
import yuriy.rssreader.services.DatabaseOperationService;
import yuriy.rssreader.ui.SingleRssView;
import yuriy.rssreader.utils.ShortToast;

import java.util.ArrayList;

import static yuriy.rssreader.MainActivity.ITEM_LINK;

public final class MainActivityReceiver extends BroadcastReceiver implements AdapterView.OnItemClickListener {

    private final ListView listView;
    private final ProgressDialog waitingDialog;
    private RssListAdapter adapter;
    private final Context mainActivityContext;

    public MainActivityReceiver(final Context mainActivityContext,
                                final ListView listView,
                                final ProgressDialog waitingDialog) {
        this.mainActivityContext = mainActivityContext;
        this.listView = listView;
        this.waitingDialog = waitingDialog;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        listView.setFastScrollEnabled(true);
        listView.setOnItemClickListener(this);
        waitingDialog.dismiss();
        final String action = intent.getAction();
        final String message = intent.getStringExtra(action);
        final TextView emptyText = (TextView) ((Activity) mainActivityContext).findViewById(R.id.database_is_empty_message_screen);
        switch (action) {
            case (DatabaseOperationService.FAIL):
                ShortToast.makeText(context, message);
                break;
            case (DatabaseOperationService.DATABASE_EMPTY):
                ShortToast.makeText(context, message);
                listView.setVisibility(View.INVISIBLE);
                emptyText.setVisibility(View.VISIBLE);
                break;
            case (DatabaseOperationService.SUCCESS):
                ShortToast.makeText(context, message);
                MainActivity.resetListPosition();
                break;
            case (DatabaseOperationService.ON_DATA_RECEIVED):
                final ArrayList<SingleRSSEntry> entriesList
                        = intent.getParcelableArrayListExtra(DatabaseOperationService.DATA);
                if (entriesList == null) {
                    return;
                }
                adapter = new RssListAdapter(mainActivityContext, entriesList);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
                listView.setSelectionFromTop(MainActivity.getListVisiblePosition(), MainActivity.getListPaddingTop());
                listView.setVisibility(View.VISIBLE);
                emptyText.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        if (adapter == null) {
            return;
        }
        adapter.getItem(position).setBeenViewed();
        final String itemLink = adapter.getItem(position).getItemLink();
        final Intent intent = new Intent(mainActivityContext, SingleRssView.class);
        intent.putExtra(ITEM_LINK, itemLink);
        mainActivityContext.startActivity(intent);
    }

}
