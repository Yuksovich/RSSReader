package yuriy.rssreader.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import yuriy.rssreader.R;
import yuriy.rssreader.services.receivers.InternetOnReceiver;
import yuriy.rssreader.services.receivers.OnBootReceiver;
import yuriy.rssreader.services.receivers.WifiOnReceiver;
import yuriy.rssreader.utils.NetworkChecker;
import yuriy.rssreader.utils.ServiceReceiversSwitchers;
import yuriy.rssreader.utils.ShortToast;

public final class Alarm extends BroadcastReceiver {

    private static final int MINUTES_TO_MILLIS = 60_000;
    private static final int REQUEST_CODE = 0;
    private static final boolean DO_NOT_NOTIFY_IF_NOTHING_NEW = false;
    private static final boolean MAKE_NOTIFICATION = true;
    private static final String KEY_WIFI_ONLY = "key_autorefresh_if_wifi_preference_screen";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        ServiceReceiversSwitchers.switchOff (WifiOnReceiver.class, context);
        ServiceReceiversSwitchers.switchOff(InternetOnReceiver.class, context);
        final SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean wifiOnly = sPrefs.getBoolean(KEY_WIFI_ONLY, false);
        if (wifiOnly) {
            if (NetworkChecker.isWifiConnected(context)) {
                DatabaseOperationService.refreshDatabase(context, DO_NOT_NOTIFY_IF_NOTHING_NEW, MAKE_NOTIFICATION);
                ServiceReceiversSwitchers.switchOff (WifiOnReceiver.class, context);
            } else {
                ServiceReceiversSwitchers.switchOn(WifiOnReceiver.class,context);
            }
        } else {
            if (NetworkChecker.isConnected(context)) {
                DatabaseOperationService.refreshDatabase(context, DO_NOT_NOTIFY_IF_NOTHING_NEW, MAKE_NOTIFICATION);
                ServiceReceiversSwitchers.switchOff(InternetOnReceiver.class, context);
            } else {
                ServiceReceiversSwitchers.switchOn(InternetOnReceiver.class, context);
            }
        }
    }

    public void startAlarmService(final Context context, int period) {
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(context, Alarm.class);
        final PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.setRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + period * MINUTES_TO_MILLIS,
                period * MINUTES_TO_MILLIS,
                pendingIntent);
        ShortToast.makeText(context, context.getString(R.string.current_refresh_period) + (double) period / 60);

        ServiceReceiversSwitchers.switchOn(OnBootReceiver.class, context);
    }

    public void stopAlarmService(final Context context) {
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(context, Alarm.class);
        final PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        ShortToast.makeText(context, context.getString(R.string.auto_refresh_off));
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }

        ServiceReceiversSwitchers.switchOff(OnBootReceiver.class, context);
    }

}
