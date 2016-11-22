package yuriy.rssreader.ui.entry_activity;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import yuriy.rssreader.R;
import yuriy.rssreader.services.DatabaseOperationService;
import yuriy.rssreader.services.SingleEntryOperationService;
import yuriy.rssreader.utils.EntrySerializer;
import yuriy.rssreader.utils.Theme;

import static yuriy.rssreader.services.SingleEntryOperationService.SINGLE_ENTRY;

public final class SingleViewFragment extends Fragment {


    private static final String MIME_TYPE = "text/html";
    private static final String ENCODING = "UTF-8";
    private static final String HISTORY_URL = null;
    private static final String BASE_URL = null;
    private String itemLink;
    private BroadcastReceiver receiver;
    private final IntentFilter intentFilter = new IntentFilter(SINGLE_ENTRY);
    private LocalBroadcastManager broadcastManager;

    public static SingleViewFragment getInstance(final String itemLink) {
        final SingleViewFragment singleViewFragment = new SingleViewFragment();
        singleViewFragment.setItemLink(itemLink);
        return singleViewFragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final @Nullable ViewGroup container, final @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.entry_view, container, false);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        broadcastManager = LocalBroadcastManager.getInstance(getActivity());

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {

                final String HTML_STYLE = getString(R.string.html_style_css_beginning)
                        + Theme.getHtmlStyleCssMiddle(context)
                        + getString(R.string.html_style_css_ending);
                final String READ_MORE_STRING = getString(R.string.html_read_more_link);
                final EntrySerializer.SerializableEntry entry =
                        (EntrySerializer.SerializableEntry) intent.getSerializableExtra(SINGLE_ENTRY);
                if (entry != null) {

                    ((TextView) getActivity()
                            .findViewById(R.id.item_title_show)).setText(entry.getItemTitle());
                    ((TextView) getActivity()
                            .findViewById(R.id.item_pubDate_show)).setText(entry.getItemPubDate());
                    ((TextView) getActivity()
                            .findViewById(R.id.channel_title_show)).setText(entry.getChannelTitle());
                    ((TextView) getActivity()
                            .findViewById(R.id.channel_description_show)).setText(entry.getChannelDescription());
                    WebView webView = (WebView) getActivity().findViewById(R.id.item_description_show);

                    final String readMoreLink = String.format(READ_MORE_STRING, itemLink);
                    webView.loadDataWithBaseURL(
                            BASE_URL,
                            HTML_STYLE + entry.getItemDescription() + readMoreLink,
                            MIME_TYPE,
                            ENCODING,
                            HISTORY_URL);
                    if (entry.isItemUnseen()) {
                        DatabaseOperationService.setEntryBeenViewed(getActivity(), itemLink);
                    }
                }
            }
        };
        broadcastManager.registerReceiver(receiver, intentFilter);
        if (itemLink != null) {
            SingleEntryOperationService.singleEntryRequest(getActivity(), itemLink);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (broadcastManager != null) {
            broadcastManager.unregisterReceiver(receiver);
        }
    }

    public void setItemLink(final String itemLink) {
        this.itemLink = itemLink;
        if (getActivity() != null) {
            SingleEntryOperationService.singleEntryRequest(getActivity(), itemLink);
        }
    }
}
