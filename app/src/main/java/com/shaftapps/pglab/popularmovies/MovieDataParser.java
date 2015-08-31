package com.shaftapps.pglab.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Paulina on 2015-08-29.
 */
public class MovieDataParser {

    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_WIDTH = "w342"; //One of the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original".
    private static final String PHOTO_WIDTH = "w780";

    public static MovieData[] getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        JSONObject jsonObject = new JSONObject(movieJsonStr);
        JSONArray resultsArray = jsonObject.getJSONArray("results");

        MovieData[] movieDatas = new MovieData[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject movieObject = resultsArray.getJSONObject(i);
            movieDatas[i] = new MovieData();
            movieDatas[i].title = movieObject.getString("title");
            movieDatas[i].originalTitle = movieObject.getString("original_title");
            movieDatas[i].posterUrl = POSTER_BASE_URL + POSTER_WIDTH + movieObject.getString("poster_path");
            movieDatas[i].photoUrl = POSTER_BASE_URL + PHOTO_WIDTH + movieObject.getString("backdrop_path");
            movieDatas[i].averageRate = movieObject.getDouble("vote_average");
            movieDatas[i].overview = movieObject.getString("overview");
            movieDatas[i].releaseDate = movieObject.getString("release_date");
        }

        return movieDatas;
    }
}
