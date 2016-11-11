package yuriy.rssreader.ui;


import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import yuriy.rssreader.R;
import yuriy.rssreader.ui.dialogs.ConfirmDialog;
import yuriy.rssreader.ui.dialogs.ToDoChannelDialog;

import java.util.Map;
import java.util.Set;

public final class SettingsActivity extends PreferenceActivity {
    private final static int DIALOG_THEME = 0;
    private final static String CHANNELS_CATEGORY = "key_channels_list_preferences_screen";
    private final static String CHANNELS = "channels";
    private final static String DELETE_ALL_ENTRIES = "key_eraseDatabase_preferences_screen";
    private static final String NO_TAG = "";
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        final PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference(CHANNELS_CATEGORY);

        final Preference deleteAllEntries = findPreference(DELETE_ALL_ENTRIES);
        deleteAllEntries.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DialogFragment dialog = new ConfirmDialog();
                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, DIALOG_THEME);
                dialog.show(getFragmentManager(), NO_TAG);
                return true;
            }
        });

        sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                fillCategory(preferenceCategory);
            }
        };
        fillCategory(preferenceCategory);

    }

    private void fillCategory(final PreferenceCategory preferenceCategory) {
        preferenceCategory.removeAll();
        final SharedPreferences channelsSharedPreferences = getSharedPreferences(CHANNELS, Context.MODE_PRIVATE);
        channelsSharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        final Map<String, ?> channelsMap = channelsSharedPreferences.getAll();
        final Set<String> channelUrlsSet = channelsMap.keySet();

        for (final String channelUrl : channelUrlsSet) {
            Preference channelEntry = new Preference(this);
            channelEntry.setTitle(channelsSharedPreferences.getString(channelUrl, channelUrl));
            channelEntry.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    DialogFragment dialog = new ToDoChannelDialog();
                    dialog.setStyle(DialogFragment.STYLE_NO_TITLE, DIALOG_THEME);
                    dialog.show(getFragmentManager(), channelUrl);
                    return true;
                }
            });
            preferenceCategory.addPreference(channelEntry);
        }
    }

}