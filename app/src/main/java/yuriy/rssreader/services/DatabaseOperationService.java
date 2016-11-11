package yuriy.rssreader.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.support.v4.content.LocalBroadcastManager;
import yuriy.rssreader.R;
import yuriy.rssreader.controllers.data_input.DataReceiver;
import yuriy.rssreader.controllers.data_input.XMLParser;
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

public final class DatabaseOperationService extends IntentService {

    private static final String CHANNELS = "channels";
    private static final String SPACER = " ";
    public static final String SUCCESS = "yuriy.rssreader.services.DatabaseOperationService.action.SUCCESS";
    public static final String FAIL = "yuriy.rssreader.services.DatabaseOperationService.action.FAIL";
    public static final String DATA = "yuriy.rssreader.services.DatabaseOperationService.extra.DATA";
    public static final String ON_DATA_RECEIVED = "yuriy.rssreader.services.action.ON_DATA_RECEIVED";
    public static final String DATABASE_EMPTY = "yuriy.rssreader.services.DatabaseOperationService.DATABASE_EMPTY";
    public static final String ALL_CHANNELS = "yuriy.rssreader.services.DatabaseOperationService.filter.ALL_CHANNELS";

    private static final String SERVICE_NAME = "yuriy.rssreader.services.DatabaseOperationService";
    private static final String REQUEST_ENTRIES_ACTION = "REQUEST_ENTRIES_ACTION";
    private static final String REFRESH_DATABASE_ACTION = "REFRESH_DATABASE";
    private static final String NOTIFY_IF_NOTHING = "NOTIFY_IF_NOTHING_NEW";
    private static final boolean NOTIFY_BY_DEFAULT = true;
    private static final String SET_ENTRY_BEEN_SEEN = "yuriy.rssreader.services.DatabaseOperationService.action.SET_ENTRY_BEEN_SEEN";
    private static final String DELETE_CHANNEL_ENTRIES = "yuriy.rssreader.services.DatabaseOperationService.DELETE_CHANNEL_ENTRIES";

    private final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
    private final Intent intent = new Intent();

    public DatabaseOperationService() {
        super(SERVICE_NAME);
    }

    public static void requestEntries(final Context context, String channel) {
        final Intent requestIntent = new Intent(context, DatabaseOperationService.class);
        requestIntent.setAction(REQUEST_ENTRIES_ACTION);
        requestIntent.putExtra(REQUEST_ENTRIES_ACTION, channel);
        context.startService(requestIntent);
    }

    public static void refreshDatabase(final Context context, boolean notifyIfNothingNew) {
        final Intent refreshIntent = new Intent(context, DatabaseOperationService.class);
        refreshIntent.setAction(REFRESH_DATABASE_ACTION);
        refreshIntent.putExtra(NOTIFY_IF_NOTHING, notifyIfNothingNew);
        context.startService(refreshIntent);
    }

    public static void setEntryBeenViewed(final Context context, final String itemLink) {
        final Intent setSeenIntent = new Intent(context, DatabaseOperationService.class);
        setSeenIntent.setAction(SET_ENTRY_BEEN_SEEN);
        setSeenIntent.putExtra(SET_ENTRY_BEEN_SEEN, itemLink);
        context.startService(setSeenIntent);
    }

    public static void deleteChannelsEntries(final Context context, final String channel) {
        final Intent deleteEntriesIntent = new Intent(context, DatabaseOperationService.class);
        deleteEntriesIntent.setAction(DELETE_CHANNEL_ENTRIES);
        deleteEntriesIntent.putExtra(DELETE_CHANNEL_ENTRIES, channel);
        context.startService(deleteEntriesIntent);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        switch (intent.getAction()) {
            case (REFRESH_DATABASE_ACTION):
                handleActionRefresh(intent.getBooleanExtra(NOTIFY_IF_NOTHING, NOTIFY_BY_DEFAULT));
                break;
            case (REQUEST_ENTRIES_ACTION):
                handleActionRequest(intent.getStringExtra(REQUEST_ENTRIES_ACTION));
                break;
            case (SET_ENTRY_BEEN_SEEN):
                handleSetSeenAction(intent.getStringExtra(SET_ENTRY_BEEN_SEEN));
                break;
            case (DELETE_CHANNEL_ENTRIES):
                handleActionDeleteChannelEntries(intent.getStringExtra(DELETE_CHANNEL_ENTRIES));
                break;
            default:
                break;
        }
    }

    private void handleActionRefresh(final boolean notifyIfNothingNew) {
        final SharedPreferences sharedPreferences = getSharedPreferences(CHANNELS, MODE_PRIVATE);
        final Map<String, ?> map = sharedPreferences.getAll();
        final ArrayList<String> urls = new ArrayList<>();

        if (map.isEmpty()) {
            intent.setAction(FAIL);
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
                dbWriter.populate(entriesArray, urlString);
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
        if (newEntriesCount != 0 || notifyIfNothingNew) {
            intent.setAction(SUCCESS);
            intent.putExtra(SUCCESS, getString(R.string.receivedItemCount) + SPACER + newEntriesCount);
            broadcastManager.sendBroadcast(intent);
        }
    }

    private void handleActionRequest(final String channelDescription) {

        DBReader dbReader = null;
        ArrayList<SingleRSSEntry> entriesArray = null;
        try {
            dbReader = new DBReader(this);
            if (ALL_CHANNELS.equals(channelDescription)) {
                entriesArray = dbReader.read();
            } else {
                entriesArray = dbReader.read(channelDescription);
            }

        } catch (DatabaseIsEmptyException e) {
            intent.setAction(DATABASE_EMPTY);
            intent.putExtra(DATABASE_EMPTY, getString(R.string.databaseIsEmpty));
            broadcastManager.sendBroadcast(intent);
        } catch (SQLException e) {
            intent.setAction(FAIL);
            intent.putExtra(FAIL, getString(R.string.sqlFail));
            broadcastManager.sendBroadcast(intent);
        } finally {
            if (dbReader != null) {
                dbReader.close();
            }
        }
        if (entriesArray != null) {
            intent.setAction(ON_DATA_RECEIVED);
            intent.putParcelableArrayListExtra(DATA, entriesArray);
            broadcastManager.sendBroadcast(intent);
        }
    }

    private void handleSetSeenAction(final String itemLink) {
        DBWriter dbWriter = null;
        try {
            dbWriter = new DBWriter(this);
            dbWriter.setEntryBeenViewed(itemLink);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (dbWriter != null) {
                dbWriter.close();
            }
        }
    }

    private void handleActionDeleteChannelEntries(final String channelUrl) {
        DBWriter dbWriter = null;
        try {
            dbWriter = new DBWriter(this);
            if (ALL_CHANNELS.equals(channelUrl)) {
                dbWriter.deleteAll();

            } else {
                dbWriter.deleteAllEntriesOfChannel(channelUrl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (dbWriter != null) {
                dbWriter.close();
            }
        }
    }
}


