package yuriy.rssreader.ui.activity_controllers;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.support.annotation.NonNull;
import yuriy.rssreader.services.Alarm;
import yuriy.rssreader.ui.SettingsActivity;
import yuriy.rssreader.ui.dialogs.ConfirmDialog;
import yuriy.rssreader.ui.dialogs.ToDoChannelDialog;

import java.util.Map;
import java.util.Set;

import static yuriy.rssreader.services.DatabaseOperationService.CHANNELS;

public final class SettingsListener implements
        Preference.OnPreferenceClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int DIALOG_THEME = 0;
    private static final String NO_TAG = "";
    private static final String CHANNEL_LIST_KEY = "yuriy.rssreader.ui.SettingsActivity.CHANNEL_LIST_KEY";
    private static final String CHANNELS_CATEGORY = "key_channels_list_preferences_screen";
    private static final String DELETE_ALL_ENTRIES = "key_eraseDatabase_preferences_screen";
    private static final String AUTO_REFRESH_SWITCH = "key_autoRefresh_preferences_screen";
    private static final String AUTO_REFRESH_PERIOD = "key_autoRefresh_period_preferences_screen";
    private static final String DEFAULT_PERIOD = "720";

    private final SettingsActivity activity;
    private final PreferenceCategory channelsCategory;


    public SettingsListener(final @NonNull SettingsActivity activity) {
        this.activity = activity;
        channelsCategory = (PreferenceCategory) activity.findPreference(CHANNELS_CATEGORY);
        fillCategory(channelsCategory);
    }

    @Override
    public boolean onPreferenceClick(final Preference preference) {
        DialogFragment dialog;
        switch (preference.getKey()) {
            case (DELETE_ALL_ENTRIES):
                dialog = new ConfirmDialog();
                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, DIALOG_THEME);
                dialog.show(activity.getFragmentManager(), NO_TAG);
                return true;
            case (CHANNEL_LIST_KEY):
                dialog = new ToDoChannelDialog();
                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, DIALOG_THEME);
                dialog.show(activity.getFragmentManager(), preference.getSummary().toString());
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        final Alarm alarm = new Alarm();
        if (AUTO_REFRESH_SWITCH.equals(key) || AUTO_REFRESH_PERIOD.equals(key)) {
            if (sharedPreferences.getBoolean(AUTO_REFRESH_SWITCH, false)) {
                int period;
                try {
                    period = Integer.parseInt(sharedPreferences.getString(AUTO_REFRESH_PERIOD, DEFAULT_PERIOD));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    period = Integer.parseInt(DEFAULT_PERIOD);
                }
                alarm.startAlarmService(activity, period);
            } else {
                alarm.stopAlarmService(activity);
            }
        } else {
            fillCategory(channelsCategory);
        }
    }

    private void fillCategory(final @NonNull PreferenceCategory preferenceCategory) {
        preferenceCategory.removeAll();
        final SharedPreferences channelsSharedPreferences =
                activity.getSharedPreferences(CHANNELS, Context.MODE_PRIVATE);
        channelsSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        final Map<String, ?> channelsMap = channelsSharedPreferences.getAll();
        final Set<String> channelUrlsSet = channelsMap.keySet();

        for (final String channelUrl : channelUrlsSet) {
            Preference channelEntry = new Preference(activity);
            channelEntry.setTitle(channelsSharedPreferences.getString(channelUrl, channelUrl));
            channelEntry.setKey(CHANNEL_LIST_KEY);
            channelEntry.setSummary(channelUrl);
            channelEntry.setOnPreferenceClickListener(this);
            preferenceCategory.addPreference(channelEntry);
        }
    }


}

