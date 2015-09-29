package com.shaftapps.pglab.popularmovies.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.shaftapps.pglab.popularmovies.Keys;
import com.shaftapps.pglab.popularmovies.fragments.DetailFragment;
import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.data.MovieContract;
import com.shaftapps.pglab.popularmovies.utils.ColorUtils;
import com.shaftapps.pglab.popularmovies.utils.ToolbarUtils;

/**
 * Activity showing details of specific movie.
 * <p/>
 * Created by Paulina on 2015-08-30.
 */
public class DetailActivity extends AppCompatActivity implements DetailFragment.OnScrollChangedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIE_LOADER_ID = 1;

    private Toolbar toolbar;
    private String title;


    //
    //  ACTIVITY LIFECYCLE METHODS
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Toolbar initialization
        toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Getting movie title
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);

        // Loading saved state
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(Keys.SELECTED_MOVIE_URI, getIntent().getData());

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
        int currentColor = ColorUtils.getColorWithProportionalAlpha(
                color, changingDistance, scrollPosition);
        toolbar.setBackgroundColor(currentColor);

        ToolbarUtils.showTitleIfOpaque(toolbar, title);
    }


    //
    //  LOADER CALLBACKS
    //

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                getIntent().getData(),
                new String[]{MovieContract.MovieEntry.COLUMN_TITLE},
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        title = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
        // Updating title (if it is visible)
        ToolbarUtils.showTitleIfOpaque(toolbar, title);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
