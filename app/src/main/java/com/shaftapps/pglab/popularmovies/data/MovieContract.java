package com.shaftapps.pglab.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Paulina on 2015-09-11.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.shaftapps.pglab.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_REVIEW = "review";
    public static final String PATH_VIDEO = "video";
    public static final int PATH_REVIEW_MOVIE_ID_INDEX = 1;
    public static final int PATH_VIDEO_MOVIE_ID_INDEX = 1;


    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;


        // Table name
        public static final String TABLE_NAME = "movie";


        // Columns declaration
        public static final String COLUMN_MOVIE_API_ID = "movie_api_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_POSTER_URL = "poster_url";
        public static final String COLUMN_BACKDROP_URL = "backdrop_url";
        public static final String COLUMN_AVERAGE_RATE = "average_rate";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_FAVORITE = "favorite";


        public static Uri buildUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        // Table name
        public static final String TABLE_NAME = "review";


        // Columns declaration
        public static final String COLUMN_REVIEW_API_ID = "review_api_id";
        public static final String COLUMN_MOVIE_API_ID = "movie_api_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";


        public static Uri buildUri(long movieApiId, long id){
            return MovieEntry
                    .buildUri(movieApiId)
                    .buildUpon()
                    .appendPath(PATH_REVIEW)
                    .appendPath(Long.toString(id))
                    .build();
        }
    }


    public static final class VideoEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;

        // Table name
        public static final String TABLE_NAME = "video";


        // Columns declaration
        public static final String COLUMN_VIDEO_API_ID = "video_api_id";
        public static final String COLUMN_MOVIE_API_ID = "movie_api_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_TYPE = "type";


        public static Uri buildUri(long movieApiId, long id){
            return MovieEntry
                    .buildUri(movieApiId)
                    .buildUpon()
                    .appendPath(PATH_VIDEO)
                    .appendPath(Long.toString(id))
                    .build();
        }
    }
}
