package com.example.company.dailydoseofnews.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.company.dailydoseofnews.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static android.content.ContentValues.TAG;


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

    public void applyPreferenceValuesAndListener(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        if (preference.getKey().equals(preference.getContext().getString(R.string.num_of_pages_prefs_key))) {
            String numPageString = getStringValue(preference.getKey(), "");
            onPreferenceChange(preference, numPageString);
        } else if (preference.getKey().equals(preference.getContext().getString(R.string.sections_prefs_key))) {
            MultiSelectListPreference multiSelectListPreference = (MultiSelectListPreference) preference;
            ArrayList<String> stringSet = getArrayFromSet(preference.getContext(),
                    preference.getContext().getString(R.string.sections_prefs_key),
                    R.array.default_news_array);
            onPreferenceChange(multiSelectListPreference, stringSet.toString());


        } else {

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
        } else if (preference instanceof MultiSelectListPreference){
            String prefString = newValue.toString().replace("[", "")
                    .replace("]", "");
            Log.d(TAG, "onPreferenceChange: PREF" + prefString);
            if (!TextUtils.isEmpty(prefString)){
                preference.setSummary(prefString);
            } else {
                preference.setSummary(R.string.no_sections_toast);
            }
        }
        return true;
    }
}
