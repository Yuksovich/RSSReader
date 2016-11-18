package yuriy.rssreader.ui.entry_activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.webkit.WebView;
import android.widget.TextView;
import yuriy.rssreader.R;
import yuriy.rssreader.services.DatabaseOperationService;
import yuriy.rssreader.services.SingleEntryOperationService;
import yuriy.rssreader.utils.EntrySerializer;
import yuriy.rssreader.utils.StateSaver;
import yuriy.rssreader.utils.Theme;

import static yuriy.rssreader.MainActivity.ITEM_LINK;
import static yuriy.rssreader.services.SingleEntryOperationService.SINGLE_ENTRY;

public final class SingleRssView extends Activity {

    private static final String MIME_TYPE = "text/html";
    private static final String ENCODING = "UTF-8";
    private String itemLink;
    private BroadcastReceiver receiver;
    private final IntentFilter intentFilter = new IntentFilter(SINGLE_ENTRY);
    private LocalBroadcastManager broadcastManager;
    private boolean exitActivityFlag = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        if(Theme.getId()!=0){
            setTheme(Theme.getId());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_rss_view_activity);

        broadcastManager = LocalBroadcastManager.getInstance(this);

        itemLink = (String) getIntent().getExtras().get(ITEM_LINK);
        if (itemLink == null) {
            finish();
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {

                final String HTML_STYLE = getString(R.string.html_style_css_beginning)+Theme.getHtmlStyleCssMiddle(context)+getString(R.string.html_style_css_ending);
                final String READ_MORE_STRING = getString(R.string.html_read_more_link);
                final EntrySerializer.SerializableEntry entry = (EntrySerializer.SerializableEntry) intent.getSerializableExtra(SINGLE_ENTRY);
                if (entry != null) {
                    ((TextView) findViewById(R.id.item_title_show)).setText(entry.getItemTitle());
                    ((TextView) findViewById(R.id.item_pubDate_show)).setText(entry.getItemPubDate());
                    ((TextView) findViewById(R.id.channel_title_show)).setText(entry.getChannelTitle());
                    ((TextView) findViewById(R.id.channel_description_show)).setText(entry.getChannelDescription());
                    WebView webView = (WebView) findViewById(R.id.item_description_show);

                    final String readMoreLink = String.format(READ_MORE_STRING, itemLink);
                    webView.loadDataWithBaseURL(null, HTML_STYLE + entry.getItemDescription() + readMoreLink, MIME_TYPE, ENCODING, null);

                    if (!entry.isItemBeenViewed()) {
                        DatabaseOperationService.setEntryBeenViewed(SingleRssView.this, itemLink);
                    }
                } else {
                    finish();
                }
            }
        };
        broadcastManager.registerReceiver(receiver, intentFilter);
        SingleEntryOperationService.singleEntryRequest(this, itemLink);
    }

    @Override
    protected void onPause() {
        super.onPause();
        broadcastManager.unregisterReceiver(receiver);
        if (!exitActivityFlag) {
            StateSaver.saveLink(this, itemLink);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        StateSaver.resetLink(this);
        exitActivityFlag = true;
    }

}
