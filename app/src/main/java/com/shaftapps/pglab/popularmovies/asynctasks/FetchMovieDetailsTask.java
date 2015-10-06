package com.shaftapps.pglab.popularmovies.asynctasks;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.shaftapps.pglab.popularmovies.data.MovieContract;
import com.shaftapps.pglab.popularmovies.utils.MovieDBResponseParser;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Class for specific movie fetching AsyncTasks.
 * http://api.themoviedb.org/3/movie/{id}
 * <p/>
 * Created by Paulina on 2015-10-05.
 */
public class FetchMovieDetailsTask extends BaseMovieDBTask {

    private static final String MOVIE = "movie";

    private Context context;
    private long movieId;

    public FetchMovieDetailsTask(Context context, long movieId) {
        this.context = context;
        this.movieId = movieId;
    }

    @Override
    protected String getUrl() {
        return getUriBuilder()
                .appendPath(MOVIE)
                .appendPath(Long.toString(movieId))
                .build()
                .toString();
    }

    @Override
    protected ArrayList<ContentValues> getParsedData(String json) {
        try {
            ArrayList<ContentValues> movieList = new ArrayList<>();
            movieList.add(MovieDBResponseParser.getSingleMovieFromJson(json));
            return movieList;
        } catch (JSONException e) {
            Log.e(this.getClass().getName(), "Error parsing JSON", e);
            return null;
        }
    }

    @Override
    protected void saveToDatabase(ArrayList<ContentValues> contentValues) {
        // Update movie from database.
        // If movie isn't present, do nothing.
        context.getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI,
                contentValues.get(0),
                MovieContract.MovieEntry._ID + "=?",
                new String[]{contentValues.get(0).getAsString(MovieContract.MovieEntry._ID)});
    }
}
