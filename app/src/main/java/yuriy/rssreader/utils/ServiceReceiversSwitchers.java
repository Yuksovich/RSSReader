package yuriy.rssreader.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

public final class ServiceReceiversSwitchers {
    private ServiceReceiversSwitchers(){
        throw new UnsupportedOperationException();
    }

    public static void switchOn(final Class<?>cls, final Context context){
        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context, cls),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
    public static void switchOff(final Class<?>cls, final Context context){
        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context, cls),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
