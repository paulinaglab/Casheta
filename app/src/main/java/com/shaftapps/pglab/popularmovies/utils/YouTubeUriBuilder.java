package com.shaftapps.pglab.popularmovies.utils;

import android.net.Uri;

/**
 * Utility for creating YouTube links.
 * <p/>
 * Created by Paulina on 2015-10-01.
 */
public class YouTubeUriBuilder {

    /**
     * Creates uri for YouTube movie.
     *
     * @param key YouTube movie's id
     * @return full uri to a movie.
     */
    public static Uri getUri(String key) {
        return new Uri.Builder()
                .scheme("http")
                .authority("www.youtube.com")
                .appendPath("watch")
                .appendQueryParameter("v", key)
                .build();
    }

    /**
     * Creates movie's thumbnail uri.
     *
     * @param key YouTube movie's id
     * @return uri to a movie thumbnail
     */
    public static Uri getThumbnailUri(String key) {
        return new Uri.Builder()
                .scheme("http")
                .authority("img.youtube.com")
                .appendPath("vi")
                .appendPath(key)
                .appendPath("0.jpg")
                .build();
    }
}
