package com.example.company.dailydoseofnews;


import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

public class SearchResultsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final String TAG = "SearchResultsActivity";

    private static final String GUARDIAN_SEARCH_EQUALS = "https://content.guardianapis.com/search?q=";
    private static final String FORMAT = "format=json";
    private static final String SHOW_THUMBNAIL_URL = "show-fields=thumbnail";
    private static final String SHOW_CONTRIBUTOR_URL = "show-tags=contributor";
    private static final String KEY_EQUALS = "api-key=";
    private static final String AND = "&";
    private String searchQuery;
    private RecyclerView mRecyclerView;
    private ProgressBar loadingBar;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        initializeViews();
        setupToolbar();
        String query = getIntentQuery();
        searchQuery = createUrl(query);
        setupQueryTextListener(query);
        checkForActiveNetwork();
    }

    private void initializeViews(){
        mRecyclerView = findViewById(R.id.search_recycler_view);
        searchView = findViewById(R.id.search_results_search_view);
        loadingBar = findViewById(R.id.search_loading_bar);
    }

    private void setupToolbar(){
        Toolbar myToolBar = findViewById(R.id.search_tool_bar);
        setSupportActionBar(myToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private String createUrl(String searchQuery){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(GUARDIAN_SEARCH_EQUALS);
        stringBuilder.append(searchQuery.trim());
        stringBuilder.append(AND);
        stringBuilder.append(FORMAT);
        stringBuilder.append(AND);
        stringBuilder.append(SHOW_THUMBNAIL_URL);
        stringBuilder.append(AND);
        stringBuilder.append(SHOW_CONTRIBUTOR_URL);
        stringBuilder.append(AND);
        stringBuilder.append(KEY_EQUALS);
        stringBuilder.append(getResources().getString(R.string.guardian_api_key));
        return stringBuilder.toString();
    }

    private String getIntentQuery(){
        if (getIntent() != null){
            String intentQuery = getIntent().getExtras().getString(MainActivity.QUERY_KEY);
            return intentQuery;
        } else {
            return null;
        }
    }

    /*
     * Gets search query from MainActivity to display.
     * Sets SearchView listener to handle additional searches.
     */
    private void setupQueryTextListener(String query){
        searchView.setQuery(query, false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = createUrl(query);
                Log.d(TAG, "onQueryTextSubmit: searchQuery = " + searchQuery);
                restartLoader();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void checkForActiveNetwork(){
        if (NetworkUtils.isConnectedToNetwork(this)){
            getSupportLoaderManager().initLoader(1, null, this);
        } else {
            loadingBar.setVisibility(View.GONE);
            Toast.makeText(this,
                    R.string.no_connection_toast,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        return new NewsLoader(this, searchQuery);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, final List<News> data) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        NewsAdapter newsAdapter = new NewsAdapter(this, data, new NewsInterface() {
            @Override
            public void onItemClick(View view, int position) {

                News currentNews = data.get(position);
                if (view.getId() == R.id.news_share_button){
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, currentNews.getNewsTitle() +
                            "\n" + currentNews.getWebUrl());
                    shareIntent.setType("text/plain");
                    if (shareIntent.resolveActivity(getApplication().getPackageManager()) != null){
                        startActivity(shareIntent);
                    }
                } else if (view.getId() == R.id.news_bookmark){
                    Toast.makeText(getApplication(), R.string.bookmarks_soon, Toast.LENGTH_SHORT).show();
                }
                else {
                    String websiteUrl = currentNews.getWebUrl();
                    Intent webIntent = new Intent();
                    webIntent.setData(Uri.parse(websiteUrl));
                    if (webIntent.resolveActivity(getApplication().getPackageManager()) != null){
                        startActivity(webIntent);
                    }
                }
            }
        });

        mRecyclerView.setAdapter(newsAdapter);
        if (newsAdapter.getItemCount() > 0){
            loadingBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {

    }

    private void restartLoader(){
        if (NetworkUtils.isConnectedToNetwork(this)){
            getSupportLoaderManager().restartLoader(1, null, this);
        } else {
            loadingBar.setVisibility(View.GONE);
            Toast.makeText(this,
                    R.string.no_connection_toast,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
