package com.shaftapps.pglab.popularmovies.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.adapters.MoviesCursorAdapter;
import com.shaftapps.pglab.popularmovies.widgets.AutoFitGridLayoutManager;

/**
 * Base class for Fragments holding grids of movies.
 * <p/>
 * Created by Paulina on 2015-10-07.
 */
public abstract class BaseMoviesCategoryFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    protected static final int MOVIES_GRID_LOADER_ID = 1;

    private OnMovieSelectListener movieSelectListener;
    private MoviesCursorAdapter moviesCursorAdapter;

    private View emptyStateView;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            movieSelectListener = (OnMovieSelectListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMovieSelectListener");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupMoviesGridRecyclerView(getMoviesGridRecyclerView());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initMoviesGridLoader();
    }

    /**
     * Inits loader of movies.
     */
    protected void initMoviesGridLoader() {
        getLoaderManager().initLoader(MOVIES_GRID_LOADER_ID, null, this);
    }

    /**
     * Method attaching RecyclerView and initializes it as grid of movies.
     *
     * @param recyclerView recycler to be initialized.
     */
    protected void setupMoviesGridRecyclerView(RecyclerView recyclerView) {
        // Movies grid's adapter initialization.
        moviesCursorAdapter = new MoviesCursorAdapter(getActivity());
        moviesCursorAdapter.setOnItemClickListener(new MoviesCursorAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(Uri uri) {
                movieSelected(uri);
            }
        });
        recyclerView.setAdapter(moviesCursorAdapter);

        // Movies grid's layout manager initialization.
        AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(
                getActivity(), R.dimen.movie_grid_preferred_column_width);
        recyclerView.setLayoutManager(layoutManager);
    }

    /**
     * Bounds view for empty state.
     *
     * @param emptyStateView view to be used as empty state.
     */
    protected void setupEmptyStateView(View emptyStateView) {
        this.emptyStateView = emptyStateView;
    }

    protected void showEmptyStateView() {
        if (emptyStateView != null)
            emptyStateView.setVisibility(View.VISIBLE);
    }

    protected void hideEmptyStateView() {
        if (emptyStateView != null)
            emptyStateView.setVisibility(View.GONE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        movieSelectListener = null;
    }

    public void movieSelected(Uri uri) {
        if (movieSelectListener != null) {
            movieSelectListener.onMovieSelect(uri);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return getMoviesCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        moviesCursorAdapter.swapCursor(data);
        if (data == null || !data.moveToFirst())
            showEmptyStateView();
        else
            hideEmptyStateView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesCursorAdapter.swapCursor(null);
    }

    protected int getMoviesGridLoaderId() {
        return MOVIES_GRID_LOADER_ID;
    }

    /**
     * Forces usage of RecyclerView in inheriting Fragments.
     *
     * @return Recycler that will be showing movies grid.
     */
    @NonNull
    protected abstract RecyclerView getMoviesGridRecyclerView();

    /**
     * Forces creation of CursorLoader to populate the grid.
     *
     * @return specific CursorLoader.
     */
    protected abstract CursorLoader getMoviesCursorLoader();

    /**
     * Listener for movie selection.
     */
    public interface OnMovieSelectListener {
        /**
         * Triggered when user selects a movie from grid.
         *
         * @param uri uri of selected movie
         */
        void onMovieSelect(Uri uri);
    }

}
