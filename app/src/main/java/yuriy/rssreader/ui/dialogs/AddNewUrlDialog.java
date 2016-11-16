package yuriy.rssreader.ui.dialogs;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import yuriy.rssreader.R;
import yuriy.rssreader.services.UrlSaverService;
import yuriy.rssreader.services.receivers.UrlSaverReceiver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class AddNewUrlDialog extends DialogFragment {

    private final static String DIALOG_TAG = "yuriy.rssreader.ui.activity_controllers.ToolbarListener";
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


        final ProgressDialog waitToCheckDialog = new ProgressDialog(getActivity());

        receiver = new UrlSaverReceiver(this, waitToCheckDialog);

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
                waitToCheckDialog.setMessage(getString(R.string.wait_to_check_dialog_message));
                waitToCheckDialog.setCanceledOnTouchOutside(true);
                waitToCheckDialog.show();
            }
        });

        if(!DIALOG_TAG.equals(getTag())){
            editText.setText(getTag());
            addUrlButton.setVisibility(View.VISIBLE);
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                try {
                    final Pattern pattern = Patterns.WEB_URL;
                    final Matcher matcher = pattern.matcher(s);

                    if (matcher.matches()) {
                        addUrlButton.setVisibility(View.VISIBLE);
                    } else {
                        addUrlButton.setVisibility(View.INVISIBLE);
                    }
                } catch (PatternSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return view;


    }
}
