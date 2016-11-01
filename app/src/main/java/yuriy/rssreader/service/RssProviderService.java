package yuriy.rssreader.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

final public class RssProviderService extends Service {

    public RssProviderService(){

    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new RssBinder();
    }
}
