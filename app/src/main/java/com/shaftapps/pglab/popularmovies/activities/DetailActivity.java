package com.shaftapps.pglab.popularmovies.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.shaftapps.pglab.popularmovies.Keys;
import com.shaftapps.pglab.popularmovies.fragments.DetailFragment;
import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.data.MovieContract;

/**
 * Activity showing details of specific movie.
 * <p/>
 * Created by Paulina on 2015-08-30.
 */
public class DetailActivity extends DetailFragmentActivity {

    //
    //  ACTIVITY LIFECYCLE METHODS
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Toolbar initialization
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        bindToolbarWithDetailFragment(toolbar);

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
}
