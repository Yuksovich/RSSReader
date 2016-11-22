package yuriy.rssreader.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import org.xmlpull.v1.XmlPullParserException;
import yuriy.rssreader.MainActivity;
import yuriy.rssreader.R;
import yuriy.rssreader.controllers.data_input.InternetDataReceiver;
import yuriy.rssreader.controllers.data_input.Parsable;
import yuriy.rssreader.controllers.data_input.RssOrAtom;
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

    public static final String CHANNELS = "yuriy.rssreader.services.DatabaseOperationService.channels";
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

    private static final String SPACER = " ";
    private static final boolean NOTIFY_BY_DEFAULT = true;
    private static final String SET_ENTRY_BEEN_SEEN = "yuriy.rssreader.services.DatabaseOperationService.action.SET_ENTRY_BEEN_SEEN";
    private static final String DELETE_CHANNEL_ENTRIES = "yuriy.rssreader.services.DatabaseOperationService.DELETE_CHANNEL_ENTRIES";
    private static final String MAKE_NOTIFICATION = "MAKE_NOTIFICATION";
    private static final int NOTIFICATION_ID = 0;
    private static final int MAX_PROGRESS = 100;
    private static final int NUMBER_OF_PROGRESS_STEPS = 4;
    private static final String ACTION_CANCEL_REFRESH = "ACTION_CANCEL_REFRESH";

    private static int notificationCounter = 0;
    private static boolean stopFlag;

    private LocalBroadcastManager broadcastManager;
    private final Intent intent = new Intent();
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    private float progress = 0f;
    private int numberOfChannels = 0;

    public DatabaseOperationService() {
        super(SERVICE_NAME);
    }

    public static void requestEntries(final Context context, String channel) {
        final Intent requestIntent = new Intent(context, DatabaseOperationService.class);
        requestIntent.setAction(REQUEST_ENTRIES_ACTION);
        requestIntent.putExtra(REQUEST_ENTRIES_ACTION, channel);
        context.startService(requestIntent);
    }

    public static void refreshDatabase(final Context context, boolean notifyIfNothingNew, boolean makeNotification) {
        final Intent refreshIntent = new Intent(context, DatabaseOperationService.class);
        refreshIntent.setAction(REFRESH_DATABASE_ACTION);
        refreshIntent.putExtra(NOTIFY_IF_NOTHING, notifyIfNothingNew);
        refreshIntent.putExtra(MAKE_NOTIFICATION, makeNotification);
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
                handleActionRefresh(
                        intent.getBooleanExtra(NOTIFY_IF_NOTHING, NOTIFY_BY_DEFAULT),
                        intent.getBooleanExtra(MAKE_NOTIFICATION, NOTIFY_BY_DEFAULT));
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

    private void handleActionRefresh(final boolean notifyIfNothingNew, final boolean makeNotification) {
        stopFlag = false;
        prepareProgressNotification();

        broadcastManager = LocalBroadcastManager.getInstance(this);
        final SharedPreferences sharedPreferences = getSharedPreferences(CHANNELS, MODE_PRIVATE);
        final Map<String, ?> map = sharedPreferences.getAll();
        final ArrayList<String> urls = new ArrayList<>();

        if (map.isEmpty()) {
            intent.setAction(FAIL);
            intent.putExtra(FAIL, getString(R.string.noChannelsToRead));
            broadcastManager.sendBroadcast(intent);
            stopSelf();
        }
        numberOfChannels = map.size();

        final Set<String> set = map.keySet();
        for (String urlAddress : set) {
            urls.add(urlAddress);
        }
        InternetDataReceiver internetDataReceiver;
        String data;
        URL url;
        Parsable parser;
        ArrayList<SingleRSSEntry> entriesArray;
        DBWriter dbWriter = null;
        int newEntriesCount = 0;

        for (String urlString : urls) {
            try {
                url = new URL(urlString);
                if (stopFlag) {
                    dismissNotification();
                    break;
                }
                incrementProgressInNotification(urlString);

                internetDataReceiver = new InternetDataReceiver();
                data = internetDataReceiver.getTextFromURL(url);
                if (stopFlag) {
                    dismissNotification();
                    break;
                }
                incrementProgressInNotification(urlString);

                parser = RssOrAtom.getParser(data, urlString);
                entriesArray = parser.receiveAllItems();
                if (stopFlag) {
                    dismissNotification();
                    break;
                }
                incrementProgressInNotification(urlString);

                dbWriter = new DBWriter(this);
                dbWriter.populate(entriesArray, urlString);
                if (stopFlag) {
                    dismissNotification();
                    break;
                }
                incrementProgressInNotification(urlString);

                newEntriesCount += dbWriter.getNewEntriesCount();
            } catch (MalformedURLException e) {
                intent.setAction(FAIL);
                intent.putExtra(FAIL, getString(R.string.incorrectURL) + SPACER + urlString);
                dismissNotification();
                broadcastManager.sendBroadcast(intent);

            } catch (IOException e) {
                intent.setAction(FAIL);
                intent.putExtra(FAIL, getString(R.string.httpFail) + SPACER + urlString);
                dismissNotification();
                broadcastManager.sendBroadcast(intent);

            } catch (NoRSSContentException e) {
                intent.setAction(FAIL);
                intent.putExtra(FAIL, getString(R.string.noRss) + SPACER + urlString);
                dismissNotification();
                broadcastManager.sendBroadcast(intent);

            } catch (SQLException e) {
                intent.setAction(FAIL);
                intent.putExtra(FAIL, getString(R.string.sqlFail) + SPACER + urlString);
                dismissNotification();
                broadcastManager.sendBroadcast(intent);
            } catch (XmlPullParserException e) {
                intent.setAction(FAIL);
                intent.putExtra(FAIL, getString(R.string.parser_fail) + SPACER + urlString);
                dismissNotification();
                broadcastManager.sendBroadcast(intent);

            } finally {
                if (dbWriter != null) {
                    dbWriter.close();
                }
            }
        }
        dismissNotification();
        if (newEntriesCount != 0 || notifyIfNothingNew) {
            final String message = getString(R.string.receivedItemCount) + SPACER + newEntriesCount;
            if (makeNotification) {
                makeNotification(getString(R.string.receivedItemCount), newEntriesCount);
            }
            intent.setAction(SUCCESS);
            intent.putExtra(SUCCESS, message);
            broadcastManager.sendBroadcast(intent);
        }
    }

    private void handleActionRequest(final String channelDescription) {
        broadcastManager = LocalBroadcastManager.getInstance(this);
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
            notificationCounter = 0;
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

    private void makeNotification(final String message, final int count) {
        notificationCounter += count;
        final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final Intent intent = new Intent(this, MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        final Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.program_name))
                .setContentText(message)
                .setTicker(message)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_rss_feed_white_24dp)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setNumber(notificationCounter)
                .build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void incrementProgressInNotification(final String urlString) {
        progress += (MAX_PROGRESS / numberOfChannels) / NUMBER_OF_PROGRESS_STEPS;
        notificationProgress((int) progress, urlString);
    }

    private void notificationProgress(final int mProgress, final String urlString) {
        notificationBuilder
                .setProgress(MAX_PROGRESS, mProgress, false)
                .setContentText(urlString);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void prepareProgressNotification() {
        final Intent cancelIntent = new Intent(this, DatabaseOperationService.class);
        cancelIntent.setAction(ACTION_CANCEL_REFRESH);
        final PendingIntent piCancel =
                PendingIntent.getService(this, 0, cancelIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.refreshing_progress_notification))
                .setSmallIcon(R.drawable.ic_autorenew_white_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .addAction(R.drawable.ic_clear_white_24dp, getString(R.string.cancel_refresh), piCancel);
    }

    private void dismissNotification() {
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        if (ACTION_CANCEL_REFRESH.equals(intent.getAction())) {
            stopSelf();
            stopFlag = true;
        }
        return super.onStartCommand(intent, flags, startId);
    }

}


