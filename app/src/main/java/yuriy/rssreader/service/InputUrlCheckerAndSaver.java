package yuriy.rssreader.service;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;
import yuriy.rssreader.R;
import yuriy.rssreader.rssexceptions.DuplicateChannelUrlException;
import yuriy.rssreader.service.controllers.DataReceiver;
import yuriy.rssreader.service.controllers.XMLParser;

import java.net.URL;
import java.util.Map;

public final class InputUrlCheckerAndSaver {

    private static final int FAIL_CASE = 1;
    private static final int SUCCESS_CASE = 0;
    private static final int DUPLICATE_URL_CASE = 2;
    private static final String CHANNELS = "channels";

    private final DialogFragment dialog;
    private final String inputUrl;
    private final SharedPreferences sharedPreferences;
    private final Context context;

    public InputUrlCheckerAndSaver(final DialogFragment dialog, final View view, final String inputUrl) {
        this.dialog = dialog;
        this.inputUrl = inputUrl;
        this.context = view.getContext();
        sharedPreferences = context.getSharedPreferences(CHANNELS, Context.MODE_PRIVATE);

        checkAndSave();
    }

    private void checkAndSave() {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                switch (msg.what) {
                    case (FAIL_CASE):
                        Toast.makeText(context, context.getString(R.string.incorrectURL), Toast.LENGTH_SHORT).show();
                        break;
                    case (SUCCESS_CASE):
                        Toast.makeText(context, context.getString(R.string.correctURL), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        break;
                    case (DUPLICATE_URL_CASE):
                        Toast.makeText(context, context.getString(R.string.duplicateURL), Toast.LENGTH_SHORT).show();
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final URL url = new URL(inputUrl);
                    final DataReceiver dataReceiver = new DataReceiver();
                    final String channelTitle = new XMLParser(dataReceiver.getTextFromURL(url)).getChannelTitle();
                    saveUrl(inputUrl, channelTitle);
                    handler.sendEmptyMessage(SUCCESS_CASE);
                } catch (DuplicateChannelUrlException e) {
                    handler.sendEmptyMessage(DUPLICATE_URL_CASE);
                } catch (Exception e) {
                    handler.sendEmptyMessage(FAIL_CASE);
                }
            }
        }).start();
    }

    private void saveUrl(final String inputUrl, final String channelTitle) throws DuplicateChannelUrlException {
        Map<String, ?> map = sharedPreferences.getAll();
        if (map != null) {
            if (map.containsKey(inputUrl)) {
                throw new DuplicateChannelUrlException();
            }
        }
        writeUrlToSharedPreferences(inputUrl, channelTitle);
    }

    private void writeUrlToSharedPreferences(final String inputUrl, final String channelTitle) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(inputUrl, channelTitle);
        editor.apply();
    }
}
