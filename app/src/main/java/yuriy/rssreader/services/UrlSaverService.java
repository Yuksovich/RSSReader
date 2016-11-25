package yuriy.rssreader.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import yuriy.rssreader.R;
import yuriy.rssreader.controllers.data_input.InternetDataReceiver;
import yuriy.rssreader.controllers.data_input.RssOrAtom;
import yuriy.rssreader.rssexceptions.DuplicateChannelUrlException;

import java.net.URL;
import java.util.Map;

import static yuriy.rssreader.services.DatabaseOperationService.CHANNELS;

public final class UrlSaverService extends IntentService {

    private static final String SERVICE_NAME = "yuriy.rssreader.services.UrlSaverService";
    public static final String SUCCESS = "yuriy.rssreader.services.UrlSaverService.SUCCESS";
    public static final String FAIL = "yuriy.rssreader.services.UrlSaverService.FAIL";

    private final Intent intent = new Intent();
    private SharedPreferences sharedPreferences;


    public UrlSaverService() {
        super(SERVICE_NAME);
    }

    public static void checkAndSave(final Context context, final String inputUrl) {
        if (context == null || inputUrl == null) {
            return;
        }
        final Intent intentToHandle = new Intent(context, UrlSaverService.class);
        intentToHandle.setAction(inputUrl);
        context.startService(intentToHandle);
    }

    @Override
    protected void onHandleIntent(final Intent intentToHandle) {
        if (intentToHandle == null) {
            return;
        }
        final String inputUrl = intentToHandle.getAction();
        handleCheckAndSave(inputUrl);
    }


    private void handleCheckAndSave(final String inputUrl) {
        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        try {

            final URL url = new URL(inputUrl);
            final InternetDataReceiver internetDataReceiver = new InternetDataReceiver();
            final String channelTitle =
                    RssOrAtom.getParser(internetDataReceiver.getTextFromURL(url), inputUrl)
                            .getChannelTitle();

            saveUrl(inputUrl, channelTitle);
            intent.setAction(SUCCESS);
            intent.putExtra(SUCCESS, getString(R.string.correctURL));
            broadcastManager.sendBroadcast(intent);
        } catch (DuplicateChannelUrlException e) {

            intent.setAction(FAIL);
            intent.putExtra(FAIL, getString(R.string.duplicateURL));
            broadcastManager.sendBroadcast(intent);

        } catch (Exception e) {
            intent.setAction(FAIL);
            intent.putExtra(FAIL, getString(R.string.incorrectURL));
            broadcastManager.sendBroadcast(intent);
        }

    }

    private void saveUrl(final String inputUrl, final String channelTitle)
            throws DuplicateChannelUrlException {
        sharedPreferences = getSharedPreferences(CHANNELS, MODE_PRIVATE);
        final Map<String, ?> map = sharedPreferences.getAll();
        if (map != null) {
            if (map.containsKey(inputUrl)) {
                throw new DuplicateChannelUrlException();
            }
        }
        writeUrlToSharedPreferences(inputUrl, channelTitle);
    }

    private void writeUrlToSharedPreferences(final String inputUrl, final String channelTitle) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(inputUrl, channelTitle);
        editor.apply();
    }
}
