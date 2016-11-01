package yuriy.rssreader;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import yuriy.rssreader.service.DataProvider;
import yuriy.rssreader.service.RssListAdapter;
import yuriy.rssreader.service.RssProviderService;

public final class MainActivity extends AppCompatActivity {

    private ListView listView;
    private DataProvider data;
    private RssListAdapter adapter;
    private Button refreshButton;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case (DataProvider.STATE_SUCCESS):
                    adapter = (RssListAdapter) msg.obj;
                    listView.setAdapter(adapter);
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
        setContentView(R.layout.activity_main);
        refreshList();
        refreshButton = (Button)findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshList();
            }
        });
    }

    private void refreshList(){
        new Thread(new DataProvider(this, handler)).start();
        listView = (ListView) findViewById(R.id.listOfEntries);
        listView.setFastScrollEnabled(true);
        Intent intent = new Intent(this, RssProviderService.class);
        startService(intent);
    }


}
