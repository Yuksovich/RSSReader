package yuriy.rssreader.service;

import android.app.DialogFragment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;
import yuriy.rssreader.R;
import yuriy.rssreader.service.realization.DataReceiver;
import yuriy.rssreader.service.realization.XMLParser;

import java.net.URL;

public final class InputedUrlCheckerAndSaver {

    private static final int FAIL_CASE = 1;
    private static final int SUCCES_CASE = 0;
    private final DialogFragment dialog;
    private final View view;
    private final String inputUrl;

    public InputedUrlCheckerAndSaver(DialogFragment dialog, final View view, final String inputUrl) {
        this.dialog = dialog;
        this.view = view;
        this.inputUrl = inputUrl;
        checkAndSave();
    }

    private void checkAndSave() {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                switch (msg.what) {
                    case (FAIL_CASE):
                        Toast.makeText(view.getContext(), view.getContext().getString(R.string.incorrectURL), Toast.LENGTH_SHORT).show();
                        break;
                    case (SUCCES_CASE):
                        Toast.makeText(view.getContext(), view.getContext().getString(R.string.correctURL), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
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

    private void saveUrl(String str) {

    }
}
