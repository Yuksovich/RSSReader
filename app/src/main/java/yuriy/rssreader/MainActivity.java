package yuriy.rssreader;


import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import yuriy.rssreader.service.DataProvider;
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
                case (DataProvider.STATE_SUCCESS):
                    final RssListAdapter adapter = (RssListAdapter) msg.obj;
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(MainActivity.this);
                    break;
                case (DataProvider.STATE_FAILURE):
                    break;
                case (DataProvider.STATE_EMPTY):
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_acttivity_main, menu);
        return true;
    }

    private void refreshList(){
        new Thread(new DataProvider(this, handler)).start();
        listView = (ListView) findViewById(R.id.listOfEntries);
        listView.setFastScrollEnabled(true);
        Intent intent = new Intent(this, RssProviderService.class);
        startService(intent);
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        final TextView textView= (TextView)view.findViewById(R.id.item_link);
        final String itemLink = textView.getText().toString();
        Intent intent = new Intent(this, SingleRssView.class);
        intent.putExtra(KEY_ITEM_LINK, itemLink);
        startActivity(intent);
    }

   }
