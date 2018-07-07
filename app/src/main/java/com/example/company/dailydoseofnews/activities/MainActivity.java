package com.example.company.dailydoseofnews.activities;


import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.company.dailydoseofnews.R;
import com.example.company.dailydoseofnews.adapter.PagerAdapterActivity;

public class MainActivity extends AppCompatActivity {

    public static final String QUERY_KEY = "queryKey";

    private SearchView searchView;
    private AppBarLayout appBarLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Bitmap appIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        setupViewPager();
        setupSearchView();
        setupQueryTextListener();
        setOnTabListener();
    }

    private void initializeViews(){
        searchView = findViewById(R.id.my_search_view);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        appBarLayout = findViewById(R.id.main_app_bar_layout);
        appIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Toolbar toolbar = findViewById(R.id.main_tool_bar);
        setSupportActionBar(toolbar);
    }

    private void setupViewPager(){
        PagerAdapterActivity pagerAdapter = new PagerAdapterActivity(this, getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    /* Shows app name as search hint when the SearchView is empty and does not have focus
     * or displays "enter keywords" when the SearchView has focus */
    private void setupSearchView(){
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    searchView.setQueryHint(getString(R.string.query_hint));
                } else {
                    searchView.setQueryHint(getResources().getString(R.string.app_name));
                }
            }
        });
    }

    private void setupQueryTextListener(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewQuery(query);
                searchView.clearFocus();
                searchView.setQuery("", false);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    // Start SearchResultsActivity with search query.
    private void searchViewQuery(String query){
        Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
        intent.putExtra(QUERY_KEY, query);
        startActivity(intent);
    }

    // Changes tab & window colors depending on subject.
    private void setOnTabListener(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int currentTabPosition = tab.getPosition();
                int primaryDarkColor;
                int primaryColor;
                if (currentTabPosition == 0 || currentTabPosition == 1){
                    primaryColor = R.color.newsColorRed;
                    primaryDarkColor = R.color.newsDarkColorRed;
                } else if (currentTabPosition == 2 || currentTabPosition == 3){
                    primaryColor = R.color.newsColorTeal;
                    primaryDarkColor = R.color.newsDarkColorTeal;
                } else if (currentTabPosition == 4 || currentTabPosition == 5){
                    primaryColor = R.color.newsColorPink;
                    primaryDarkColor = R.color.newsDarkColorPink;
                } else if (currentTabPosition == 6 || currentTabPosition == 7){
                    primaryColor = R.color.newsColorBlue;
                    primaryDarkColor = R.color.newsDarkColorBlue;
                } else {
                    primaryColor = R.color.newsColorBlue;
                    primaryDarkColor = R.color.newsDarkColorBlue;
                }
                setTaskDescription(new ActivityManager.TaskDescription(getString(R.string.app_name),
                        appIcon,
                        getResources().getColor(primaryDarkColor)));
                tabLayout.setBackgroundColor(getResources().getColor(primaryColor));
                appBarLayout.setBackgroundColor(getResources().getColor(primaryColor));
                getWindow().setStatusBarColor(getResources().getColor(primaryDarkColor));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
