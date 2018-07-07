package com.example.company.dailydoseofnews.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.company.dailydoseofnews.fragments.NewsFragment;
import com.example.company.dailydoseofnews.R;

public class PagerAdapterActivity extends FragmentPagerAdapter{

    public static final String LATEST_NEWS_KEY = "latestNewsKey";
    public static final String SECTION_KEY = "sectionKey";
    private Context context;
    private int[] categoryNameIdArray = {R.string.latest, R.string.politics, R.string.music,
            R.string.games, R.string.travel, R.string.technology, R.string.sport};
    public PagerAdapterActivity(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    /* First Tab will always display "latest news", which uses the latestNewsUrl String.
     * All other tabs will use a section URL from the categoryNameIdArray.
     */
    @Override
    public Fragment getItem(int position) {
        NewsFragment newsFragment = new NewsFragment();
        if (position == 0){
            return newsFragment;
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(SECTION_KEY, context.getString(categoryNameIdArray[position]));
            bundle.putString(LATEST_NEWS_KEY, null);
            newsFragment.setArguments(bundle);
            return newsFragment;
        }
    }

    @Override
    public int getCount() {
        return categoryNameIdArray.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return (context.getString(categoryNameIdArray[position]));
    }
}
