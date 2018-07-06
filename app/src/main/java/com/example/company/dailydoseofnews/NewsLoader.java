package com.example.company.dailydoseofnews;


import android.content.Context;
import java.util.List;

public class NewsLoader extends android.support.v4.content.AsyncTaskLoader{

    private String mUrl;
    private Context mContext;

    public NewsLoader(Context context, String url) {
        super(context);
        mContext = context;
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
        List<News> newsList = NetworkUtils.getNewsData(mContext, mUrl);
        return newsList;
    }
}
