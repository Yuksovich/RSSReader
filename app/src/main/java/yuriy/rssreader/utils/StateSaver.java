package yuriy.rssreader.utils;

import android.content.Context;
import android.content.SharedPreferences;
import yuriy.rssreader.services.DatabaseOperationService;

public final class StateSaver {
    private static final String SAVED_STATE = "yuriy.rssreader.utils.StateSaver.SAVED_STATE";
    private static final String ITEM_LINK = "yuriy.rssreader.utils.StateSaver.ITEM_LINK";
    private static final String LIST_POSITION = "yuriy.rssreader.utils.StateSaver.LIST_POSITION";
    private static final String LIST_PADDING = "yuriy.rssreader.utils.StateSaver.LIST_PADDING";
    private static final String CHANNEL_FILTER = "yuriy.rssreader.utils.StateSaver.CHANNEL_FILTER";
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

    public static void saveLink(final Context context, final String itemLink) {
        getEditor(context).putString(ITEM_LINK, itemLink).apply();
    }

    public static void saveListPosition(final Context context, final int position) {
        getEditor(context).putInt(LIST_POSITION, position).apply();
    }

    public static void saveListPadding(final Context context, final int padding) {
        getEditor(context).putInt(LIST_PADDING, padding).apply();
    }

    public static void saveChannelFilter(final Context context, final String listFilter) {
        getEditor(context).putString(CHANNEL_FILTER, listFilter).apply();
    }

    public static String getSavedLink(final Context context) {
        return getSharedPrefs(context).getString(ITEM_LINK, NO_LINK);
    }

    public static int getSavedPosition(final Context context) {
        return getSharedPrefs(context).getInt(LIST_POSITION, 0);
    }

    public static int getSavedPadding(final Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SAVED_STATE, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(LIST_PADDING, 0);
    }

    public static String getChannelFilter(final Context context) {
        return getSharedPrefs(context).getString(CHANNEL_FILTER, DatabaseOperationService.ALL_CHANNELS);
    }

    public static void resetLink(final Context context) {
        getEditor(context).remove(ITEM_LINK).apply();
    }


}
