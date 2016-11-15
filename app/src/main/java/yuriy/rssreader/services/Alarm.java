package yuriy.rssreader.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import yuriy.rssreader.R;
import yuriy.rssreader.services.receivers.OnBootReceiver;
import yuriy.rssreader.utils.ShortToast;

public final class Alarm extends BroadcastReceiver {

    private static final int MINUTES_TO_MILLIS = 60_000;
    private static final int REQUEST_CODE = 0;
    private static final boolean NOTIFY_IF_NOTHING_NEW = false;
    private static final boolean MAKE_NOTIFICATION = true;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        DatabaseOperationService.refreshDatabase(context, NOTIFY_IF_NOTHING_NEW, MAKE_NOTIFICATION);

    }

    public void startAlarmService(final Context context, int period) {
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(context, Alarm.class);
        final PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + period * MINUTES_TO_MILLIS,
                period * MINUTES_TO_MILLIS,
                pendingIntent);
        ShortToast.makeText(context, context.getString(R.string.current_refresh_period) + (double) period / 60);

        turnOnBootAlarmReceiver(context);
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

        turnOffBootAlarmReceiver(context);
    }

    private void turnOnBootAlarmReceiver(final Context context){
        final ComponentName onBootReceiver = new ComponentName(context, OnBootReceiver.class);
        final PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(onBootReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void turnOffBootAlarmReceiver(final Context context){
        final ComponentName onBootReceiver = new ComponentName(context, OnBootReceiver.class);
        final PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(onBootReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
