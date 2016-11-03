package yuriy.rssreader.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import yuriy.rssreader.R;
import yuriy.rssreader.service.InputUrlCheckerAndSaver;

public final class AddNewUrlDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.add_url_dialog, null);
        final EditText editText = (EditText) view.findViewById(R.id.urlInput);
        editText.setSelection(editText.getText().toString().length());
        builder.setView(view);

        final Button cancelButton = (Button) view.findViewById(R.id.cancelUrlButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final Button addUrlButton = (Button) view.findViewById(R.id.addUrlButton);
        addUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String inputUrl = String.valueOf(editText.getText());
                new InputUrlCheckerAndSaver(AddNewUrlDialog.this, view, inputUrl);
            }
        });

        return builder.create();
    }

 }
