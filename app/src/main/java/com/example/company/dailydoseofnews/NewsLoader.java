package com.example.company.dailydoseofnews;


import android.content.Context;
import com.example.company.dailydoseofnews.network.NetworkUtils;

import java.util.List;

public class NewsLoader extends android.support.v4.content.AsyncTaskLoader{

    private String mUrl;

    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (mUrl == null){
            return null;
        }
        List<News> newsList = NetworkUtils.getNewsData(mUrl);
        return newsList;
    }
}
