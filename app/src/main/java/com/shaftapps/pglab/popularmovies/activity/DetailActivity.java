package com.shaftapps.pglab.popularmovies.activity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.shaftapps.pglab.popularmovies.Keys;
import com.shaftapps.pglab.popularmovies.fragment.DetailFragment;
import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.data.MovieContract;
import com.shaftapps.pglab.popularmovies.util.ColorUtils;

/**
 * Activity showing details of specific movie.
 * <p/>
 * Created by Paulina on 2015-08-30.
 */
public class DetailActivity extends AppCompatActivity implements DetailFragment.OnScrollChangedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIE_LOADER_ID = 1;

    private Toolbar toolbar;
    private String title;

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

        updateTitle();
    }

    private void updateTitle() {
        // Show title on toolbar when background is opaque.
        if ((toolbar.getBackground() instanceof ColorDrawable) &&
                Color.alpha(((ColorDrawable) toolbar.getBackground()).getColor()) == 255)
            toolbar.setTitle(title);
        else toolbar.setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);

        initFavoriteMenuItem(menu);

        return true;
    }

    private void initFavoriteMenuItem(Menu menu) {
        // Is this movie favorite?
        Cursor cursor = getContentResolver().query(
                getIntent().getData(),
                new String[]{MovieContract.MovieEntry.COLUMN_FAVORITE},
                null,
                null,
                null);
        int columnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE);
        cursor.moveToFirst();
        boolean favorite = cursor.getInt(columnIndex) == 1;
        cursor.close();

        // Show adequate menu item
        MenuItem item = menu.findItem(R.id.action_favorite);
        setFavoriteItemChecked(item, favorite);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            setFavoriteItemChecked(item, !item.isChecked());
            updateMovie(item.isChecked());
            Log.i(getClass().getSimpleName(), "Fav icon clicked! Now is set to " + item.isChecked());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFavoriteItemChecked(MenuItem item, boolean checked) {
        item.setChecked(checked);
        if (checked) {
            item.setIcon(R.drawable.ic_favorite_on);
        } else {
            item.setIcon(R.drawable.ic_favorite_off);
        }
    }

    private void updateMovie(boolean favorite) {
        if (favorite) {
            // Add movie to favorites
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, true);
            getContentResolver().update(
                    MovieContract.MovieEntry.CONTENT_URI,
                    contentValues,
                    MovieContract.MovieEntry._ID + "=?",
                    new String[]{Long.toString(ContentUris.parseId(getIntent().getData()))});
        } else {
            // Remove movie from favorites
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, false);
            getContentResolver().update(
                    MovieContract.MovieEntry.CONTENT_URI,
                    contentValues,
                    MovieContract.MovieEntry._ID + "=?",
                    new String[]{Long.toString(ContentUris.parseId(getIntent().getData()))});
            //TODO: undo snackbar
            //TODO: highest rated/most popular ?: flag remove on exit
        }
    }

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
        updateTitle();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
