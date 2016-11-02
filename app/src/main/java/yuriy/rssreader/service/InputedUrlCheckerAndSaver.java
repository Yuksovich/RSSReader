package yuriy.rssreader.service;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;
import yuriy.rssreader.R;
import yuriy.rssreader.service.realization.DataReceiver;
import yuriy.rssreader.service.realization.XMLParser;
import yuriy.rssreader.ui.AddNewUrlDialog;

import java.net.URL;

public final class InputedUrlCheckerAndSaver {

    private static final int FAIL_CASE = 1;
    private static final int SUCCES_CASE = 0;
    private final Context context;
    private final View view;
    private final String inputUrl;

    public InputedUrlCheckerAndSaver(final Context context, final View view, final String inputUrl) {
        this.view = view;
        this.inputUrl = inputUrl;
        this.context = context;
    }

    private void checkAndSave(final View view) {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                switch (msg.what) {
                    case (FAIL_CASE):
                        Toast.makeText(view.getContext(), context.getString(R.string.incorrectURL), Toast.LENGTH_SHORT).show();
                        break;
                    case (SUCCES_CASE):
                        Toast.makeText(view.getContext(), context.getString(R.string.correctURL), Toast.LENGTH_SHORT).show();
                        AddNewUrlDialog.this.saveUrl(inputUrl);
                        AddNewUrlDialog.this.dismiss();
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
