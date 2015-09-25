package com.shaftapps.pglab.popularmovies.asynctask;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.shaftapps.pglab.popularmovies.data.MovieContract;
import com.shaftapps.pglab.popularmovies.util.MovieDBResponseParser;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Paulina on 2015-09-24.
 */
public class FetchReviewsTask extends BaseMovieDBTask {

    private static final String MOVIE = "movie";
    private static final String REVIEW = "reviews";

    private Context context;
    private long movieId;
    private DurationListener durationListener;

    public FetchReviewsTask(Context context, long movieId) {
        this.context = context;
        this.movieId = movieId;
    }

    @Override
    protected String getUrl() {
        return getUriBuilder()
                .appendPath(MOVIE)
                .appendPath(Long.toString(movieId))
                .appendPath(REVIEW)
                .build()
                .toString();
    }

    @Override
    protected ArrayList<ContentValues> getParsedData(String json) {
        try {
            return MovieDBResponseParser.getReviewsFromJson(json);
        } catch (JSONException e) {
            Log.e(this.getClass().getName(), "Error parsing JSON", e);
            return null;
        }
    }

    @Override
    protected void saveToDatabase(ArrayList<ContentValues> contentValues) {
        Uri reviewUri = MovieContract.ReviewEntry.buildUriByMovieId(movieId);
        for (ContentValues review : contentValues) {
            Uri insertUri = context.getContentResolver().insert(
                    reviewUri,
                    review);
            if (insertUri == null) {
                context.getContentResolver().update(
                        reviewUri,
                        review,
                        MovieContract.ReviewEntry.COLUMN_REVIEW_API_ID + "=?",
                        new String[]{review.getAsString(MovieContract.ReviewEntry.COLUMN_REVIEW_API_ID)});
            }
        }
    }

    public void setDurationListener(DurationListener listener) {
        durationListener = listener;
    }

    public interface DurationListener {

        void onTaskStart();

        void onTaskEnd();
    }
}
