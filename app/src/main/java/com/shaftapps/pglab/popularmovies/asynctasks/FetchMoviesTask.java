package com.shaftapps.pglab.popularmovies.asynctasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.util.Log;

import com.shaftapps.pglab.popularmovies.data.MovieContract;
import com.shaftapps.pglab.popularmovies.utils.MovieDBResponseParser;

import org.json.JSONException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * Class for fetching movies by category (sorting mode).
 * <p/>
 * Created by Paulina on 2015-09-01.
 */
public class FetchMoviesTask extends BaseMovieDBTask {

    // For this app version I always download same page count of
    // most popular or highest rated movies.
    private static final int PAGE_COUNT = 3;

    // URL constant parts
    private static final String SEARCH_METHOD = "discover";
    private static final String MOVIE = "movie";
    private static final String SORT_BY = "sort_by";
    private static final String POPULARITY = "popularity.desc";
    private static final String VOTE_AVERAGE = "vote_average.desc";
    private static final String VOTE_MIN = "vote_count.gte";
    private static final String VOTE_MIN_VALUE = "1000";
    private static final String PAGE = "page";

    private Context context;
    @QueryType
    private int queryType;


    public FetchMoviesTask(int id, Context context, @QueryType int queryType) {
        super(id);
        this.context = context;
        this.queryType = queryType;
    }


    //
    //  ASYNC TASK METHODS
    //

    @Override
    protected Boolean doInBackground(String[] params) {
        // Clearing cache
        clearCache();

        for (int page = 1; page <= PAGE_COUNT; page++) {
            // Fetching data
            String jsonStr = fetchData(getUrlWithPage(page));
            if (jsonStr == null)
                return false;

            // Parsing
            ArrayList<ContentValues> parsedData = getParsedData(jsonStr);

            // Saving to database
            saveToDatabase(parsedData);
            if (parsedData == null)
                return false;
        }

        return true;
    }


    //
    //  HELPER METHODS
    //


    @Override
    protected void clearCache() {
        switch (queryType) {
            case MOST_POPULAR: {
                // Delete all movies, which are in database only because they're most popular.
                context.getContentResolver().delete(
                        MovieContract.MovieEntry.CONTENT_URI,
                        MovieContract.MovieEntry.COLUMN_HIGHEST_RATED + " IS NULL AND " +
                                MovieContract.MovieEntry.COLUMN_FAVORITE + " IS NULL",
                        null);
                // Overwrite (reset) popularity in remaining movies.
                ContentValues contentValues = new ContentValues();
                contentValues.putNull(MovieContract.MovieEntry.COLUMN_MOST_POPULAR);
                context.getContentResolver().update(
                        MovieContract.MovieEntry.CONTENT_URI,
                        contentValues,
                        null,
                        null);
                break;
            }
            case HIGHEST_RATED: {
                // Delete all movies, which are in database only because they're highest rated.
                context.getContentResolver().delete(
                        MovieContract.MovieEntry.CONTENT_URI,
                        MovieContract.MovieEntry.COLUMN_MOST_POPULAR + " IS NULL AND " +
                                MovieContract.MovieEntry.COLUMN_FAVORITE + " IS NULL",
                        null);
                // Overwrite (reset) highest rated order value in remaining movies.
                ContentValues contentValues = new ContentValues();
                contentValues.putNull(MovieContract.MovieEntry.COLUMN_HIGHEST_RATED);
                context.getContentResolver().update(
                        MovieContract.MovieEntry.CONTENT_URI,
                        contentValues,
                        null,
                        null);
                break;
            }
            default:
                throw new UnsupportedOperationException(
                        "Unsupported query type: " + queryType);
        }
    }

    /**
     * Builds url string by appending 'page' query parameter to base discover url.
     *
     * @param page number of page.
     * @return complete query url with page number.
     */
    private String getUrlWithPage(int page) {
        return Uri.parse(getUrl())
                .buildUpon()
                .appendQueryParameter(PAGE, Integer.toString(page))
                .build().toString();
    }


    /**
     * Method returning url with query to the API.
     *
     * @return specific url query
     */
    protected String getUrl() {
        switch (queryType) {
            case MOST_POPULAR:
                return getUriBuilder()
                        .appendQueryParameter(SORT_BY, POPULARITY)
                        .build().toString();
            case HIGHEST_RATED:
                return getUriBuilder()
                        .appendQueryParameter(SORT_BY, VOTE_AVERAGE)
                        .appendQueryParameter(VOTE_MIN, VOTE_MIN_VALUE)
                        .build().toString();
            default:
                throw new UnsupportedOperationException(
                        "Unsupported query type: " + queryType);
        }
    }

    @Override
    protected Uri.Builder getUriBuilder() {
        return super.getUriBuilder()
                .appendPath(SEARCH_METHOD)
                .appendPath(MOVIE);
    }

    @Override
    protected ArrayList<ContentValues> getParsedData(String json) {
        try {
            return MovieDBResponseParser.getMoviesFromJson(json, queryType);
        } catch (JSONException e) {
            Log.e(this.getClass().getName(), "Error parsing JSON", e);
            return null;
        }
    }

    @Override
    protected void saveToDatabase(ArrayList<ContentValues> contentValues) {
        // Insert new data from API to database or
        // update if they're used by another category
        context.getContentResolver().bulkInsert(
                MovieContract.MovieEntry.CONTENT_URI,
                contentValues.toArray(new ContentValues[contentValues.size()]));
    }


    /**
     * Logical type describes movies fetching state.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MOST_POPULAR, HIGHEST_RATED})
    public @interface QueryType {
    }

    public static final int MOST_POPULAR = 0;
    public static final int HIGHEST_RATED = 1;
}