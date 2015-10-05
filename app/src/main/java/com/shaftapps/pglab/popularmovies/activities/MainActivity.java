package com.shaftapps.pglab.popularmovies.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.shaftapps.pglab.popularmovies.Keys;
import com.shaftapps.pglab.popularmovies.fragments.DetailFragment;
import com.shaftapps.pglab.popularmovies.fragments.MoviesFragment;
import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.utils.DisplayUtils;

/**
 * Application's main activity and entry point.
 * <p/>
 * Created by Paulina on 2015-08-30.
 */
public class MainActivity extends DetailFragmentActivity implements MoviesFragment.OnMovieSelectListener,
        AdapterView.OnItemSelectedListener {

    private static final String SORTING_MODE_KEY = "sorting_mode_key";
    private static final String DETAIL_FRAGMENT_TAG = "detail_fragment_tag";

    private Spinner sortModeSpinner;
    private Toolbar toolbar;
    private MoviesFragment moviesFragment;
    private MoviesFragment.SortingMode sortingMode;
    private boolean twoPane;


    //
    //  ACTIVITY LIFECYCLE METHODS
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting custom toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // Initialization of spinner with sorting modes
        initSpinner();

        // Initialization of MainActivity's static fragment
        moviesFragment = (MoviesFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_movies);

        // Checking MainActivity is two pane (ie. sw600dp) layout or not
        twoPane = DisplayUtils.isSmallestWidth600dp(this);
        if (twoPane) {
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
            Toolbar detailSubToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
            bindToolbarWithDetailFragment(detailSubToolbar);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Setting default (first) option of sorting movies if there is no saved data
        // or loading saved one.
        if (savedInstanceState == null) {
            sortingMode = MoviesFragment.SortingMode.MOST_POPULAR;
            notifySortingModeSet(false);
        } else {
            sortingMode = (MoviesFragment.SortingMode) savedInstanceState.getSerializable(SORTING_MODE_KEY);
            notifySortingModeSet(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SORTING_MODE_KEY, sortingMode);
        super.onSaveInstanceState(outState);
    }


    //
    // OPTION MENU METHODS
    //

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (twoPane) {
            Toolbar detailSubToolbar = getDetailFragmentToolbar();
            detailSubToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return getSupportFragmentManager().findFragmentById(R.id.movie_detail_container).onOptionsItemSelected(item);
                }
            });
            detailSubToolbar.getMenu().clear();
            return super.onCreatePanelMenu(featureId, detailSubToolbar.getMenu());
        } else {
            return super.onCreatePanelMenu(featureId, menu);
        }
    }

    //
    //  INITIALIZATION HELPER METHODS
    //

    private void initSpinner() {
        sortModeSpinner = (Spinner) toolbar.findViewById(R.id.sort_mode_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sorting_modes, R.layout.sort_mode_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sortModeSpinner.setAdapter(adapter);
        sortModeSpinner.setOnItemSelectedListener(this);
    }


    //
    //  OTHER HELPER METHODS
    //

    /**
     * Method called when a selected sorting mode should be applied.
     */
    private void notifySortingModeSet(boolean scrollTop) {
        moviesFragment.loadRequiredMovies(sortingMode, scrollTop);
    }


    //
    //  INTERFACE METHODS:
    //  OnMovieSelectListener
    //

    @Override
    public void onMovieSelect(Uri uri) {
        if (twoPane) {
            // Putting uri to arguments
            Bundle args = new Bundle();
            args.putParcelable(Keys.SELECTED_MOVIE_URI, uri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAIL_FRAGMENT_TAG)
                    .commit();

        } else {
            // Opening new activity with uri of selected movie.
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(uri);
            startActivity(intent);
        }
    }


    //
    //  INTERFACE METHODS:
    //  OnItemSelectedListener (Spinner)
    //

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (sortModeSpinner == parent) {
            if (position != sortingMode.ordinal()) {
                sortingMode = MoviesFragment.SortingMode.values()[position];
                notifySortingModeSet(true);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

}
