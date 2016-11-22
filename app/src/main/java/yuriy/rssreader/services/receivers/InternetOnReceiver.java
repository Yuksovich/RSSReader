package yuriy.rssreader.services.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import yuriy.rssreader.services.Alarm;
import yuriy.rssreader.services.DatabaseOperationService;
import yuriy.rssreader.utils.NetworkChecker;
import yuriy.rssreader.utils.ServiceReceiversSwitchers;

public class InternetOnReceiver extends BroadcastReceiver {
    private static final String WAITING_INTENT = "action.wait";
    private static final int PAUSE = 30_000;
    private static final int NUMBER_OF_TRIES = 3;
    private static int connectionCounter = 0;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final String action = intent.getAction();
        if (action == null || !action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            return;
        }
        if (NetworkChecker.isConnected(context)) {
            DatabaseOperationService.refreshDatabase(context, false, true);
            ServiceReceiversSwitchers.switchOff(InternetOnReceiver.class, context);
        } else {
            tryLater(context);
        }

    }

    private void tryLater(final Context context) {
        if (context == null) {
            return;
        }
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent alarmIntent = new Intent(context, Alarm.class);
        alarmIntent.setAction(WAITING_INTENT);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        connectionCounter++;
        if (connectionCounter == NUMBER_OF_TRIES) {
            connectionCounter = 0;
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + PAUSE * connectionCounter, pendingIntent);
        }
    }
}
