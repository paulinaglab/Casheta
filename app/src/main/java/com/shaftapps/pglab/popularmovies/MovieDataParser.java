package com.shaftapps.pglab.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Utility class parsing JSON response to list of MovieData.
 *
 * Created by Paulina on 2015-08-29.
 */
public class MovieDataParser {

    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_WIDTH = "w342"; //One of the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original".
    private static final String PHOTO_WIDTH = "w780";

    public static ArrayList<MovieData> getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        JSONObject jsonObject = new JSONObject(movieJsonStr);
        JSONArray resultsArray = jsonObject.getJSONArray("results");

        ArrayList<MovieData> movieDatas = new ArrayList<>();
        MovieData movie;

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject movieObject = resultsArray.getJSONObject(i);
            movie = new MovieData();
            movie.title = movieObject.getString("title");
            movie.originalTitle = movieObject.getString("original_title");
            movie.posterUrl = POSTER_BASE_URL + POSTER_WIDTH + movieObject.getString("poster_path");
            movie.photoUrl = POSTER_BASE_URL + PHOTO_WIDTH + movieObject.getString("backdrop_path");
            movie.averageRate = movieObject.getDouble("vote_average");
            movie.overview = movieObject.getString("overview");
            movie.releaseDate = movieObject.getString("release_date");
            movieDatas.add(movie);
        }

        return movieDatas;
    }
}
