package com.shaftapps.pglab.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Base class for movies fetching AsyncTasks.
 * <p/>
 * Created by Paulina on 2015-09-01.
 */
public abstract class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<MovieData>> {

    private static final String SCHEME = "https";
    private static final String AUTHORITY = "api.themoviedb.org";
    private static final String API_VERSION = "3";
    private static final String SEARCH_METHOD = "discover";
    private static final String TYPE = "movie";
    private static final String API_KEY = "api_key";

    // Enter your API key here
    private static final String API_KEY_VALUE = "";

    @Override
    protected ArrayList<MovieData> doInBackground(String[] params) {
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
                moviesJsonStr = null;
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
            return MovieDataParser.getMovieDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(this.getClass().getName(), "Error parsing JSON", e);
            return null;
        }
    }

    /**
     * Method returning url with query to the API.
     *
     * @return specific url query
     */
    protected abstract String getUrl();

    protected Uri.Builder getUriBuilder() {
        return new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_VERSION)
                .appendPath(SEARCH_METHOD)
                .appendPath(TYPE)
                .appendQueryParameter(API_KEY, API_KEY_VALUE);
    }

}