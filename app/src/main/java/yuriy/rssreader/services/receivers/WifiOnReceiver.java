package yuriy.rssreader.services.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import yuriy.rssreader.services.Alarm;
import yuriy.rssreader.services.DatabaseOperationService;
import yuriy.rssreader.utils.NetworkChecker;
import yuriy.rssreader.utils.ServiceReceiversSwitchers;

public class WifiOnReceiver extends BroadcastReceiver {
    private static final String WAITING_INTENT = "action.wait";
    private static final int PAUSE = 30_000;
    private static final int NUMBER_OF_TRIES = 3;
    private static int connectionCounter = 0;

    public WifiOnReceiver() {
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (NetworkChecker.isWifiConnected(context)) {
            DatabaseOperationService.refreshDatabase(context, false, true);
            ServiceReceiversSwitchers.switchOff(WifiOnReceiver.class, context);
        } else {
            final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            final Intent alarmIntent = new Intent(context, Alarm.class);
            alarmIntent.setAction(WAITING_INTENT);
            final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            connectionCounter++;
            if (connectionCounter == NUMBER_OF_TRIES) {
                connectionCounter = 0;
            } else {
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + PAUSE * connectionCounter, pendingIntent);
            }

        }


    }
}
