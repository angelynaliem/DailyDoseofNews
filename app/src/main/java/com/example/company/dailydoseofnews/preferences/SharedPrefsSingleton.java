package com.example.company.dailydoseofnews.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import com.example.company.dailydoseofnews.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * I was using a static Context and received a warning about leaking a static Context object.
 * I've implemented a temporary solution by removing the static Context
 * & just requiring a Context in the methods I needed it in.
 */
public class SharedPrefsSingleton implements Preference.OnPreferenceChangeListener {

    private static SharedPrefsSingleton sharedPrefsInstance;
    private static SharedPreferences mSharedPreferences;

    private SharedPrefsSingleton(){}

    public static SharedPrefsSingleton getInstance(Context context){
        if (sharedPrefsInstance == null){
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            sharedPrefsInstance = new SharedPrefsSingleton();

        }
        return sharedPrefsInstance;
    }

    public void applyPreferenceValuesAndListener(Context context, Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        if (preference.getKey().equals(context.getString(R.string.num_of_pages_prefs_key))){
            String numPageString = getStringValue(preference.getKey(), "");
            onPreferenceChange(preference, numPageString);
        } else {
            // do nothing right now because the other preference is a MultiList &
            // that summary doesn't change.
        }
    }

    public String getStringValue(String key, String defValue){
        return mSharedPreferences.getString(key, defValue);
    }

    public ArrayList<String> getArrayFromSet(Context context, String key, int defaultArrayResourceId) {
        // Array list to be returned
        ArrayList<String> prefsArrayList = new ArrayList<>();
        // Get default Array to pass in the sharedPreferences below.
        Set<String> stringSet = new HashSet<>(Arrays.asList(context.getResources().getStringArray(defaultArrayResourceId)));
        // Get Set from preferences to add to the prefsArrayList.
        stringSet = mSharedPreferences.getStringSet(key, stringSet);
        // Add Set to ArrayList
        prefsArrayList.addAll(stringSet);
        Collections.sort(prefsArrayList);
        return prefsArrayList;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference instanceof ListPreference){
            preference.setSummary(newValue.toString());
        }
        return true;
    }
}
