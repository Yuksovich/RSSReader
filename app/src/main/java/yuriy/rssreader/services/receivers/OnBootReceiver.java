package yuriy.rssreader.services.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import yuriy.rssreader.services.Alarm;

public final class OnBootReceiver extends BroadcastReceiver {
    private static final String AUTO_REFRESH_SWITCH = "key_autoRefresh_preferences_screen";
    private static final String AUTO_REFRESH_PERIOD = "key_autoRefresh_period_preferences_screen";
    private static final String DEFAULT_PERIOD = "720";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (context == null || intent == null) {
            return;
        }
        final String action = intent.getAction();
        if (action == null || !action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            return;
        }
        final SharedPreferences sPrefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        if (sPrefs.getBoolean(AUTO_REFRESH_SWITCH, false)) {
            int period = Integer.parseInt(sPrefs.getString(AUTO_REFRESH_PERIOD, DEFAULT_PERIOD));
            final Alarm alarm = new Alarm();
            alarm.startAlarmService(context, period);
        }
    }
}
