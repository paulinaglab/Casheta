package com.shaftapps.pglab.popularmovies;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by Paulina on 2015-08-30.
 */
public class DetailActivity extends AppCompatActivity implements DetailFragment.OnScrollListener {

    private Toolbar toolbar;
    private String title;

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

        MovieData movieData = getIntent().getParcelableExtra(MovieData.EXTRA_KEY);
        title = movieData.title;

        // Loading saved state
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieData.EXTRA_KEY, movieData);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onScrollChanged(int range, int color, int scrollPosition) {
        // Paint Toolbar background
        float changingDistance = range - toolbar.getHeight();
        float progress = Math.min(Math.max(scrollPosition, 0), changingDistance)
                / changingDistance;
        color = Color.argb((int) (255 * progress),
                Color.red(color),
                Color.green(color),
                Color.blue(color));
        toolbar.setBackgroundColor(color);

        if (progress == 1)
            toolbar.setTitle(title);
        else toolbar.setTitle("");
    }
}
