package com.shaftapps.pglab.popularmovies.utils;

import android.content.ContentValues;

import com.shaftapps.pglab.popularmovies.asynctasks.FetchMoviesTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.shaftapps.pglab.popularmovies.data.MovieContract.MovieEntry;
import static com.shaftapps.pglab.popularmovies.data.MovieContract.ReviewEntry;
import static com.shaftapps.pglab.popularmovies.data.MovieContract.VideoEntry;

/**
 * Utility class parsing JSON response to list of MovieData.
 * <p/>
 * Created by Paulina on 2015-08-29.
 */
public class MovieDBResponseParser {

    private static final int PAGE_LENGTH = 20;

    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_WIDTH = "w342"; //One of the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original".
    private static final String BACKDROP_WIDTH = "w780";


    /**
     * Parses JSON containing movies to array of ContentValues.
     *
     * @param moviesJsonStr themoviedb.org response.
     * @param queryType     type of query (sorting mode).
     * @return array list ready to be saved to database.
     * @throws JSONException
     */
    public static ArrayList<ContentValues> getMoviesFromJson(
            String moviesJsonStr, int queryType)
            throws JSONException {

        JSONObject jsonObject = new JSONObject(moviesJsonStr);
        int pageIndex = jsonObject.getInt("page");
        JSONArray resultsArray = jsonObject.getJSONArray("results");

        ArrayList<ContentValues> movieValues = new ArrayList<>();
        ContentValues movie;

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject movieObject = resultsArray.getJSONObject(i);

            movie = getSingleMovieFromJson(movieObject);

            // This data are important only if query concerned specific category.
            switch (queryType) {
                case FetchMoviesTask.MOST_POPULAR:
                    movie.put(MovieEntry.COLUMN_MOST_POPULAR,
                            movieObject.getDouble("popularity"));
                    break;
                case FetchMoviesTask.HIGHEST_RATED:
                    movie.put(MovieEntry.COLUMN_HIGHEST_RATED,
                            (pageIndex - 1) * PAGE_LENGTH + i);
                    break;
            }

            movieValues.add(movie);
        }

        return movieValues;
    }

    /**
     * Parses JSON with single movie.
     *
     * @param moviesJsonStr themoviedb.org response.
     * @return single object of ContentValue ready to save to database.
     * @throws JSONException
     */
    public static ContentValues getSingleMovieFromJson(String moviesJsonStr)
            throws JSONException {
        return getSingleMovieFromJson(new JSONObject(moviesJsonStr));
    }

    /**
     * Parses JSON with single movie.
     *
     * @param movieObject JSONObject from themoviedb.org response.
     * @return single object of ContentValue ready to save to database.
     * @throws JSONException
     */
    public static ContentValues getSingleMovieFromJson(JSONObject movieObject)
            throws JSONException {

        ContentValues movie = new ContentValues();

        movie.put(MovieEntry._ID,
                movieObject.getLong("id"));
        movie.put(MovieEntry.COLUMN_TITLE,
                movieObject.getString("title"));
        movie.put(MovieEntry.COLUMN_ORIGINAL_TITLE,
                movieObject.getString("original_title"));
        movie.put(MovieEntry.COLUMN_POSTER_URL,
                POSTER_BASE_URL + POSTER_WIDTH + movieObject.getString("poster_path"));
        movie.put(MovieEntry.COLUMN_BACKDROP_URL,
                POSTER_BASE_URL + BACKDROP_WIDTH + movieObject.getString("backdrop_path"));
        movie.put(MovieEntry.COLUMN_AVERAGE_RATE,
                movieObject.getDouble("vote_average"));
        movie.put(MovieEntry.COLUMN_OVERVIEW,
                movieObject.getString("overview"));
        movie.put(MovieEntry.COLUMN_RELEASE_DATE,
                movieObject.getString("release_date"));

        JSONArray genresArray = movieObject.optJSONArray("genres");
        if (genresArray != null)
            movie.put(MovieEntry.COLUMN_GENRE,
                    getListedStringObjects(genresArray, "name"));

        JSONArray countriesArray = movieObject.optJSONArray("production_countries");
        if (countriesArray != null)
            movie.put(MovieEntry.COLUMN_COUNTRY,
                    getListedStringObjects(countriesArray, "name"));

        return movie;
    }

    /**
     * Turns JSONArray of objects containing pairs key-value into String of comma separated values.
     *
     * @param jsonArray array of JSONObjects.
     * @param keyName   name of the key.
     * @return converted String of comma separated values.
     * @throws JSONException
     */
    private static String getListedStringObjects(JSONArray jsonArray, String keyName) throws JSONException {
        StringBuilder listBuilder = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject movieObject = jsonArray.getJSONObject(i);

            String singleItem = movieObject.optString(keyName);
            if (singleItem != null) {
                listBuilder.append(singleItem);

                if (i < jsonArray.length() - 1)
                    listBuilder.append(", ");
            }
        }

        return listBuilder.toString();
    }

    /**
     * Converts themoviedb.org response into list of ContentValues.
     *
     * @param reviewJsonStr themoviedb.org response
     * @return list of ContentValues
     * @throws JSONException
     */
    public static ArrayList<ContentValues> getReviewsFromJson(String reviewJsonStr)
            throws JSONException {

        JSONObject jsonObject = new JSONObject(reviewJsonStr);
        long movieId = jsonObject.getLong("id");
        JSONArray resultsArray = jsonObject.getJSONArray("results");

        ArrayList<ContentValues> reviewValues = new ArrayList<>();
        ContentValues review;

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject reviewObject = resultsArray.getJSONObject(i);
            review = new ContentValues();

            review.put(ReviewEntry.COLUMN_MOVIE_ID, movieId);
            review.put(ReviewEntry.COLUMN_REVIEW_API_ID, reviewObject.getString("id"));
            review.put(ReviewEntry.COLUMN_AUTHOR, reviewObject.getString("author"));
            review.put(ReviewEntry.COLUMN_CONTENT, reviewObject.getString("content"));

            reviewValues.add(review);
        }

        return reviewValues;
    }

    /**
     * Converts themoviedb.org response into list of ContentValues.
     *
     * @param videoJsonStr themoviedb.org response
     * @return list of ContentValues
     * @throws JSONException
     */
    public static ArrayList<ContentValues> getVideosFromJson(String videoJsonStr)
            throws JSONException {

        JSONObject jsonObject = new JSONObject(videoJsonStr);
        long movieId = jsonObject.getLong("id");
        JSONArray resultsArray = jsonObject.getJSONArray("results");

        ArrayList<ContentValues> reviewValues = new ArrayList<>();
        ContentValues review;

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject videoObject = resultsArray.getJSONObject(i);
            review = new ContentValues();

            review.put(VideoEntry.COLUMN_MOVIE_ID, movieId);
            review.put(VideoEntry.COLUMN_VIDEO_API_ID, videoObject.getString("id"));
            review.put(VideoEntry.COLUMN_NAME, videoObject.getString("name"));
            review.put(VideoEntry.COLUMN_SITE, videoObject.getString("site"));
            review.put(VideoEntry.COLUMN_KEY, videoObject.getString("key"));
            review.put(VideoEntry.COLUMN_TYPE, videoObject.getString("type"));

            reviewValues.add(review);
        }

        return reviewValues;
    }
}
