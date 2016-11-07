package yuriy.rssreader.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.support.v4.content.LocalBroadcastManager;
import yuriy.rssreader.R;
import yuriy.rssreader.controllers.processors.DataReceiver;
import yuriy.rssreader.controllers.processors.XMLParser;
import yuriy.rssreader.database.DBReader;
import yuriy.rssreader.database.DBWriter;
import yuriy.rssreader.database.SingleRSSEntry;
import yuriy.rssreader.rssexceptions.DatabaseIsEmptyException;
import yuriy.rssreader.rssexceptions.NoRSSContentException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public final class DatabaseRefresherService extends IntentService {

    private static final String CHANNELS = "channels";
    private static final String SPACER = " ";
    public static final String SUCCESS = "yuriy.rssreader.services.action.SUCCESS";
    public static final String FAIL = "yuriy.rssreader.services.action.FAIL";
    public static final String DATA = "yuriy.rssreader.services.extra.DATA";
    private static final String SERVICE_NAME = "yuriy.rssreader.services.DatabaseRefresherService";
    private static final String REQUEST_ENTRIES_ACTION = "REQUEST_ENTRIES_ACTION";
    private static final String REFRESH_DATABASE_ACTION = "REFRESH_DATABASE";
    public static final String ON_DATA_RECEIVED = "yuriy.rssreader.services.action.ON_DATA_RECEIVED";

    private final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
    private final Intent intent = new Intent();

    public DatabaseRefresherService() {
        super(SERVICE_NAME);
    }

    public static void requestEntries(final Context context) {
        final Intent requestIntent = new Intent(context, DatabaseRefresherService.class);
        requestIntent.setAction(REQUEST_ENTRIES_ACTION);
        context.startService(requestIntent);
    }

    public static void refreshDatabase(final Context context) {
        final Intent refreshIntent = new Intent(context, DatabaseRefresherService.class);
        refreshIntent.setAction(REFRESH_DATABASE_ACTION);
        context.startService(refreshIntent);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        switch (intent.getAction()) {
            case (REFRESH_DATABASE_ACTION):
                handleActionRefresh();
                break;
            case (REQUEST_ENTRIES_ACTION):
                handleActionRequest();
                break;
            default:
                break;
        }
    }


    private void handleActionRefresh() {
        final SharedPreferences sharedPreferences = getSharedPreferences(CHANNELS, MODE_PRIVATE);
        final Map<String, ?> map = sharedPreferences.getAll();
        final ArrayList<String> urls = new ArrayList<>();



        if (map.isEmpty()) {
            intent.putExtra(FAIL, getString(R.string.noChannelsToRead));
            broadcastManager.sendBroadcast(intent);
            stopSelf();
        }

        final Set<String> set = map.keySet();
        for (String urlAddress : set) {
            urls.add(urlAddress);
        }

        DataReceiver dataReceiver;
        String data;
        URL url;
        XMLParser xmlParser;
        ArrayList<SingleRSSEntry> entriesArray;
        DBWriter dbWriter = null;
        int newEntriesCount = 0;

        for (String urlString : urls) {
            try {
                url = new URL(urlString);
                dataReceiver = new DataReceiver();
                data = dataReceiver.getTextFromURL(url);
                xmlParser = new XMLParser(data);
                entriesArray = xmlParser.receiveAllItems();
                dbWriter = new DBWriter(this);
                dbWriter.populate(entriesArray);
                newEntriesCount += dbWriter.getNewEntriesCount();
            } catch (MalformedURLException e) {
                intent.setAction(FAIL);
                intent.putExtra(FAIL, getString(R.string.incorrectURL) + SPACER + urlString);
                broadcastManager.sendBroadcast(intent);

            } catch (IOException e) {
                intent.setAction(FAIL);
                intent.putExtra(FAIL, getString(R.string.httpFail) + SPACER + urlString);
                broadcastManager.sendBroadcast(intent);

            } catch (NoRSSContentException e) {
                intent.setAction(FAIL);
                intent.putExtra(FAIL, getString(R.string.noRss) + SPACER + urlString);
                broadcastManager.sendBroadcast(intent);

            } catch (SQLException e) {
                intent.setAction(FAIL);
                intent.putExtra(FAIL, getString(R.string.sqlFail) + SPACER + urlString);
                broadcastManager.sendBroadcast(intent);

            } finally {
                if (dbWriter != null) {
                    dbWriter.close();
                }
            }
        }
        intent.setAction(SUCCESS);
        intent.putExtra(SUCCESS, getString(R.string.receivedItemCount) + SPACER + newEntriesCount);
        broadcastManager.sendBroadcast(intent);

    }

    private void handleActionRequest() {
        DBReader dbReader = null;
        ArrayList<SingleRSSEntry> entriesArray = null;
        try {
            dbReader = new DBReader(this);
            entriesArray = dbReader.read();
        } catch (DatabaseIsEmptyException e) {
            intent.setAction(FAIL);
            intent.putExtra(FAIL, getString(R.string.databaseIsEmpty));
        } catch (SQLException e) {
            intent.setAction(FAIL);
            intent.putExtra(FAIL, getString(R.string.sqlFail));
        } finally {
            if (dbReader != null) {
                dbReader.close();
            }
        }

        intent.setAction(ON_DATA_RECEIVED);
        intent.putParcelableArrayListExtra(DATA, entriesArray);
        broadcastManager.sendBroadcast(intent);
    }

}
