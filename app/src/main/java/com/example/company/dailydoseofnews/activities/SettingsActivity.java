package com.example.company.dailydoseofnews.activities;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.company.dailydoseofnews.R;
import com.example.company.dailydoseofnews.preferences.SharedPrefsSingleton;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_prefs);
            SharedPrefsSingleton prefsSingleton = SharedPrefsSingleton.getInstance(getActivity());
            Preference articleNumber = findPreference(getString(R.string.num_of_pages_prefs_key));
            Preference sectionsPref = findPreference(getString(R.string.sections_prefs_key));
            prefsSingleton.applyPreferenceValuesAndListener(articleNumber);
            prefsSingleton.applyPreferenceValuesAndListener(sectionsPref);
        }
    }
}
