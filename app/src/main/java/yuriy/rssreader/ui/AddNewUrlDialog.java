package yuriy.rssreader.ui;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
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

    private static final String SPACER = " ";
    private EditText editText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
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

                    }
                })
                .setNegativeButton(R.string.cancelUrlAddButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        AddNewUrlDialog.this.getDialog().cancel();
                    }
                });

        final Button checkUrlButton = (Button) view.findViewById(R.id.checkUrlButton);
        checkUrlButton.setOnClickListener(this);

        return builder.create();
    }

    private void addNewUrl(final String stringUrl) {
        //Intent intent = new Intent(context, RssProviderService.class);

    }

    @Override
    public void onClick(View v) {
        final String inputUrl = String.valueOf(editText.getText());
        try {
            final URL url = new URL(inputUrl);
            final DataReceiver dataReceiver = new DataReceiver();
            new XMLParser(dataReceiver.getTextFromURL(url)).resolveXmlToEntries();

        } catch (Exception e) {
            Toast.makeText(v.getContext(), getString(R.string.incorrectURL) + SPACER + inputUrl, Toast.LENGTH_SHORT).show();
        }
    }
}
