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
import android.widget.*;
import yuriy.rssreader.controllers.ChannelSelectionPopup;
import yuriy.rssreader.controllers.RssListAdapter;
import yuriy.rssreader.database.SingleRSSEntry;
import yuriy.rssreader.services.DatabaseRefresherService;
import yuriy.rssreader.ui.AddNewUrlDialog;
import yuriy.rssreader.ui.SettingsActivity;
import yuriy.rssreader.ui.SingleRssView;
import yuriy.rssreader.utils.ShortToast;

import java.util.ArrayList;

public final class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    private final static String DIALOG_NEW_URL = "dialogNewUrl";
    private static final String KEY_ITEM_LINK = "itemLink";
    private static final int DIALOG_THEME = 0;
    private ListView listView;
    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver receiver;
    private final IntentFilter intentFilter = new IntentFilter();

    public MainActivity(){
        super();
        intentFilter.addAction(DatabaseRefresherService.SUCCESS);
        intentFilter.addAction(DatabaseRefresherService.FAIL);
        intentFilter.addAction(DatabaseRefresherService.ON_DATA_RECEIVED);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        broadcastManager = LocalBroadcastManager.getInstance(this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list_view_main_activity);

        receiver = new BroadcastReceiver() {
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
        };



        DatabaseRefresherService.requestEntries(this);

        final ImageButton refreshButton = (ImageButton) findViewById(R.id.refreshButton_toolbar);
        refreshButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatabaseRefresherService.refreshDatabase(MainActivity.this);
                DatabaseRefresherService.requestEntries(MainActivity.this);
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
        getMenuInflater().inflate(R.menu.menu_acttivity_main, menu);
        return true;
    }*/


    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        final TextView textView = (TextView) view.findViewById(R.id.item_link_listView_entry);
        final String itemLink = textView.getText().toString();
        Intent intent = new Intent(this, SingleRssView.class);
        intent.putExtra(KEY_ITEM_LINK, itemLink);
        startActivity(intent);
    }

    private void showPopupMenu(final View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.filter_popup);
        popupMenu.show();
        Menu menu = popupMenu.getMenu();
        ChannelSelectionPopup channelSelectionPopup = new ChannelSelectionPopup(this, menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        broadcastManager.registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        broadcastManager.unregisterReceiver(receiver);
    }
}
