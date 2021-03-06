package com.example.company.dailydoseofnews.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.company.dailydoseofnews.adapter.PagerAdapterActivity;
import com.example.company.dailydoseofnews.data.NewsContract.UrlParams;
import com.example.company.dailydoseofnews.network.NetworkUtils;
import com.example.company.dailydoseofnews.News;
import com.example.company.dailydoseofnews.adapter.NewsAdapter;
import com.example.company.dailydoseofnews.interfaces.NewsInterface;
import com.example.company.dailydoseofnews.NewsLoader;
import com.example.company.dailydoseofnews.R;
import com.example.company.dailydoseofnews.preferences.SharedPrefsSingleton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<News>> {

    private RecyclerView mRecyclerView;
    private NewsAdapter newsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View noNetworkView;
    private ProgressBar progressBar;
    private String myUrl;
    private SharedPrefsSingleton sharedPrefsSingleton;

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recylcer_view_layout, container, false);
        sharedPrefsSingleton = SharedPrefsSingleton.getInstance(getContext());

        getPagerAdapterPosition();
        initializeViews(rootView);
        setupSwipeRefresh();
        checkNetworkAndStartLoader();
        return rootView;
    }

    private void initializeViews(View rootView) {
        mRecyclerView = rootView.findViewById(R.id.news_recycler_view);
        swipeRefreshLayout = rootView.findViewById(R.id.recycler_swipe_refresh);
        noNetworkView = rootView.findViewById(R.id.no_network_view);
        progressBar = rootView.findViewById(R.id.loading_bar);
    }

    /** Gets the current position of the PagerAdapter that is used in the
     * @createSectionUrl
     */
    private void getPagerAdapterPosition() {
        if (getArguments() != null) {
            int position = getArguments().getInt(PagerAdapterActivity.PREFS_POSITION_KEY);
            myUrl = createSectionUrl(position);
        }
    }

    /* Creates the URL using the sections from the SharedPreferences. */
    private String createSectionUrl(int position) {
        String pageSize = sharedPrefsSingleton.getStringValue(
                getString(R.string.num_of_pages_prefs_key),
                getString(R.string.page_default_value));
        ArrayList<String> sectionArrayList = sharedPrefsSingleton.getArrayFromSet(
                getContext(),
                getString(R.string.sections_prefs_key),
                R.array.default_news_array);

        // This gets the current position of the ArrayList of user prefs
        // to load the correct section.
        String currentSection = sectionArrayList.get(position);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(UrlParams.SCHEME);
        builder.authority(UrlParams.AUTHORITY);
        // Some sections have two paths for example: "sport/mlb"
        // This appends two paths if it contains a "/"
        if (currentSection.contains("/")){
            String[] sectionArray = currentSection.split("/");
            builder.appendPath(sectionArray[0]);
            builder.appendPath(sectionArray[1]);
        } else {
            // otherwise it will contain one word e.g. "film"
            builder.appendPath(currentSection);
        }
        builder.appendQueryParameter(UrlParams.SHOW_FIELDS, "thumbnail");
        builder.appendQueryParameter(UrlParams.PAGE_SIZE, pageSize);
        builder.appendQueryParameter(UrlParams.SHOW_TAGS, "contributor");
        builder.appendQueryParameter(UrlParams.API_KEY_PARAM, getString(R.string.guardian_api_key));
        return builder.toString();
    }

    // Handles swipe refresh
    // If no network is found the refreshing will stop after 5 sec.
    private void setupSwipeRefresh(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!getLoaderManager().hasRunningLoaders() && NetworkUtils.isConnectedToNetwork(getContext())){
                    checkNetworkAndRestartLoader();
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            checkNetworkAndStartLoader();
                        }
                    }, 5000);
                }
            }
        });
    }

    // Starts loader if there is a network connection.
    // Checks if the list is populated to prevent the noNetworkView to overlay
    // news articles that may have been loaded before network interruption.
    private void checkNetworkAndStartLoader(){
        if (NetworkUtils.isConnectedToNetwork(getContext())){
            getLoaderManager().initLoader(2, null, this);
            noNetworkView.setVisibility(View.GONE);
        } else if (!NetworkUtils.isConnectedToNetwork(getContext())){
            if (newsAdapter != null && newsAdapter.getItemCount() > 0){
                noNetworkView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                noNetworkView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void checkNetworkAndRestartLoader(){
        if (NetworkUtils.isConnectedToNetwork(getContext())){
            getLoaderManager().restartLoader(2, null, this);
            noNetworkView.setVisibility(View.GONE);
        } else if (NetworkUtils.isConnectedToNetwork(getContext()) == false){
            if (newsAdapter != null && newsAdapter.getItemCount() > 0){
                noNetworkView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                noNetworkView.setVisibility(View.VISIBLE);
            }
        }
    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int id, @Nullable Bundle args) {
        return new NewsLoader(getContext(), myUrl);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, final List<News> data) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        // Handles share button or opens web page of the current article.
        newsAdapter = new NewsAdapter(getContext(), data, new NewsInterface() {
            @Override
            public void onItemClick(View view, int position) {
                News currentNews = data.get(position);
                    // Share Intent
                if (view.getId() == R.id.news_share_button){
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, currentNews.getNewsTitle() +
                            "\n" + currentNews.getWebUrl());
                    shareIntent.setType("text/plain");
                    if (shareIntent.resolveActivity(getContext().getPackageManager()) != null){
                        startActivity(shareIntent);
                    }
                    // Bookmark Toast
                } else if (view.getId() == R.id.news_bookmark) {
                    Toast.makeText(getContext(), R.string.bookmarks_soon, Toast.LENGTH_SHORT).show();
                }
                else {
                    // Website Intent
                    String websiteUrl = currentNews.getWebUrl();
                    Intent webIntent = new Intent();
                    webIntent.setData(Uri.parse(websiteUrl));
                    if (webIntent.resolveActivity(getContext().getPackageManager()) != null){
                        startActivity(webIntent);
                    }
                }
            }
        });
        mRecyclerView.setAdapter(newsAdapter);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {

    }
}
