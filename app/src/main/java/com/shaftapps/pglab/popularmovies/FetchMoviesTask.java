package com.shaftapps.pglab.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.shaftapps.pglab.popularmovies.data.MovieContract;
import com.shaftapps.pglab.popularmovies.util.MovieDataParser;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Class for movies fetching AsyncTasks.
 * <p/>
 * Created by Paulina on 2015-09-01.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<ContentValues>> {

    // URL constant parts
    private static final String SCHEME = "https";
    private static final String AUTHORITY = "api.themoviedb.org";
    private static final String API_VERSION = "3";
    private static final String SEARCH_METHOD = "discover";
    private static final String TYPE = "movie";
    private static final String API_KEY = "api_key";
    private static final String SORT_BY = "sort_by";
    private static final String POPULARITY = "popularity.desc";
    private static final String VOTE_AVERAGE = "vote_average.desc";
    private static final String VOTE_MIN = "vote_count.gte";
    private static final String VOTE_MIN_VALUE = "1000";

    // Enter your API key here
    private static final String API_KEY_VALUE = "";

    private Context context;
    private DurationListener durationListener;
    private QueryType queryType;


    public FetchMoviesTask(Context context, QueryType queryType) {
        this.context = context;
        this.queryType = queryType;
    }

    @Override
    protected void onPreExecute() {
        if (durationListener != null)
            durationListener.onTaskStart();
    }

    @Override
    protected ArrayList<ContentValues> doInBackground(String[] params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null;

        try {
            URL url = new URL(getUrl());

            // Create the request and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Make JSON easier to read
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                moviesJsonStr = null;
            }

            moviesJsonStr = buffer.toString();

        } catch (IOException e) {
            Log.e(this.getClass().getName(), "Error ", e);
            return null;

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(this.getClass().getName(), "Error closing stream", e);
                }
            }
        }

        try {
            return MovieDataParser.getMoviesFromJson(moviesJsonStr, queryType);
        } catch (JSONException e) {
            Log.e(this.getClass().getName(), "Error parsing JSON", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<ContentValues> movieValues) {
        if (durationListener != null)
            durationListener.onTaskEnd();

        if (movieValues == null)
            Toast.makeText(context, R.string.error_fetching_movies, Toast.LENGTH_SHORT).show();
        else {
            // TODO:
            // insert new data from API to database or
            // update if they're used by another category and
            // delete cached data
            for (ContentValues movie : movieValues) {
                Uri uri = context.getContentResolver().insert(
                        MovieContract.MovieEntry.CONTENT_URI,
                        movie);
                if (uri == null) {
                    context.getContentResolver().update(
                            MovieContract.MovieEntry.CONTENT_URI,
                            movie,
                            MovieContract.MovieEntry._ID + "=?",
                            new String[]{movie.getAsString(MovieContract.MovieEntry._ID)});
                }
            }
        }
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
                        .build()
                        .toString();
            case HIGHEST_RATED:
                return getUriBuilder()
                        .appendQueryParameter(SORT_BY, VOTE_AVERAGE)
                        .appendQueryParameter(VOTE_MIN, VOTE_MIN_VALUE)
                        .build()
                        .toString();
            default:
                throw new UnsupportedOperationException(
                        "Unsupported query type: " + queryType.name());
        }
    }

    protected Uri.Builder getUriBuilder() {
        return new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_VERSION)
                .appendPath(SEARCH_METHOD)
                .appendPath(TYPE)
                .appendQueryParameter(API_KEY, API_KEY_VALUE);
    }

    public void setDurationListener(DurationListener listener) {
        durationListener = listener;
    }


    public enum QueryType {
        MOST_POPULAR, HIGHEST_RATED
    }


    public interface DurationListener {

        void onTaskStart();

        void onTaskEnd();
    }

}