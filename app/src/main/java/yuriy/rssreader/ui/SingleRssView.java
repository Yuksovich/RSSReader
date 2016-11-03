package yuriy.rssreader.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
import yuriy.rssreader.R;
import yuriy.rssreader.data.SingleRSSEntry;
import yuriy.rssreader.service.realization.DBReader;

public class SingleRssView extends AppCompatActivity {

    private static final String KEY_ITEM_LINK = "itemLink";
    private static final String MIME_TYPE = "text/html";
    private static final String ENCODING = "UTF-8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_rss_view);
        final String itemLink = (String)getIntent().getExtras().get(KEY_ITEM_LINK);
        SingleRSSEntry entry = findEntryByItemLink(itemLink);
        if(entry!=null) {
            ((TextView)findViewById(R.id.item_title_show)).setText(entry.getItemTitle());
            ((TextView)findViewById(R.id.item_pubDate_show)).setText(entry.getItemPubDate());
            ((TextView)findViewById(R.id.channel_title_show)).setText(entry.getChannelTitle());
            ((TextView)findViewById(R.id.channel_description_show)).setText(entry.getChannelDescription());
            WebView webView = (WebView)findViewById(R.id.item_descriprion_show);

            final String HTML_STYLE = getString(R.string.html_style_css);
            webView.loadDataWithBaseURL(null, HTML_STYLE+entry.getItemDescription(), MIME_TYPE, ENCODING, null);

        }
    }

    private SingleRSSEntry findEntryByItemLink(final String itemLink){
        DBReader dbReader = new DBReader(this);
        try {
            return dbReader.readSingleEntry(itemLink);

        }catch (Exception e){
            Toast.makeText(this, getText(R.string.sqlFail), Toast.LENGTH_SHORT).show();
            this.finish();
        }finally {
            dbReader.close();

        }
        return null;
    }
}