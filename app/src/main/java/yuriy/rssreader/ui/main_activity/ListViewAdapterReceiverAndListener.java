package yuriy.rssreader.ui.main_activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import yuriy.rssreader.MainActivity;
import yuriy.rssreader.R;
import yuriy.rssreader.controllers.RssEntriesListAdapter;
import yuriy.rssreader.database.SingleRSSEntry;
import yuriy.rssreader.services.DatabaseOperationService;
import yuriy.rssreader.ui.entry_activity.SingleRssViewActivity;
import yuriy.rssreader.ui.entry_activity.SingleViewFragment;
import yuriy.rssreader.utils.ShortToast;
import yuriy.rssreader.utils.StateSaver;

import java.util.ArrayList;

import static yuriy.rssreader.MainActivity.ITEM_LINK;

public final class ListViewAdapterReceiverAndListener
        extends BroadcastReceiver
        implements AdapterView.OnItemClickListener {

    private static final String EMPTY_STRING = "";
    private final ListView listView;
    private final ProgressDialog waitingDialog;
    private final Activity mainActivityContext;
    private RssEntriesListAdapter adapter;
    private TextView emptyText;

    public ListViewAdapterReceiverAndListener(final Activity mainActivityContext,
                                              final ListView listView,
                                              final ProgressDialog waitingDialog) {
        this.mainActivityContext = mainActivityContext;
        this.listView = listView;
        this.waitingDialog = waitingDialog;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (listView == null || mainActivityContext == null) {
            return;
        }
        removeUpdateSigns();

        listView.setFastScrollEnabled(true);
        listView.setOnItemClickListener(this);
        final String action = intent.getAction();
        String message = intent.getStringExtra(action);
        if (message == null) {
            message = EMPTY_STRING;
        }
        if (action == null) {
            return;
        }
        emptyText = (TextView) mainActivityContext.findViewById(R.id.database_is_empty_message_screen);

        switch (action) {
            case (DatabaseOperationService.FAIL):
                ShortToast.makeText(context, message);
                break;
            case (DatabaseOperationService.DATABASE_EMPTY):
                ShortToast.makeText(context, message);
                listView.setVisibility(View.INVISIBLE);
                if (emptyText != null) {
                    emptyText.setVisibility(View.VISIBLE);
                }
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
                assignDataToList(entriesList);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        openEntry(position);
    }

    private void removeUpdateSigns() {
        final SwipeRefreshLayout swipeRefreshLayout =
                (SwipeRefreshLayout) mainActivityContext.findViewById(R.id.list_view_swipe_refresh);
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (waitingDialog != null) {
            waitingDialog.dismiss();
        }
    }

    private void assignDataToList(final ArrayList<SingleRSSEntry> entriesList) {

        adapter = new RssEntriesListAdapter(mainActivityContext, entriesList);
        listView.setAdapter(adapter);
        listView.setSelectionFromTop(MainActivity.getListVisiblePosition(), MainActivity.getListPaddingTop());
        listView.setVisibility(View.VISIBLE);
        if (emptyText != null) {
            emptyText.setVisibility(View.INVISIBLE);
        }
        final String savedLink = StateSaver.getSavedLink(mainActivityContext);
        if (!StateSaver.NO_LINK.equals(savedLink)) {
           openEntry(savedLink);
        }
    }

    private void openEntry(final int position) {
        if (adapter == null) {
            return;
        }
        adapter.getItem(position).setBeenViewed();
        adapter.notifyDataSetChanged();
        final String itemLink = adapter.getItem(position).getItemLink();
        openEntry(itemLink);
    }

    private void openEntry(final String itemLink) {

        if (mainActivityContext.findViewById(R.id.entry_view_fragment) != null) {
            final SingleViewFragment entryFragment = SingleViewFragment.getInstance(itemLink);
            final FragmentTransaction fragmentTransaction =
                    mainActivityContext.getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.entry_view_fragment, entryFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();

        } else {
            final Intent intent = new Intent(mainActivityContext, SingleRssViewActivity.class);
            intent.putExtra(ITEM_LINK, itemLink);
            mainActivityContext.startActivity(intent);
        }
    }

}
