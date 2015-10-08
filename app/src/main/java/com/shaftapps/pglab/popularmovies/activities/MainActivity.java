package com.shaftapps.pglab.popularmovies.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shaftapps.pglab.popularmovies.Keys;
import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.adapters.MovieCategoryPagerAdapter;
import com.shaftapps.pglab.popularmovies.fragments.BaseMoviesCategoryFragment;
import com.shaftapps.pglab.popularmovies.fragments.DetailFragment;
import com.shaftapps.pglab.popularmovies.utils.DisplayUtils;

/**
 * Application's main activity and entry point.
 * <p/>
 * Created by Paulina on 2015-08-30.
 */
public class MainActivity extends DetailFragmentActivity implements BaseMoviesCategoryFragment.OnMovieSelectListener {

    private static final String DETAIL_FRAGMENT_TAG = "detail_fragment_tag";

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

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
        setSupportActionBar(toolbar);

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

        viewPager = (ViewPager) findViewById(R.id.main_view_pager);
        viewPager.setAdapter(new MovieCategoryPagerAdapter(this, getSupportFragmentManager()));

        tabLayout = (TabLayout) findViewById(R.id.main_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
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


    //
    //  INTERFACE METHODS:
    //  OnMovieSelectListener
    //

    @Override
    public void onMovieSelect(Uri uri) {
        if (twoPane) {
            // If already selected movie is the same as now clicked don't create another Fragment -
            // - just tell Fragment it should react.
            DetailFragment detailFragment = (DetailFragment)
                    getSupportFragmentManager().findFragmentById(R.id.movie_detail_container);
            if (detailFragment != null && detailFragment.getMovieUri() != null
                    && detailFragment.getMovieUri().equals(uri)) {
                detailFragment.reloadMovie();
                return;
            }

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

}
