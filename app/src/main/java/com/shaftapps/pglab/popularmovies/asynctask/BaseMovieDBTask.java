package com.shaftapps.pglab.popularmovies.asynctask;

import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Paulina on 2015-09-24.
 */
public abstract class BaseMovieDBTask extends AsyncTask<String, Void, Boolean> {

    // URL constant parts
    private static final String SCHEME = "https";
    private static final String AUTHORITY = "api.themoviedb.org";
    private static final String API_VERSION = "3";
    private static final String API_KEY = "api_key";

    // Enter your API key here
    private static final String API_KEY_VALUE = "";

    @Override
    protected Boolean doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr = null;

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
                jsonStr = null;
            }

            jsonStr = buffer.toString();

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

        ArrayList<ContentValues> parsedData = getParsedData(jsonStr);
        saveToDatabase(parsedData);

        return parsedData != null;
    }

    protected abstract String getUrl();

    protected Uri.Builder getUriBuilder() {
        return new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_VERSION)
                .appendQueryParameter(API_KEY, API_KEY_VALUE);
    }

    protected abstract ArrayList<ContentValues> getParsedData(String json);

    protected abstract void saveToDatabase(ArrayList<ContentValues> contentValues);

}
