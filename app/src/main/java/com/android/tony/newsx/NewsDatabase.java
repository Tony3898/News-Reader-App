package com.android.tony.newsx;

import android.net.Uri;

public class NewsDatabase {
    private Uri uriImage, uriUrl;
    private String newsTitle, newsCategories, newsDate;

    NewsDatabase(Uri uriImage, String newsTitle, String newsCatgories, String newsDate, Uri uriUrl) {
        this.uriImage = uriImage;
        this.newsTitle = newsTitle;
        this.newsCategories = newsCatgories;
        this.newsDate = newsDate;
        this.uriUrl = uriUrl;
    }

    public Uri getUriImage() {
        return uriImage;
    }

    public String getNewsCategories() {
        return newsCategories;
    }

    public String getNewsDate() {
        return newsDate;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public Uri getUriUrl() {
        return uriUrl;
    }
}
