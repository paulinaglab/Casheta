package com.shaftapps.pglab.popularmovies.asynctasks;

import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Base class for tasks fetching data from themoviedb.org.
 * <p/>
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

    private int id;
    private DurationListener durationListener;

    /**
     * Constructor.
     *
     * @param id Unique id for this task.
     */
    public BaseMovieDBTask(int id) {
        this.id = id;
    }

    @Override
    protected void onPreExecute() {
        if (durationListener != null)
            durationListener.onTaskStart(this);
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (isCancelled())
            return;

        if (success == null || !success) {
            // Toast.makeText(context, R.string.error_fetching_movies, Toast.LENGTH_SHORT).show();
            if (durationListener != null) {
                durationListener.onTaskFailed(this);
            }
        } else {
            if (durationListener != null) {
                durationListener.onTaskEnd(this);
            }
        }
    }

    /**
     * @param params params[0] is URL string. If not provided, getUrl() method will be used.
     * @return return true if downloaded successfully, false otherwise.
     */
    @Override
    protected Boolean doInBackground(String... params) {
        // Fetching data
        String jsonStr = fetchData(params);
        if (jsonStr == null)
            return false;

        // Clearing cache
        clearCache();

        // Parsing
        ArrayList<ContentValues> parsedData = getParsedData(jsonStr);

        // Saving to database
        saveToDatabase(parsedData);

        return parsedData != null;
    }

    protected String fetchData(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr = null;

        try {
            String urlString = params.length > 0 ? params[0] : getUrl();
            URL url = new URL(urlString);

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

        } catch (Exception e) {
            Log.e(this.getClass().getName(), "Error ", e);
            return null;

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    Log.e(this.getClass().getName(), "Error closing stream", e);
                }
            }
        }
        return jsonStr;
    }

    /**
     * Method returning complete query url.
     *
     * @return complete task query url.
     */
    protected abstract String getUrl();

    /**
     * Return Uri.Builder with basic url.
     *
     * @return Uri.Builder with url: https://api.themoviedb.org/3/...?api_key=XXX
     */
    protected Uri.Builder getUriBuilder() {
        return new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_VERSION)
                .appendQueryParameter(API_KEY, API_KEY_VALUE);
    }

    /**
     * Method clearing cache.
     */
    protected abstract void clearCache();

    /**
     * Method calling JSON response parsing.
     *
     * @param json response from themoviedb.org.
     * @return array of parsed to ContentValues data.
     */
    protected abstract ArrayList<ContentValues> getParsedData(String json);

    /**
     * Method saving data to database.
     *
     * @param contentValues parsed data got from themoviedb.org.
     */
    protected abstract void saveToDatabase(ArrayList<ContentValues> contentValues);

    public int getId() {
        return id;
    }

    public void setDurationListener(DurationListener durationListener) {
        this.durationListener = durationListener;
    }

    /**
     * Listener interface providing callbacks for important task state changes.
     */
    public interface DurationListener {

        /**
         * Invoked before task starts.
         *
         * @param task task which will be started.
         */
        void onTaskStart(BaseMovieDBTask task);

        /**
         * Invoked when task ends successfully.
         *
         * @param task task which has ended.
         */
        void onTaskEnd(BaseMovieDBTask task);

        /**
         * Invoked when task ends with failure.
         *
         * @param task task which has failed.
         */
        void onTaskFailed(BaseMovieDBTask task);
    }

}