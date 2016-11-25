package yuriy.rssreader.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class NetworkChecker {

    private NetworkChecker() {
        throw new UnsupportedOperationException();
    }


    public static boolean isConnected(final Context context) {
        if (context == null) {
            return false;
        }
        final ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isWifiConnected(final Context context) {
        if (context == null) {
            return false;
        }
        final ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

}
