package yuriy.rssreader.ui;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import yuriy.rssreader.R;
import yuriy.rssreader.service.realization.DataReceiver;
import yuriy.rssreader.service.realization.XMLParser;

import java.net.URL;

public final class AddNewUrlDialog extends DialogFragment implements View.OnClickListener {

    private static final int FAIL_CASE = 1;
    private static final int SUCCES_CASE = 0;

    private EditText editText;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.add_url_dialog, null);
        editText = (EditText) view.findViewById(R.id.urlInput);
        editText.setSelection(editText.getText().toString().length());

        builder.setView(view)
                .setPositiveButton(R.string.addUrlButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        addNewUrl(String.valueOf(editText.getText()));
                        AddNewUrlDialog.this.onClick(view);
                    }
                })
                .setNegativeButton(R.string.cancelUrlAddButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        AddNewUrlDialog.this.getDialog().cancel();
                    }
                });

        final Button checkUrlButton = (Button) view.findViewById(R.id.addUrlButton);
        checkUrlButton.setOnClickListener(this);

        return builder.create();
    }

    private void addNewUrl(final String stringUrl) {

    }


    @Override
    public void onClick(final View v) {

        final String inputUrl = String.valueOf(editText.getText());

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case (FAIL_CASE):
                        Toast.makeText(v.getContext(), getString(R.string.incorrectURL), Toast.LENGTH_SHORT).show();

                        break;
                    case (SUCCES_CASE):
                        Toast.makeText(v.getContext(), getString(R.string.correctURL), Toast.LENGTH_SHORT).show();

                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final URL url = new URL(inputUrl);
                    final DataReceiver dataReceiver = new DataReceiver();
                    new XMLParser(dataReceiver.getTextFromURL(url)).resolveXmlToEntries();
                    handler.sendEmptyMessage(SUCCES_CASE);
                } catch (Exception e) {
                    handler.sendEmptyMessage(FAIL_CASE);
                }
            }
        }).start();

    }
}
