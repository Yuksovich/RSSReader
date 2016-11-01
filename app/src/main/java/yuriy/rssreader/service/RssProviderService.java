package yuriy.rssreader.service;

import android.app.Service;
import android.content.Intent;
import android.database.SQLException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;
import yuriy.rssreader.R;
import yuriy.rssreader.data.SingleRSSEntry;
import yuriy.rssreader.rssexceptions.NoRSSContentException;
import yuriy.rssreader.service.realization.DBPopulator;
import yuriy.rssreader.service.realization.DataReceiver;
import yuriy.rssreader.service.realization.XMLParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


final public class RssProviderService extends Service {

    private static final int SUCCESS = 0;
    private static final int NO_RSS = 1;
    private static final int INCORRECT_URL = 2;
    private static final int HTTP_CONNECTION_FAIL = 3;
    private static final int DATABASE_FAIL = 4;
    private static final String SPACER = " ";
    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case (0):
                    Toast.makeText(RssProviderService.this, getResources().getString(R.string.recievedItemCount) + SPACER + (int) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case (1):
                    Toast.makeText(RssProviderService.this, getResources().getString(R.string.noRss) + SPACER + msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case (2):
                    Toast.makeText(RssProviderService.this, getResources().getString(R.string.incorrectURL) + SPACER + (int) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case (3):
                    Toast.makeText(RssProviderService.this, getResources().getString(R.string.httpFail) + SPACER + msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case (4):
                    Toast.makeText(RssProviderService.this, getResources().getString(R.string.sqlFail) + SPACER + msg.obj, Toast.LENGTH_SHORT).show();

            }
        }
    };

    public RssProviderService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//////////////////////////////////////////////////////////////////////////////////////////////////////
        URL urlHabr = null;                                   //this code must be replaced
        try {
            urlHabr = new URL("https://habrahabr.ru/rss");
        } catch (MalformedURLException e) {
        }

        final ArrayList<URL> urls = new ArrayList<>();
        urls.add(urlHabr);                                  //to here is just to check service working
//////////////////////////////////////////////////////////////////////////////////////////////////////
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataReceiver dataReceiver;
                String data;
                XMLParser xmlParser;
                ArrayList<SingleRSSEntry> entriesArray;
                DBPopulator dbPopulator = null;
                int newEntriesCount = 0;

                for (URL url : urls) {
                    try {
                        dataReceiver = new DataReceiver();

                        data = dataReceiver.getTextFromURL(url);
                        xmlParser = new XMLParser(data);
                        entriesArray = xmlParser.resolveXmlToEntries();
                        dbPopulator = new DBPopulator(RssProviderService.this);
                        dbPopulator.populate(entriesArray);
                        newEntriesCount += dbPopulator.getNewEntriesCount();

                    } catch (IOException e) {
                        handler.sendMessage(Message.obtain(handler, HTTP_CONNECTION_FAIL, url));
                    } catch (NoRSSContentException e) {
                        handler.sendMessage(Message.obtain(handler, NO_RSS, url));
                    } catch (SQLException e) {
                        handler.sendMessage(Message.obtain(handler, DATABASE_FAIL, url));
                    } finally {
                        if (dbPopulator != null) {
                            dbPopulator.close();
                        }
                    }
                }
                handler.sendMessage(Message.obtain(handler, SUCCESS, newEntriesCount));
            }
        }).start();

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
