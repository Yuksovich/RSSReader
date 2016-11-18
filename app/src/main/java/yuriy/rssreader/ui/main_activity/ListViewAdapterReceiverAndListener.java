package yuriy.rssreader.ui.main_activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import yuriy.rssreader.MainActivity;
import yuriy.rssreader.R;
import yuriy.rssreader.controllers.RssListAdapter;
import yuriy.rssreader.database.SingleRSSEntry;
import yuriy.rssreader.services.DatabaseOperationService;
import yuriy.rssreader.ui.entry_activity.SingleRssView;
import yuriy.rssreader.utils.ShortToast;
import yuriy.rssreader.utils.StateSaver;

import java.util.ArrayList;

import static yuriy.rssreader.MainActivity.ITEM_LINK;

public final class ListViewAdapterReceiverAndListener
        extends BroadcastReceiver
        implements AdapterView.OnItemClickListener {

    private final ListView listView;
    private final ProgressDialog waitingDialog;
    private RssListAdapter adapter;
    private final Context mainActivityContext;

    public ListViewAdapterReceiverAndListener(final Context mainActivityContext,
                                              final ListView listView,
                                              final ProgressDialog waitingDialog) {
        this.mainActivityContext = mainActivityContext;
        this.listView = listView;
        this.waitingDialog = waitingDialog;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (listView == null||mainActivityContext==null) {
            return;
        }
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) listView.getParent();
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        listView.setFastScrollEnabled(true);
        listView.setOnItemClickListener(this);
        if (waitingDialog != null) {
            waitingDialog.dismiss();
        }
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

                final String savedLink = StateSaver.getSavedLink(context);
                if (!StateSaver.NO_LINK.equals(savedLink)) {
                    final Intent resumeIntent = new Intent(mainActivityContext, SingleRssView.class);
                    resumeIntent.putExtra(ITEM_LINK, savedLink);
                    StateSaver.resetLink(mainActivityContext);
                    mainActivityContext.startActivity(resumeIntent);
                }
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
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        adapter.getItem(position).setBeenViewed();
        final String itemLink = adapter.getItem(position).getItemLink();
        final Intent intent = new Intent(mainActivityContext, SingleRssView.class);
        intent.putExtra(ITEM_LINK, itemLink);
        mainActivityContext.startActivity(intent);
    }

}
