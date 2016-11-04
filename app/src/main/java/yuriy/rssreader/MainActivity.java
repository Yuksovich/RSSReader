package yuriy.rssreader;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import yuriy.rssreader.service.ChannelSelectionPopup;
import yuriy.rssreader.service.ListViewDataProvider;
import yuriy.rssreader.service.RssListAdapter;
import yuriy.rssreader.service.RssProviderService;
import yuriy.rssreader.ui.AddNewUrlDialog;
import yuriy.rssreader.ui.SingleRssView;

public final class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private final static String DIALOG_NEW_URL = "dialogNewUrl";
    private static final String KEY_ITEM_LINK = "itemLink";

    private ListView listView;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case (ListViewDataProvider.STATE_SUCCESS):
                    final RssListAdapter adapter = (RssListAdapter) msg.obj;
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(MainActivity.this);
                    break;
                case (ListViewDataProvider.STATE_FAILURE):
                    break;
                case (ListViewDataProvider.STATE_EMPTY):
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

        setContentView(R.layout.activity_main);
        refreshList();
        final ImageButton refreshButton = (ImageButton)findViewById(R.id.refreshButton_toolbar);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshList();
            }
        });

        final ImageButton addNewUrlButton = (ImageButton)findViewById(R.id.addUrlButton_toolbar);
        addNewUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new AddNewUrlDialog();
                dialog.show(getFragmentManager(), DIALOG_NEW_URL);
            }
        });

        final ImageButton filterButton = (ImageButton)findViewById(R.id.filterButton_toolbar);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                showPopupMenu(view);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_acttivity_main, menu);
        return true;
    }

    private void refreshList(){
        new Thread(new ListViewDataProvider(this, handler)).start();
        listView = (ListView) findViewById(R.id.listOfEntries);
        listView.setFastScrollEnabled(true);
        Intent intent = new Intent(this, RssProviderService.class);
        startService(intent);
        listView.showContextMenu();
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        final TextView textView= (TextView)view.findViewById(R.id.item_link);
        final String itemLink = textView.getText().toString();
        Intent intent = new Intent(this, SingleRssView.class);
        intent.putExtra(KEY_ITEM_LINK, itemLink);
        startActivity(intent);
    }

    private void showPopupMenu(View view){
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.filter_popup);
        popupMenu.show();
        Menu menu = popupMenu.getMenu();
        ChannelSelectionPopup channelSelectionPopup = new ChannelSelectionPopup(this, menu);



    }

   }
