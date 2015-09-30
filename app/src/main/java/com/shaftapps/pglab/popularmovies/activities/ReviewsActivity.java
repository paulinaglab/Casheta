package com.shaftapps.pglab.popularmovies.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.shaftapps.pglab.popularmovies.Keys;
import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.data.MovieContract;
import com.shaftapps.pglab.popularmovies.fragments.ReviewsDialogFragment;

/**
 * Created by Paulina on 2015-09-30.
 */
public class ReviewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        // Toolbar initialization
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Cursor cursor = getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COLUMN_TITLE},
                MovieContract.MovieEntry._ID + "=?",
                new String[]{Long.toString(MovieContract.ReviewEntry.getMovieIdFromUri(getIntent().getData()))},
                null);
        if (cursor.moveToFirst())
            toolbar.setTitle(
                    cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
        cursor.close();
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Loading saved state
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(Keys.REVIEWS_OF_MOVIE_URI, getIntent().getData());

            ReviewsDialogFragment fragment = new ReviewsDialogFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.reviews_container, fragment)
                    .commit();
        }
    }
}
