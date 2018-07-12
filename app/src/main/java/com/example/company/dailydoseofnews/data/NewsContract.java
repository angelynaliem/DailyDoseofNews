package com.example.company.dailydoseofnews.data;

public final class NewsContract {

    private NewsContract(){}

    public static final class UrlParams{

        public static final String SCHEME = "https";
        public static final String AUTHORITY = "content.guardianapis.com";
        public static final String SEARCH_PARAM = "search";
        public static final String SHOW_FIELDS = "show-fields";
        public static final String PAGE_SIZE = "page-size";
        public static final String SHOW_TAGS = "show-tags";
        public static final String FORMAT = "show-tags";
        public static final String Q_PARAM = "q";
        public static final String API_KEY_PARAM = "api-key";

    }
}
