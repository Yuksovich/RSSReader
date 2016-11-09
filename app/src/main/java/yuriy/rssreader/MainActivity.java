package yuriy.rssreader;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import yuriy.rssreader.controllers.ChannelSelectionPopup;
import yuriy.rssreader.controllers.RssListAdapter;
import yuriy.rssreader.database.SingleRSSEntry;
import yuriy.rssreader.services.DatabaseOperationService;
import yuriy.rssreader.ui.AddNewUrlDialog;
import yuriy.rssreader.ui.SettingsActivity;
import yuriy.rssreader.ui.SingleRssView;
import yuriy.rssreader.utils.ShortToast;

import java.util.ArrayList;

public final class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    private final static String DIALOG_NEW_URL = "dialogNewUrl";
    public static final String ITEM_LINK = "yuriy.rssreader.MainActivity.itemLink";
    private final static String KEY_TO_LIST_POSITION = "yuriy.rssreader.MainActivity.KEY_TO_LIST_POSITION";
    private static final String KEY_TO_LIST_PADDING = "yuriy.rssreader.MainActivity.KEY_TO_LIST_PADDING";
    private static final int DIALOG_THEME = 0;
    private static final boolean NOTIFY_IF_NOTHING_NEW = true;

    private ListView listView;
    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver receiver;
    private final IntentFilter intentFilter = new IntentFilter();
    private ArrayList<SingleRSSEntry> entriesList;
    private RssListAdapter adapter = null;
    private int listVisiblePosition=0;
    private int listPaddingTop=0;

    public MainActivity() {
        super();
        intentFilter.addAction(DatabaseOperationService.SUCCESS);
        intentFilter.addAction(DatabaseOperationService.FAIL);
        intentFilter.addAction(DatabaseOperationService.ON_DATA_RECEIVED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            listVisiblePosition = savedInstanceState.getInt(KEY_TO_LIST_POSITION);
            listPaddingTop = savedInstanceState.getInt(KEY_TO_LIST_PADDING);
        }

        broadcastManager = LocalBroadcastManager.getInstance(this);
        DatabaseOperationService.requestEntries(this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list_view_main_activity);
        listView.setFastScrollEnabled(true);
        listView.setOnItemClickListener(this);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                final String message = intent.getStringExtra(action);
                switch (action) {
                    case (DatabaseOperationService.FAIL):
                        ShortToast.makeText(context, message);
                        break;
                    case (DatabaseOperationService.SUCCESS):
                        ShortToast.makeText(context, message);
                        break;
                    case (DatabaseOperationService.ON_DATA_RECEIVED):
                        entriesList = intent.getParcelableArrayListExtra(DatabaseOperationService.DATA);
                        if (entriesList == null) {
                            return;
                        }
                        adapter = new RssListAdapter(MainActivity.this, entriesList);
                        listView.setAdapter(adapter);
                        listView.setSelectionFromTop(listVisiblePosition, listPaddingTop);
                        break;
                    default:
                        break;
                }
            }
        };

        final ImageButton refreshButton = (ImageButton) findViewById(R.id.refreshButton_toolbar);
        refreshButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatabaseOperationService.refreshDatabase(MainActivity.this, NOTIFY_IF_NOTHING_NEW);
                DatabaseOperationService.requestEntries(MainActivity.this);
            }
        });

        final ImageButton addNewUrlButton = (ImageButton) findViewById(R.id.addUrlButton_toolbar);
        addNewUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new AddNewUrlDialog();
                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, DIALOG_THEME);
                dialog.show(getFragmentManager(), DIALOG_NEW_URL);
            }
        });

        final ImageButton filterButton = (ImageButton) findViewById(R.id.filterButton_toolbar);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                showPopupMenu(view);
            }
        });

        final ImageButton settingsButton = (ImageButton) findViewById(R.id.settingsButton_toolbar);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
    }

    /*@Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }*/

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        adapter.getItem(position).setBeenViewed();

        final String itemLink = adapter.getItem(position).getItemLink();
        final Intent intent = new Intent(this, SingleRssView.class);
        intent.putExtra(ITEM_LINK, itemLink);
        startActivity(intent);
    }

    private void showPopupMenu(final View view) {
        final PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.filter_popup);
        popupMenu.show();
        Menu menu = popupMenu.getMenu();
        ChannelSelectionPopup channelSelectionPopup = new ChannelSelectionPopup(this, menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        broadcastManager.registerReceiver(receiver, intentFilter);

        if (adapter != null) {
            listView.setAdapter(adapter);
        }
        listView.setSelectionFromTop(listVisiblePosition, listPaddingTop);

    }

    @Override
    protected void onPause() {
        super.onPause();
        broadcastManager.unregisterReceiver(receiver);
        listVisiblePosition = listView.getFirstVisiblePosition();
        View view = listView.getChildAt(0);
        listPaddingTop = (view == null) ? 0 : (view.getTop() - listView.getPaddingTop());

    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_TO_LIST_POSITION, listVisiblePosition);
        outState.putInt(KEY_TO_LIST_PADDING, listPaddingTop);
    }
}
