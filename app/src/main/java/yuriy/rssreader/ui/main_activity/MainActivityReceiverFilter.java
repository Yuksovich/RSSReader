package yuriy.rssreader.ui.main_activity;

import android.content.IntentFilter;
import yuriy.rssreader.services.DatabaseOperationService;
import yuriy.rssreader.services.UrlSaverService;

public final class MainActivityReceiverFilter {
    private MainActivityReceiverFilter(){
        throw new UnsupportedOperationException();
    }

    public static IntentFilter getInstance(){
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DatabaseOperationService.SUCCESS);
        intentFilter.addAction(DatabaseOperationService.FAIL);
        intentFilter.addAction(DatabaseOperationService.ON_DATA_RECEIVED);
        intentFilter.addAction(DatabaseOperationService.DATABASE_EMPTY);
        intentFilter.addAction(UrlSaverService.SUCCESS);
        return intentFilter;
    }
}
