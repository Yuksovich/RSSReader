package yuriy.rssreader.ui;


import android.os.Bundle;
import android.preference.PreferenceActivity;
import yuriy.rssreader.R;

public final class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

    }

}