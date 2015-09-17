package com.shaftapps.pglab.popularmovies.activity;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.shaftapps.pglab.popularmovies.Keys;
import com.shaftapps.pglab.popularmovies.fragment.DetailFragment;
import com.shaftapps.pglab.popularmovies.MovieData;
import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.data.MovieContract;

/**
 * Activity showing details of specific movie.
 * <p/>
 * Created by Paulina on 2015-08-30.
 */
public class DetailActivity extends AppCompatActivity implements DetailFragment.OnScrollChangedListener {

    private Toolbar toolbar;
    private String title;
    private MovieData movieData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Toolbar initialization
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Getting data from Intent
        movieData = getIntent().getParcelableExtra(Keys.SELECTED_MOVIE_DATA_EXTRA);
        title = movieData.title;

        // Loading saved state
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(Keys.SELECTED_MOVIE_DATA_EXTRA, movieData);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onScrollChanged(int ratioWrapperHeight, int color, int scrollPosition) {
        // Paint Toolbar background.
        // Alpha of the color depends on DetailFragment's scroll position - start as transparent
        // and ends as opaque.
        float changingDistance = ratioWrapperHeight - toolbar.getHeight();
        float progress = Math.min(Math.max(scrollPosition, 0), changingDistance)
                / changingDistance;
        int currentColor = Color.argb((int) (255 * progress),
                Color.red(color),
                Color.green(color),
                Color.blue(color));
        toolbar.setBackgroundColor(currentColor);

        // Show title on toolbar when background is opaque.
        if (progress == 1)
            toolbar.setTitle(title);
        else toolbar.setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        //TODO: is it favorite? - set proper icon
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            item.setChecked(!item.isChecked());
            if (item.isChecked()) {
                item.setIcon(R.drawable.ic_favorite_on);
            } else {
                item.setIcon(R.drawable.ic_favorite_off);
            }
            Log.i(getClass().getSimpleName(), "Fav icon clicked! Now is set to " + item.isChecked());
            markAsFavorite(item.isChecked());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void markAsFavorite(boolean favorite) {
        if (favorite) {
            // Add movie to favorites
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_API_ID, movieData.apiId);
            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movieData.title);
            contentValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movieData.originalTitle);
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_URL, movieData.posterUrl);
            contentValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_URL, movieData.photoUrl);
            contentValues.put(MovieContract.MovieEntry.COLUMN_AVERAGE_RATE, movieData.averageRate);
            contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movieData.overview);
            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieData.releaseDate);
            contentValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, true);
            getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
        } else {
            // Remove movie from favorites
            getContentResolver().delete(
                    MovieContract.MovieEntry.CONTENT_URI,
                    MovieContract.MovieEntry.COLUMN_MOVIE_API_ID + "=?",
                    new String[]{Long.toString(movieData.apiId)});
        }
    }
}
