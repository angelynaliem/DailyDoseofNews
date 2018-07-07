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
        implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final String TAG = "NewsFragment";

    private static final String KEY_EQUALS = "api-key";
    private static final String GUARDIAN_SEARCH_EQUALS = "content.guardianapis.com";
    private static final String SEARCH_PARAM = "search";
    private static final String HTTPS = "https";
    private static final String FORMAT = "format";
    private static final String JSON = "json";
    private static final String SHOW_FIELDS = "show-fields";
    private static final String THUMBNAIL = "thumbnail";
    private static final String SHOW_TAGS = "show-tags";
    private static final String CONTRIBUTOR = "contributor";

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

    private void initializeViews(View rootView) {
        mRecyclerView = rootView.findViewById(R.id.news_recycler_view);
        swipeRefreshLayout = rootView.findViewById(R.id.recycler_swipe_refresh);
        noNetworkView = rootView.findViewById(R.id.no_network_view);
        progressBar = rootView.findViewById(R.id.loading_bar);
    }

    /* Only the first fragment "latest news" will not have arguments.
     * All other fragments will use the argument to search for a section.
     */
    private void getArgumentStrings() {
        if (getArguments() != null) {
            String sectionString = getArguments().getString(PagerAdapterActivity.SECTION_KEY);
            myUrl = createSectionUrl(sectionString);
        } else {
            myUrl = createLatestNewsUrl();
        }
    }

    private String createSectionUrl(String section) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(HTTPS);
        builder.authority(GUARDIAN_SEARCH_EQUALS);
        builder.appendPath(section);
        builder.appendQueryParameter(SHOW_FIELDS, THUMBNAIL);
        builder.appendQueryParameter(SHOW_TAGS, CONTRIBUTOR);
        builder.appendQueryParameter(KEY_EQUALS, getString(R.string.guardian_api_key));
        return builder.toString();
    }

    private String createLatestNewsUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(HTTPS);
        builder.authority(GUARDIAN_SEARCH_EQUALS);
        builder.appendPath(SEARCH_PARAM);
        builder.appendQueryParameter(FORMAT, JSON);
        builder.appendQueryParameter(SHOW_FIELDS, THUMBNAIL);
        builder.appendQueryParameter(SHOW_TAGS, CONTRIBUTOR);
        builder.appendQueryParameter(KEY_EQUALS, getString(R.string.guardian_api_key));
        return builder.toString();
    }


    // Handles swipe refresh
    // If no network is found the refreshing will stop after 5 sec.
    private void setupSwipeRefresh(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!getLoaderManager().hasRunningLoaders() && NetworkUtils.isConnectedToNetwork(getContext())){
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
