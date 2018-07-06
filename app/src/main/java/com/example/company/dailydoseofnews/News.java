package com.example.company.dailydoseofnews;

public class News {

    private String mTitle;
    private String mAuthor;
    private String mImageUrl;
    private String mDate;
    private String mWebUrl;

    public News(String articleTitle, String articleAuthor,
                String articleImageUrl, String articleDate, String webUrl){
        mTitle = articleTitle;
        mAuthor = articleAuthor;
        mImageUrl = articleImageUrl;
        mDate = articleDate;
        mWebUrl = webUrl;
    }

    public String getNewsTitle(){
        return mTitle;
    }

    public String getNewsAuthor(){
        return mAuthor;
    }

    public String getNewsImageUrl(){
        return mImageUrl;
    }

    public String getNewsDate(){
        return mDate;
    }

    public String getWebUrl(){
        return mWebUrl;
    }
}
