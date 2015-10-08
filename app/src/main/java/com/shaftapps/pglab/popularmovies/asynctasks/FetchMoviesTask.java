package com.shaftapps.pglab.popularmovies.asynctasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.data.MovieContract;
import com.shaftapps.pglab.popularmovies.utils.MovieDBResponseParser;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Class for movies fetching AsyncTasks.
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
    private DurationListener durationListener;
    private QueryType queryType;


    public FetchMoviesTask(Context context, QueryType queryType) {
        this.context = context;
        this.queryType = queryType;
    }


    //
    //  ASYNC TASK METHODS
    //

    @Override
    protected void onPreExecute() {
        if (durationListener != null)
            durationListener.onTaskStart();
    }

    @Override
    protected Boolean doInBackground(String[] params) {
        clearCachedMovies();

        for (int page = 1; page <= PAGE_COUNT; page++)
            if (!super.doInBackground(getUrlWithPage(page)))
                return false;

        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (isCancelled())
            return;

        if (success == null || !success) {
            Toast.makeText(context, R.string.error_fetching_movies, Toast.LENGTH_SHORT).show();
            if (durationListener != null)
                durationListener.onTaskFailed(queryType);
        } else {
            if (durationListener != null)
                durationListener.onTaskEnd(queryType);
        }
    }


    //
    //  HELPER METHODS
    //

    private void clearCachedMovies() {
        switch (queryType) {
            case MOST_POPULAR: {
                // Delete all movies, which are in database only because they're most popular.
                context.getContentResolver().delete(
                        MovieContract.MovieEntry.CONTENT_URI,
                        MovieContract.MovieEntry.COLUMN_HIGHEST_RATED + "=? AND " +
                                MovieContract.MovieEntry.COLUMN_FAVORITE + "=?",
                        new String[]{"NULL", "NULL"});
                // Overwrite (reset) popularity in remaining movies.
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieContract.MovieEntry.COLUMN_MOST_POPULAR, "NULL");
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
                        MovieContract.MovieEntry.COLUMN_MOST_POPULAR + "=? AND " +
                                MovieContract.MovieEntry.COLUMN_FAVORITE + "=?",
                        new String[]{"NULL", "NULL"});
                // Overwrite (reset) highest rated order value in remaining movies.
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieContract.MovieEntry.COLUMN_HIGHEST_RATED, "NULL");
                context.getContentResolver().update(
                        MovieContract.MovieEntry.CONTENT_URI,
                        contentValues,
                        null,
                        null);
                break;
            }
            default:
                throw new UnsupportedOperationException(
                        "Unsupported query type: " + queryType.name());
        }
    }

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
                        "Unsupported query type: " + queryType.name());
        }
    }

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

    public void setDurationListener(DurationListener listener) {
        durationListener = listener;
    }


    public enum QueryType {
        MOST_POPULAR, HIGHEST_RATED
    }


    public interface DurationListener {

        void onTaskStart();

        void onTaskEnd(QueryType queryType);

        void onTaskFailed(QueryType queryType);
    }

}