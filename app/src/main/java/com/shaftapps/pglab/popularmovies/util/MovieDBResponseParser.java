package com.shaftapps.pglab.popularmovies.util;

import android.content.ContentValues;

import com.shaftapps.pglab.popularmovies.asynctask.FetchMoviesTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.shaftapps.pglab.popularmovies.data.MovieContract.*;

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


    public static ArrayList<ContentValues> getMoviesFromJson(
            String movieJsonStr, FetchMoviesTask.QueryType queryType)
            throws JSONException {

        JSONObject jsonObject = new JSONObject(movieJsonStr);
        int pageIndex = jsonObject.getInt("page");
        JSONArray resultsArray = jsonObject.getJSONArray("results");

        ArrayList<ContentValues> movieValues = new ArrayList<>();
        ContentValues movie;

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject movieObject = resultsArray.getJSONObject(i);
            movie = new ContentValues();

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

            // This data are important only if query concerned specific category.
            switch (queryType) {
                case MOST_POPULAR:
                    movie.put(MovieEntry.COLUMN_MOST_POPULAR,
                            movieObject.getDouble("popularity"));
                    break;
                case HIGHEST_RATED:
                    movie.put(MovieEntry.COLUMN_HIGHEST_RATED,
                            (pageIndex - 1) * PAGE_LENGTH + i);
                    break;
            }

            movieValues.add(movie);
        }

        return movieValues;
    }

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
}
