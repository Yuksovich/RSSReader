package yuriy.rssreader.ui.settings_activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import yuriy.rssreader.R;
import yuriy.rssreader.utils.Theme;

public final class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        if(Theme.getId()!=0){
            setTheme(Theme.getId());
        }
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        final SettingsListener settingsListener = new SettingsListener(this);

        final Preference deleteAllEntries = findPreference(getString(R.string.key_eraseDatabase_preferences_screen));
        deleteAllEntries.setOnPreferenceClickListener(settingsListener);

        final SharedPreferences settingsPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        settingsPreferences.registerOnSharedPreferenceChangeListener(settingsListener);

    }


}