package com.shaftapps.pglab.popularmovies.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.shaftapps.pglab.popularmovies.Keys;
import com.shaftapps.pglab.popularmovies.data.MovieContract;
import com.shaftapps.pglab.popularmovies.fragments.DetailFragment;
import com.shaftapps.pglab.popularmovies.fragments.MoviesFragment;
import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.utils.ColorUtils;
import com.shaftapps.pglab.popularmovies.utils.DisplayMetricsUtils;
import com.shaftapps.pglab.popularmovies.utils.ToolbarUtils;

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
    // Only if two pane
    private Toolbar detailSubToolbar;
    private String detailSubToolbarTitle;


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
        twoPane = DisplayMetricsUtils.getSmallestWidth(getResources().getDisplayMetrics()) > 600;
        if (twoPane) {
            twoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
            detailSubToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
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

            // Reset subtoolbar color
            detailSubToolbar.setBackgroundColor(Color.TRANSPARENT);
            // Load current selected movie's title
            Cursor cursor = getContentResolver().query(
                    uri,
                    new String[]{MovieContract.MovieEntry.COLUMN_TITLE},
                    null,
                    null,
                    null);
            int columnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
            cursor.moveToFirst();
            detailSubToolbarTitle = cursor.getString(columnIndex);
            cursor.close();
            // Reset title on subtoolbar
            ToolbarUtils.showTitleIfOpaque(detailSubToolbar, detailSubToolbarTitle);

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


    //
    //  INTERFACE METHODS:
    //  OnScrollChangedListener (DetailFragment's ScrollView & 'subtoolbar')
    //

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
        // Paint Toolbar background.
        // Alpha of the color depends on DetailFragment's scroll position - start as transparent
        // and ends as opaque.
        float changingDistance = ratioWrapperHeight - detailSubToolbar.getHeight();
        int currentColor = ColorUtils.getColorWithProportionalAlpha(
                color, changingDistance, scrollPosition);
        detailSubToolbar.setBackgroundColor(currentColor);

        ToolbarUtils.showTitleIfOpaque(detailSubToolbar, detailSubToolbarTitle);
    }
}
