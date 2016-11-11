package yuriy.rssreader.ui.dialogs;

import android.app.DialogFragment;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import yuriy.rssreader.R;
import yuriy.rssreader.services.UrlSaverService;
import yuriy.rssreader.services.receivers.UrlSaverReceiver;

public final class AddNewUrlDialog extends DialogFragment {
    private final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
    private final IntentFilter intentFilter = new IntentFilter();
    private UrlSaverReceiver receiver;

    public AddNewUrlDialog() {
        super();
        intentFilter.addAction(UrlSaverService.FAIL);
        intentFilter.addAction(UrlSaverService.SUCCESS);
    }

    @Override
    public void onResume() {
        super.onResume();
        broadcastManager.registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        broadcastManager.unregisterReceiver(receiver);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.add_url_dialog, container);
        final EditText editText = (EditText) view.findViewById(R.id.urlInput);

        receiver = new UrlSaverReceiver(this);

        editText.setSelection(editText.getText().toString().length());

        final Button cancelButton = (Button) view.findViewById(R.id.cancelUrlButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dismiss();
            }
        });

        final Button addUrlButton = (Button) view.findViewById(R.id.addUrlButton);
        addUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String inputUrl = String.valueOf(editText.getText());
                UrlSaverService.checkAndSave(getActivity(), inputUrl);
            }
        });

        return view;
    }
}
