package com.shaftapps.pglab.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity implements MoviesFragment.OnMovieSelectListener,
        AdapterView.OnItemSelectedListener {

    private Spinner sortModeSpinner;
    private MoviesFragment moviesFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        sortModeSpinner = (Spinner) toolbar.findViewById(R.id.sort_mode_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sorting_modes, R.layout.sort_mode_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sortModeSpinner.setAdapter(adapter);

        sortModeSpinner.setOnItemSelectedListener(this);

        moviesFragment =
                (MoviesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movies);
        if (savedInstanceState == null)
            notifySortingModeSet();
    }

    private void notifySortingModeSet() {
        switch (sortModeSpinner.getSelectedItemPosition()) {
            case 0:
                moviesFragment.loadRequiredMovies(MoviesFragment.SortingMode.MOST_POPULAR);
                break;
            case 1:
                moviesFragment.loadRequiredMovies(MoviesFragment.SortingMode.HIGHEST_RATED);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieSelect(MovieData selected) {
        Intent intent = new Intent(this, DetailActivity.class).putExtra(MovieData.EXTRA_KEY, selected);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (sortModeSpinner.equals(parent)) {
            notifySortingModeSet();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
