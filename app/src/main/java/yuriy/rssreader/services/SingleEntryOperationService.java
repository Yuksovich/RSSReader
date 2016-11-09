package yuriy.rssreader.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import yuriy.rssreader.database.DBReader;
import yuriy.rssreader.database.SingleRSSEntry;
import yuriy.rssreader.utils.EntrySerializer;


public final class SingleEntryOperationService extends IntentService {
    private final static String SERVICE_NAME = "yuriy.rssreader.services.SingleEntryOperationService";
    private static final String ENTRY_QUERY = "yuriy.rssreader.services.SingleEntryOperationService.ENTRY_QUERY";
    public static final String SINGLE_ENTRY = "yuriy.rssreader.services.SingleEntryOperationService.SINGLE_ENTRY";
    private final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
    private final Intent intent = new Intent();

    public SingleEntryOperationService() {
        super(SERVICE_NAME);
    }

    public static void singleEntryRequest(final Context context, final String itemLink) {
        final Intent intentQuery = new Intent(context, SingleEntryOperationService.class);
        intentQuery.setAction(ENTRY_QUERY);
        intentQuery.putExtra(ENTRY_QUERY, itemLink);
        context.startService(intentQuery);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        final String action = intent.getAction();
        switch (action) {
            case (ENTRY_QUERY):
                handleSingleEntryQuery(intent.getStringExtra(ENTRY_QUERY));
                break;
            default:
                break;
        }
    }

    private void handleSingleEntryQuery(String itemLink) {

        DBReader dbReader = null;
        try {
            dbReader = new DBReader(this);
            SingleRSSEntry entry = dbReader.readSingleEntry(itemLink);
            intent.setAction(SINGLE_ENTRY);
            intent.putExtra(SINGLE_ENTRY, EntrySerializer.getSerializable(entry));
            broadcastManager.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if(dbReader!=null) {
                dbReader.close();
            }
        }

    }
}