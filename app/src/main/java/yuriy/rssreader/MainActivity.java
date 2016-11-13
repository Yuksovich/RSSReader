package yuriy.rssreader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ListView;
import yuriy.rssreader.controllers.RssListAdapter;
import yuriy.rssreader.services.DatabaseOperationService;
import yuriy.rssreader.ui.activity_controllers.ListViewAdapterReceiverAndListener;
import yuriy.rssreader.ui.activity_controllers.MainActivityReceiverFilter;
import yuriy.rssreader.ui.activity_controllers.ToolbarListener;
import yuriy.rssreader.utils.StateSaver;

public final class MainActivity extends Activity {

    public static final String ITEM_LINK = "yuriy.rssreader.MainActivity.itemLink";
    private ListView listView;
    private LocalBroadcastManager broadcastManager;
    private ListViewAdapterReceiverAndListener receiver;
    private final IntentFilter intentFilter = MainActivityReceiverFilter.getInstance();
    private RssListAdapter adapter;
    private static int listVisiblePosition;
    private static int listPaddingTop;
    private static String currentChannelFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listVisiblePosition = StateSaver.getSavedPosition(this);
        listPaddingTop = StateSaver.getSavedPadding(this);
        currentChannelFilter = StateSaver.getChannelFilter(this);

        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list_view_main_activity);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        final ProgressDialog waitingDialog = new ProgressDialog(this);
        final ToolbarListener toolbarListener = new ToolbarListener(this, waitingDialog, listView);

        broadcastManager = LocalBroadcastManager.getInstance(this);
        receiver = new ListViewAdapterReceiverAndListener(this, listView, waitingDialog);

        findViewById(R.id.refreshButton_toolbar).setOnClickListener(toolbarListener);
        findViewById(R.id.addUrlButton_toolbar).setOnClickListener(toolbarListener);
        findViewById(R.id.filterButton_toolbar).setOnClickListener(toolbarListener);
        findViewById(R.id.settingsButton_toolbar).setOnClickListener(toolbarListener);
        findViewById(R.id.deleteButton_toolbar).setOnClickListener(toolbarListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        broadcastManager.registerReceiver(receiver, intentFilter);
        adapter = (RssListAdapter) listView.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        listView.setSelectionFromTop(listVisiblePosition, listPaddingTop);
        DatabaseOperationService.requestEntries(this, currentChannelFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        broadcastManager.unregisterReceiver(receiver);
        setListPosition();
        StateSaver.saveListPosition(this, listVisiblePosition);
        StateSaver.saveListPadding(this, listPaddingTop);
        StateSaver.saveChannelFilter(this, currentChannelFilter);
    }

    @Override
    public void onBackPressed() {
        adapter = (RssListAdapter) listView.getAdapter();
        if (adapter != null && adapter.isShowDeleteButton()) {
            adapter.setShowDeleteButton(false);
            adapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }

    public static void resetListPosition() {
        listPaddingTop = 0;
        listVisiblePosition = 0;
    }

    public static void setCurrentChannelFilter(final String channelFilter) {
        currentChannelFilter = channelFilter;
    }

    public static String getCurrentChannelFilter() {
        return currentChannelFilter;
    }

    private void setListPosition() {
        listVisiblePosition = listView.getFirstVisiblePosition();
        View view = listView.getChildAt(0);
        listPaddingTop = (view == null) ? 0 : (view.getTop() - listView.getPaddingTop());
    }

    public static int getListVisiblePosition() {
        return listVisiblePosition;
    }

    public static int getListPaddingTop() {
        return listPaddingTop;
    }
}
