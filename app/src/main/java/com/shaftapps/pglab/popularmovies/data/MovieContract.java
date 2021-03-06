package com.shaftapps.pglab.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the themoviedb.org database.
 *
 * Created by Paulina on 2015-09-11.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.shaftapps.pglab.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movies";
    public static final String PATH_REVIEW = "reviews";
    public static final String PATH_VIDEO = "videos";
    public static final String PATH_TRAILER = "trailers";
    public static final int PATH_REVIEW_MOVIE_ID_INDEX = 2;
    public static final int PATH_VIDEO_MOVIE_ID_INDEX = 2;
    public static final int PATH_TRAILER_MOVIE_ID_INDEX = 3;


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
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_POSTER_URL = "poster_url";
        public static final String COLUMN_BACKDROP_URL = "backdrop_url";
        public static final String COLUMN_AVERAGE_RATE = "average_rate";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_GENRE = "genre";
        public static final String COLUMN_COUNTRY = "countries";
        public static final String COLUMN_FAVORITE = "favorite";
        // This column specify movie is most popular by "popularity" value from API
        // or NULL if is not.
        public static final String COLUMN_MOST_POPULAR = "most_popular";
        // This column specify movie is one of the highest rated by index (order/its position on
        // highest rated list) or NULL if is not.
        public static final String COLUMN_HIGHEST_RATED = "highest_rated";


        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        // Table name
        public static final String TABLE_NAME = "review";


        // Columns declaration
        public static final String COLUMN_REVIEW_API_ID = "review_api_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";


        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildUriByMovieId(long movieId) {
            return CONTENT_URI
                    .buildUpon()
                    .appendPath(PATH_MOVIE)
                    .appendPath(Long.toString(movieId))
                    .build();
        }

        public static long getMovieIdFromUri(Uri uriByMovieId) {
            return Long.parseLong(uriByMovieId.getLastPathSegment());
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
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_TYPE = "type";


        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildUriByMovieId(long movieId) {
            return CONTENT_URI
                    .buildUpon()
                    .appendPath(PATH_MOVIE)
                    .appendPath(Long.toString(movieId))
                    .build();
        }

        public static Uri buildUriForMovieTrailer(long movieId) {
            return CONTENT_URI
                    .buildUpon()
                    .appendPath(PATH_TRAILER)
                    .appendPath(PATH_MOVIE)
                    .appendPath(Long.toString(movieId))
                    .build();
        }
    }
}
