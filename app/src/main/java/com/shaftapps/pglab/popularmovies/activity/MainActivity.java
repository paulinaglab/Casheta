package com.shaftapps.pglab.popularmovies.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.shaftapps.pglab.popularmovies.fragment.DetailFragment;
import com.shaftapps.pglab.popularmovies.fragment.MoviesFragment;
import com.shaftapps.pglab.popularmovies.R;

/**
 * Application's main activity and entry point.
 * <p/>
 * Created by Paulina on 2015-08-30.
 */
public class MainActivity extends AppCompatActivity implements MoviesFragment.OnMovieSelectListener,
        AdapterView.OnItemSelectedListener, DetailFragment.OnScrollChangedListener {

    private static final String SORTING_MODE_KEY = "sorting_mode_key";
    private static final String DETAIL_FRAGMENT_TAG = "detail_fragment_tag";

    private Spinner sortModeSpinner;
    private Toolbar toolbar;
    private MoviesFragment moviesFragment;
    private MoviesFragment.SortingMode sortingMode;
    private boolean twoPane;


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

        // Setting default (first) option of sorting movies if there is no saved data
        // or loading saved one.
        if (savedInstanceState == null) {
            sortingMode = MoviesFragment.SortingMode.MOST_POPULAR;
            notifySortingModeSet(false);
        } else {
            sortingMode = (MoviesFragment.SortingMode) savedInstanceState.getSerializable(SORTING_MODE_KEY);
            notifySortingModeSet(false);
        }

        // Checking MainActivity is two pain layout or not
        if (findViewById(R.id.movie_detail_container) != null) {
            twoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else
            twoPane = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SORTING_MODE_KEY, sortingMode);
        super.onSaveInstanceState(outState);
    }

    private void initSpinner() {
        sortModeSpinner = (Spinner) toolbar.findViewById(R.id.sort_mode_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sorting_modes, R.layout.sort_mode_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sortModeSpinner.setAdapter(adapter);
        sortModeSpinner.setOnItemSelectedListener(this);
    }

    /**
     * Method called when a selected sorting mode should be applied.
     */
    private void notifySortingModeSet(boolean scrollTop) {
        moviesFragment.loadRequiredMovies(sortingMode, scrollTop);
    }

    @Override
    public void onMovieSelect(Uri uri) {
        if (!twoPane) {
            // Opening new activity with uri of selected movie.
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(uri);
            startActivity(intent);
        }
    }

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

    /**
     * NOTE: This method is called only if MainActivity's layout is two pane.
     * Called when scroll position in DetailFragment is changed.
     *
     * @param ratioWrapperHeight top part layout (ratio wrapper) height
     * @param color              color generated based on poster image
     * @param scrollPosition     current scroll position
     */
    @Override
    public void onScrollChanged(int ratioWrapperHeight, int color, int scrollPosition) {

    }
}
