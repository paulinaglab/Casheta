package com.shaftapps.pglab.popularmovies.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;

import com.shaftapps.pglab.popularmovies.Keys;
import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.fragments.DetailFragment;
import com.shaftapps.pglab.popularmovies.utils.ColorUtils;

/**
 * Activity showing details of specific movie. Concerns only on phones.
 * <p/>
 * Created by Paulina on 2015-08-30.
 */
public class DetailActivity extends DetailFragmentActivity {

    /**
     * Layout which would be use to showing Snackbars.
     */
    private CoordinatorLayout coordinatorLayout;

    //
    //  ACTIVITY LIFECYCLE METHODS
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.movie_detail_container);

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

        // Preparing to color status bar from 20% black to dynamic, depending on movie bitmaps.
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar_color));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }


    //
    //  BASE CLASS METHODS
    //

    @Override
    public void onActionBarParamsChanged(int wrapperHeight, int color, int scrollPosition) {
        super.onActionBarParamsChanged(wrapperHeight, color, scrollPosition);

        // In this Activity (which is concern only on phones) I upgrade this method and color also
        // a status bar.
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float changingDistance = wrapperHeight - getDetailFragmentToolbar().getHeight();
            int currentStatusBarColor = ColorUtils.getProportionalColor(
                    ContextCompat.getColor(this, R.color.status_bar_color),
                    ColorUtils.getColorWithTranslateBrightness(color, -20),
                    changingDistance,
                    scrollPosition);
            getWindow().setStatusBarColor(currentStatusBarColor);
        }
    }

    @Override
    protected CoordinatorLayout getCoordinatorLayout() {
        return coordinatorLayout;
    }
}
