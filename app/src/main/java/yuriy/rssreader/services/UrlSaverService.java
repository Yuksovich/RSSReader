package yuriy.rssreader.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import yuriy.rssreader.R;
import yuriy.rssreader.controllers.processors.DataReceiver;
import yuriy.rssreader.controllers.processors.XMLParser;
import yuriy.rssreader.rssexceptions.DuplicateChannelUrlException;

import java.net.URL;
import java.util.Map;

public final class UrlSaverService extends IntentService {

    private static final String CHANNELS = "channels";
    private static final String SERVICE_NAME = "yuriy.rssreader.services.UrlSaverService";
    public static final String SUCCESS  = "yuriy.rssreader.services.SUCCESS";
    public static final String FAIL  = "yuriy.rssreader.services.FAIL";


    private final Intent intent = new Intent();
    private final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
    private SharedPreferences sharedPreferences= getSharedPreferences(CHANNELS, MODE_PRIVATE);

    public UrlSaverService(){
        super(SERVICE_NAME);
    }

    public static void checkAndSave(final Context context,final  String inputUrl){
        Intent intentToHandle = new Intent(context, UrlSaverService.class);
        intentToHandle.setAction(inputUrl);
        context.startService(intentToHandle);
    }

    @Override
    protected void onHandleIntent(final Intent intentToHandle) {
        String inputUrl = intentToHandle.getAction();
        handleCheckAndSave(inputUrl);
    }


    private void handleCheckAndSave(final String inputUrl) {

                try{
                    final URL url = new URL(inputUrl);
                    final DataReceiver dataReceiver = new DataReceiver();
                    final String channelTitle = new XMLParser(dataReceiver.getTextFromURL(url)).getChannelTitle();
                    saveUrl(inputUrl, channelTitle);
                    intent.setAction(SUCCESS);
                    intent.putExtra(SUCCESS, (R.string.correctURL));
                    broadcastManager.sendBroadcast(intent);
                } catch (DuplicateChannelUrlException e) {

                    intent.setAction(FAIL);
                    intent.putExtra(FAIL, (R.string.duplicateURL));
                    broadcastManager.sendBroadcast(intent);

                } catch (Exception e) {
                    intent.setAction(FAIL);
                    intent.putExtra(FAIL, (R.string.incorrectURL));
                    broadcastManager.sendBroadcast(intent);
                }

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