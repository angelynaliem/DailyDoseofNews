package com.example.company.dailydoseofnews.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.company.dailydoseofnews.preferences.SharedPrefsSingleton;
import com.example.company.dailydoseofnews.fragments.NewsFragment;
import com.example.company.dailydoseofnews.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PagerAdapterActivity extends FragmentPagerAdapter {

    public static final String PREFS_POSITION_KEY = "prefsPositionKey";
    private ArrayList<String> sectionsArrayList;
    private Context context;

    public PagerAdapterActivity(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        SharedPrefsSingleton sharedPrefsSingleton = SharedPrefsSingleton.getInstance(context);
        sectionsArrayList = sharedPrefsSingleton.getArrayFromSet(context, context.getString(R.string.sections_prefs_key),
                R.array.default_news_array);
    }

    /* First Tab will always display "latest news", which uses the latestNewsUrl String.
     * All other tabs will use a section URL from the categoryNameIdArray.
     */
    @Override
    public Fragment getItem(int position) {
        NewsFragment newsFragment = new NewsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PREFS_POSITION_KEY, position);
        newsFragment.setArguments(bundle);
        return newsFragment;
    }

    @Override
    public int getCount() {
       return sectionsArrayList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String currentString = sectionsArrayList.get(position);
        if (currentString.contains("/")) {
            String[] correctTitle = currentString.split("/");
            return correctTitle[1];
        } else {
            return currentString;
        }
    }


}


