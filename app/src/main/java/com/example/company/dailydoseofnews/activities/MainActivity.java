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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.company.dailydoseofnews.R;
import com.example.company.dailydoseofnews.adapter.PagerAdapterActivity;


public class MainActivity extends AppCompatActivity {

    public static final String QUERY_KEY = "queryKey";
    private static final String TAG = "MainActivity";
    private SearchView searchView;
    private AppBarLayout appBarLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Bitmap appIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ONCREATE!!");
        initializeViews();
        setupViewPager();
        setupSearchView();
        setupQueryTextListener();
        setOnTabListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings_overflow){
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void setOnTabListener(){
        // This sets the first tabColor when called from onCreate();
        int firstTab = tabLayout.getSelectedTabPosition();
        TabLayout.Tab tab = tabLayout.getTabAt(firstTab);
        String tabString = tab.getText().toString();
        findColors(tabString);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                String currentTabText = tab.getText().toString().trim();
                findColors(currentTabText);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    // Determine what colors to use depending on the Tab text.
    private void findColors(String currentTabText){
        int primaryDarkColor;
        int primaryColor;
        if (currentTabText.equals("us-news") || currentTabText.equals("world")
                || currentTabText.equals("business") || currentTabText.equals("technology")){
            primaryColor = R.color.colorRed;
            primaryDarkColor = R.color.colorRedDark;
        } else if (currentTabText.equals("games") || currentTabText.equals("environment")
                || currentTabText.equals("film") || currentTabText.equals("music")){
            primaryColor = R.color.colorCyan;
            primaryDarkColor = R.color.colorCyanDark;
        } else if (currentTabText.equals("food-and-drink") || currentTabText.equals("family")
                || currentTabText.equals("fashion") || currentTabText.equals("health-and-wellbeing")){
            primaryColor = R.color.colorPurple;
            primaryDarkColor = R.color.colorPurpleDark;
        } else if (currentTabText.equals("nfl") || currentTabText.equals("football")
                || currentTabText.equals("mlb") || currentTabText.equals("nba")
                || currentTabText.equals("nhl")){
            primaryColor = R.color.colorBlue;
            primaryDarkColor = R.color.colorBlueDark;
        } else {
            primaryColor = R.color.colorCyan;
            primaryDarkColor = R.color.colorCyanDark;
        }
        applyColors(primaryColor, primaryDarkColor);
    }

    // Apply's TabLayout, ActionBar, Window, & StatusBar colors depending on subject.
    private void applyColors(int primaryColor, int primaryDarkColor){
        setTaskDescription(new ActivityManager.TaskDescription(getString(R.string.app_name),
                appIcon,
                getResources().getColor(primaryDarkColor)));
        tabLayout.setBackgroundColor(getResources().getColor(primaryColor));
        appBarLayout.setBackgroundColor(getResources().getColor(primaryColor));
        getWindow().setStatusBarColor(getResources().getColor(primaryDarkColor));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
