package com.shaftapps.pglab.popularmovies.utils;

import android.net.Uri;

/**
 * Created by Paulina on 2015-10-01.
 */
public class YouTubeUriBuilder {

    public static Uri getUri(String key){
       return new Uri.Builder()
                .scheme("http")
                .authority("www.youtube.com")
                .appendPath("watch")
                .appendQueryParameter("v", key)
                .build();
    }

    public static Uri getThumbnailUri(String key){
        return new Uri.Builder()
                .scheme("http")
                .authority("img.youtube.com")
                .appendPath("vi")
                .appendPath(key)
                .appendPath("0.jpg")
                .build();
    }
}
