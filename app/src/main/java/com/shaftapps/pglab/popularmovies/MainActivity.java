package com.shaftapps.pglab.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Application's main activity and entry point.
 * <p/>
 * Created by Paulina on 2015-08-30.
 */
public class MainActivity extends AppCompatActivity implements MoviesFragment.OnMovieSelectListener,
        AdapterView.OnItemSelectedListener {

    private static final String SORTING_MODE_KEY = "sorting_mode_key";

    private Spinner sortModeSpinner;
    private Toolbar toolbar;
    private MoviesFragment moviesFragment;
    private MoviesFragment.SortingMode sortingMode;


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
        if (savedInstanceState == null) {
            sortingMode = MoviesFragment.SortingMode.MOST_POPULAR;
            Log.d(getClass().getSimpleName() + "sortMode: empty", sortingMode.name());
            notifySortingModeSet(false);
        } else {
            sortingMode = (MoviesFragment.SortingMode) savedInstanceState.getSerializable(SORTING_MODE_KEY);
            Log.d(getClass().getSimpleName() + "sortMode: saved", sortingMode.name());
            notifySortingModeSet(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SORTING_MODE_KEY, sortingMode);
        Log.d(getClass().getSimpleName() + " sortMode: saving state", sortingMode.name());
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
     * Method called when a sorting mode should be applied.
     */
    private void notifySortingModeSet(boolean scrollTop) {
        moviesFragment.loadRequiredMovies(sortingMode, scrollTop);
    }


    @Override
    public void onMovieSelect(MovieData selected) {
        // Opening new activity with details of selected movie.
        Intent intent = new Intent(this, DetailActivity.class).putExtra(MovieData.EXTRA_KEY, selected);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (sortModeSpinner == parent) {
            if (position != sortingMode.ordinal()) {
                Log.d(getClass().getSimpleName(), "Position: " + position + " on item selected");
                sortingMode = MoviesFragment.SortingMode.values()[position];
                notifySortingModeSet(true);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
