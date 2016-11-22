package yuriy.rssreader.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.support.v4.content.LocalBroadcastManager;
import yuriy.rssreader.database.DBReader;
import yuriy.rssreader.database.DBWriter;
import yuriy.rssreader.database.SingleRSSEntry;
import yuriy.rssreader.utils.EntrySerializer;


public final class SingleEntryOperationService extends IntentService {
    private final static String SERVICE_NAME = "yuriy.rssreader.services.SingleEntryOperationService";
    private static final String ENTRY_QUERY = "yuriy.rssreader.services.SingleEntryOperationService.ENTRY_QUERY";
    public static final String SINGLE_ENTRY = "yuriy.rssreader.services.SingleEntryOperationService.SINGLE_ENTRY";
    private static final String ENTRY_DELETE = "yuriy.rssreader.services.SingleEntryOperationService.ENTRY_DELETE";

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

    public static void singleEntryDelete(final Context context, final String itemLink){
        final Intent intentDelete = new Intent(context, SingleEntryOperationService.class);
        intentDelete.setAction(ENTRY_DELETE);
        intentDelete.putExtra(ENTRY_DELETE, itemLink);
        context.startService(intentDelete);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final String action = intent.getAction();
        switch (action) {
            case (ENTRY_QUERY):
                handleSingleEntryQuery(intent.getStringExtra(ENTRY_QUERY));
                break;
            case (ENTRY_DELETE):
                handleSingleEntryDelete(intent.getStringExtra(ENTRY_DELETE));
                break;
            default:
                break;
        }
    }

    private void handleSingleEntryQuery(final String itemLink) {
        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        DBReader dbReader = null;
        try {
            dbReader = new DBReader(this);
            final SingleRSSEntry entry = dbReader.readSingleEntry(itemLink);
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

    private void handleSingleEntryDelete(final String itemLink){
        DBWriter dbWriter = null;
        try {
            dbWriter = new DBWriter(this);
            dbWriter.deleteEntry(itemLink);
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            if(dbWriter!=null) {
                dbWriter.close();
            }
        }
    }
}