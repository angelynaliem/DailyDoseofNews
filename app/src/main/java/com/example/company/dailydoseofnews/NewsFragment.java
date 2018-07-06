package com.example.company.dailydoseofnews;


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

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<News>>{


    private static final String GUARDIAN_API_STARTING_POINT = "https://content.guardianapis.com/";
    private static final String SEARCH = "search";
    private static final String SHOW_THUMBNAIL_URL = "show-fields=thumbnail";
    private static final String SHOW_CONTRIBUTOR_URL = "show-tags=contributor";
    private static final String KEY_EQUALS = "api-key=";
    private static final String AND = "&";
    private static final String QUESTION = "?";
    private RecyclerView mRecyclerView;
    private NewsAdapter newsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View noNetworkView;
    private ProgressBar progressBar;
    private String myUrl;

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recylcer_view_layout, container, false);
        getArgumentStrings();
        initializeViews(rootView);
        setupSwipeRefresh();
        checkNetworkAndStartLoader();
        return rootView;
    }

    private void initializeViews(View rootView){
        mRecyclerView = rootView.findViewById(R.id.news_recycler_view);
        swipeRefreshLayout = rootView.findViewById(R.id.recycler_swipe_refresh);
        noNetworkView = rootView.findViewById(R.id.no_network_view);
        progressBar = rootView.findViewById(R.id.loading_bar);
    }

    /* Only the first fragment "latest news" will not have arguments.
     * All other fragments will use the argument to search for a section.
     */
    private void getArgumentStrings(){
        if (getArguments() != null){
            String sectionString = getArguments().getString(PagerAdapterActivity.SECTION_KEY);
            myUrl = createSectionUrl(sectionString);
        } else {
            myUrl = createLatestNewsUrl();
        }
    }

    private String createSectionUrl(String section){
        StringBuilder sectionUrl = new StringBuilder();
        sectionUrl.append(GUARDIAN_API_STARTING_POINT);
        sectionUrl.append(section);
        sectionUrl.append(QUESTION);
        sectionUrl.append(KEY_EQUALS);
        sectionUrl.append(getString(R.string.guardian_api_key));
        sectionUrl.append(AND);
        sectionUrl.append(SHOW_THUMBNAIL_URL);
        sectionUrl.append(AND);
        sectionUrl.append(SHOW_CONTRIBUTOR_URL);
        return sectionUrl.toString();
    }

    private String createLatestNewsUrl(){
        StringBuilder latestNewsUrl = new StringBuilder();
        latestNewsUrl.append(GUARDIAN_API_STARTING_POINT);
        latestNewsUrl.append(SEARCH);
        latestNewsUrl.append(QUESTION);
        latestNewsUrl.append(KEY_EQUALS);
        latestNewsUrl.append(getString(R.string.guardian_api_key));
        latestNewsUrl.append(AND);
        latestNewsUrl.append(SHOW_THUMBNAIL_URL);
        latestNewsUrl.append(AND);
        latestNewsUrl.append(SHOW_CONTRIBUTOR_URL);
        return latestNewsUrl.toString();
    }

    private void setupSwipeRefresh(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!getLoaderManager().hasRunningLoaders()){
                    progressBar.setVisibility(View.VISIBLE);
                    checkNetworkAndStartLoader();
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
                if (view.getId() == R.id.news_share_button){
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, currentNews.getNewsTitle() +
                            "\n" + currentNews.getWebUrl());
                    shareIntent.setType("text/plain");
                    if (shareIntent.resolveActivity(getContext().getPackageManager()) != null){
                        startActivity(shareIntent);
                    }
                } else if (view.getId() == R.id.news_bookmark) {
                    Toast.makeText(getContext(), R.string.bookmarks_soon, Toast.LENGTH_SHORT).show();
                }
                else {
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