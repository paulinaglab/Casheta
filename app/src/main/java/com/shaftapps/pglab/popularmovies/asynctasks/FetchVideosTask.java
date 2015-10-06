package com.shaftapps.pglab.popularmovies.asynctasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.shaftapps.pglab.popularmovies.data.MovieContract;
import com.shaftapps.pglab.popularmovies.utils.MovieDBResponseParser;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Class for videos fetching AsyncTasks.
 * Videos of movie url: http://api.themoviedb.org/3/movie/{id}/videos
 *
 * Created by Paulina on 2015-09-25.
 */
public class FetchVideosTask extends BaseMovieDBTask {

    private static final String MOVIE = "movie";
    private static final String VIDEO = "videos";

    private Context context;
    private long movieId;

    public FetchVideosTask(Context context, long movieId) {
        this.context = context;
        this.movieId = movieId;
    }

    @Override
    protected String getUrl() {
        return getUriBuilder()
                .appendPath(MOVIE)
                .appendPath(Long.toString(movieId))
                .appendPath(VIDEO)
                .build()
                .toString();
    }

    @Override
    protected ArrayList<ContentValues> getParsedData(String json) {
        try {
            return MovieDBResponseParser.getVideosFromJson(json);
        } catch (JSONException e) {
            Log.e(this.getClass().getName(), "Error parsing JSON", e);
            return null;
        }
    }

    @Override
    protected void saveToDatabase(ArrayList<ContentValues> contentValues) {
        Uri videoUri = MovieContract.VideoEntry.buildUriByMovieId(movieId);
        for (ContentValues video : contentValues) {
            Uri insertUri = context.getContentResolver().insert(
                    videoUri,
                    video);
            if (insertUri == null) {
                context.getContentResolver().update(
                        videoUri,
                        video,
                        MovieContract.VideoEntry.COLUMN_VIDEO_API_ID + "=?",
                        new String[]{video.getAsString(MovieContract.VideoEntry.COLUMN_VIDEO_API_ID)});
            }
        }
    }

}
