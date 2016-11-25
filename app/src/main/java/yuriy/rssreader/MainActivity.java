package yuriy.rssreader;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import yuriy.rssreader.controllers.RssEntriesListAdapter;
import yuriy.rssreader.services.DatabaseOperationService;
import yuriy.rssreader.ui.main_activity.AddNewUrlDialog;
import yuriy.rssreader.ui.main_activity.ListViewAdapterController;
import yuriy.rssreader.ui.main_activity.MainActivityReceiverFilter;
import yuriy.rssreader.ui.main_activity.ToolbarController;
import yuriy.rssreader.utils.StateSaver;
import yuriy.rssreader.utils.Theme;

public final class MainActivity extends Activity {

    public static final String ITEM_LINK = "yuriy.rssreader.MainActivity.itemLink";
    private static final int DIALOG_THEME = 0;

    private static final String LINK_ACTION = "android.intent.action.VIEW";

    private int listVisiblePosition;
    private int listPaddingTop;
    private String currentChannelFilter;
    private ListView listView;
    private LocalBroadcastManager broadcastManager;
    private ListViewAdapterController receiver;
    private RssEntriesListAdapter adapter;
    private final IntentFilter intentFilter = MainActivityReceiverFilter.getInstance();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        if (Theme.getId() == 0) {
            Theme.setThemeFromSharedPreferences(this);
            setTheme(Theme.getId());
        } else {
            setTheme(Theme.getId());
        }
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        if (LINK_ACTION.equals(intent.getAction())) {
            final String url = intent.getData().toString();
            if (url != null) {
                final DialogFragment dialog = new AddNewUrlDialog();
                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, DIALOG_THEME);
                dialog.show(getFragmentManager(), url);
            }
        }

        listVisiblePosition = StateSaver.getSavedPosition(this);
        listPaddingTop = StateSaver.getSavedPadding(this);
        currentChannelFilter = StateSaver.getChannelFilter(this);

        setContentView(R.layout.activity_main);
        listView = ((ListFragment) getFragmentManager().findFragmentById(R.id.entries_list_fragment))
                .getListView();

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        final ToolbarController toolbarController = new ToolbarController(this, listView);

        broadcastManager = LocalBroadcastManager.getInstance(this);
        receiver = new ListViewAdapterController(this, listView);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.list_view_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(toolbarController);

        findViewById(R.id.refreshButton_toolbar).setOnClickListener(toolbarController);
        findViewById(R.id.addUrlButton_toolbar).setOnClickListener(toolbarController);
        findViewById(R.id.filterButton_toolbar).setOnClickListener(toolbarController);
        findViewById(R.id.settingsButton_toolbar).setOnClickListener(toolbarController);
        findViewById(R.id.deleteButton_toolbar).setOnClickListener(toolbarController);
    }

    @Override
    protected void onResume() {
        super.onResume();
        broadcastManager.registerReceiver(receiver, intentFilter);
        adapter = (RssEntriesListAdapter) listView.getAdapter();
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
        adapter = (RssEntriesListAdapter) listView.getAdapter();
        if (adapter != null && adapter.isShowDeleteButton()) {
            adapter.setShowDeleteButton(false);
            adapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }

    private void setListPosition() {
        listVisiblePosition = listView.getFirstVisiblePosition();
        View view = listView.getChildAt(0);
        listPaddingTop = (view == null) ? 0 : (view.getTop() - listView.getPaddingTop());
    }

}
