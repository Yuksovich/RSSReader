package yuriy.rssreader.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public final class StateSaver {
    private static final String SAVED_STATE = "yuriy.rssreader.utils.StateSaver.SAVED_STATE";
    private static final String ITEM_LINK = "yuriy.rssreader.utils.StateSaver.ITEM_LINK";
    private static final String LIST_POSITION = "yuriy.rssreader.utils.StateSaver.LIST_POSITION";
    private static final String LIST_PADDING = "yuriy.rssreader.utils.StateSaver.LIST_PADDING";
    private static final String CHANNEL_FILTER = "yuriy.rssreader.utils.StateSaver.CHANNEL_FILTER";
    private static final String ALL_CHANNELS = "yuriy.rssreader.services.DatabaseOperationService.filter.ALL_CHANNELS";
    public static final String NO_LINK = "yuriy.rssreader.utils.StateSaver.NO_LINK";


    private StateSaver() {
        throw new UnsupportedOperationException();
    }

    private static SharedPreferences getSharedPrefs(final Context context) {
        return context.getSharedPreferences(SAVED_STATE, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(final Context context) {
        return getSharedPrefs(context).edit();
    }

    public static void saveLink(@NonNull final Context context, @NonNull final String itemLink) {
        getEditor(context).putString(ITEM_LINK, itemLink).apply();
    }

    public static void saveListPosition(@NonNull final Context context, @NonNull final int position) {
        getEditor(context).putInt(LIST_POSITION, position).apply();
    }

    public static void saveListPadding(@NonNull final Context context, @NonNull final int padding) {
        getEditor(context).putInt(LIST_PADDING, padding).apply();
    }

    public static void saveChannelFilter(@NonNull final Context context, @NonNull final String listFilter) {
        getEditor(context).putString(CHANNEL_FILTER, listFilter).apply();
    }

    public static String getSavedLink(@NonNull final Context context) {
        return getSharedPrefs(context).getString(ITEM_LINK, NO_LINK);
    }

    public static int getSavedPosition(@NonNull final Context context) {
        return getSharedPrefs(context).getInt(LIST_POSITION, 0);
    }

    public static int getSavedPadding(@NonNull final Context context) {
        return getSharedPrefs(context).getInt(LIST_PADDING, 0);
    }

    public static String getChannelFilter(@NonNull final Context context) {
        return getSharedPrefs(context).getString(CHANNEL_FILTER, ALL_CHANNELS);
    }

    public static void resetLink(@NonNull final Context context) {
        getEditor(context).remove(ITEM_LINK).apply();
    }

    public static void resetListPosition(@NonNull final Context context) {
        saveListPadding(context, 0);
        saveListPosition(context, 0);
    }


}
