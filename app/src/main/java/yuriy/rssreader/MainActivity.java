package yuriy.rssreader;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import yuriy.rssreader.service.DataProvider;
import yuriy.rssreader.service.RssListAdapter;

public final class MainActivity extends AppCompatActivity {

    private ListView listView;
    private DataProvider data;
    private RssListAdapter adapter;

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
    }

    public void refresh(View v){
        refreshList();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void refreshList() {


        new Thread(new DataProvider(this, handler)).start();

        listView = (ListView) findViewById(R.id.listOfEntries);
        listView.setFastScrollEnabled(true);
    }
}
