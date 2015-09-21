package com.shaftapps.pglab.popularmovies.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.shaftapps.pglab.popularmovies.FetchMoviesTask;
import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.CursorMoviesAdapter;
import com.shaftapps.pglab.popularmovies.data.MovieContract;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Fragment with grid of loaded movies.
 * <p/>
 * Created by Paulina on 2015-08-30.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        FetchMoviesTask.DurationListener{

    private static final int MOST_POPULAR_LOADER_ID = 1;
    private static final int HIGHEST_RATED_LOADER_ID = 2;
    private static final int FAVORITE_LOADER_ID = 3;

    private OnMovieSelectListener movieSelectListener;
    private RecyclerView recyclerView;
    private CursorMoviesAdapter cursorMoviesAdapter;
    private ProgressBar progressBar;
    private SortingMode sortingMode;

    private Cursor mostPopularCursor;
    private Cursor highestRatedCursor;
    private Cursor favoriteCursor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_movies, container, false);

        // RecyclerView initialization
        cursorMoviesAdapter = new CursorMoviesAdapter(getActivity());
        cursorMoviesAdapter.setOnItemClickListener(new CursorMoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(Uri uri) {
                movieSelected(uri);
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);

        recyclerView =
                (RecyclerView) fragmentView.findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cursorMoviesAdapter);

        // ProgressBar initialization
        progressBar = (ProgressBar) fragmentView.findViewById(R.id.movies_progress_bar);

        return fragmentView;
    }

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOST_POPULAR_LOADER_ID, null, this);
        getLoaderManager().initLoader(HIGHEST_RATED_LOADER_ID, null, this);
        getLoaderManager().initLoader(FAVORITE_LOADER_ID, null, this);
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

    public void loadRequiredMovies(SortingMode sortingMode, boolean scrollTop) {
        this.sortingMode = sortingMode;
        switch (sortingMode) {
            case MOST_POPULAR:
                if (mostPopularCursor == null || !mostPopularCursor.moveToFirst()) {
                    FetchMoviesTask task = new FetchMoviesTask(getActivity(),
                            FetchMoviesTask.QueryType.MOST_POPULAR);
                    task.setDurationListener(this);
                    task.execute();
                } else {
                    cursorMoviesAdapter.swapCursor(mostPopularCursor);
                    updateGrid(scrollTop);
                }
                break;
            case HIGHEST_RATED:
                if (highestRatedCursor == null || !highestRatedCursor.moveToFirst()) {
                    FetchMoviesTask task = new FetchMoviesTask(getActivity(),
                            FetchMoviesTask.QueryType.HIGHEST_RATED);
                    task.setDurationListener(this);
                    task.execute();
                } else {
                    cursorMoviesAdapter.swapCursor(highestRatedCursor);
                    updateGrid(scrollTop);
                }
                break;
            case FAVORITES:
                cursorMoviesAdapter.swapCursor(favoriteCursor);
                updateGrid(scrollTop);
                break;
        }
    }

    private void updateGrid(boolean scrollTop) {
        cursorMoviesAdapter.notifyDataSetChanged();
        if (scrollTop)
            recyclerView.smoothScrollToPosition(0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MOST_POPULAR_LOADER_ID:
                return new CursorLoader(
                        getActivity(),
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        MovieContract.MovieEntry.COLUMN_MOST_POPULAR + "<>?",
                        new String[]{"NULL"},
                        MovieContract.MovieEntry.COLUMN_MOST_POPULAR + " DESC");
            case HIGHEST_RATED_LOADER_ID:
                return new CursorLoader(
                        getActivity(),
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        MovieContract.MovieEntry.COLUMN_HIGHEST_RATED + "<>?",
                        new String[]{"NULL"},
                        MovieContract.MovieEntry.COLUMN_HIGHEST_RATED + " ASC");
            case FAVORITE_LOADER_ID:
                return new CursorLoader(
                        getActivity(),
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        MovieContract.MovieEntry.COLUMN_FAVORITE + "=?",
                        new String[]{Integer.toString(1)},
                        null);
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        updateCursor(loader.getId(), data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        updateCursor(loader.getId(), null);
    }

    private void updateCursor(int loaderId, Cursor data) {
        switch (loaderId) {
            case MOST_POPULAR_LOADER_ID:
                mostPopularCursor = data;
                if (sortingMode.equals(SortingMode.MOST_POPULAR))
                    cursorMoviesAdapter.swapCursor(mostPopularCursor);
                break;
            case HIGHEST_RATED_LOADER_ID:
                highestRatedCursor = data;
                if (sortingMode.equals(SortingMode.HIGHEST_RATED))
                    cursorMoviesAdapter.swapCursor(highestRatedCursor);
                break;
            case FAVORITE_LOADER_ID:
                favoriteCursor = data;
                if (sortingMode.equals(SortingMode.FAVORITES))
                    cursorMoviesAdapter.swapCursor(favoriteCursor);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loaderId);
        }
    }

    @Override
    public void onTaskStart() {
        progressBar.setVisibility(View.VISIBLE);
        cursorMoviesAdapter.swapCursor(null);
        updateGrid(false);
    }

    @Override
    public void onTaskEnd() {
        progressBar.setVisibility(View.INVISIBLE);
    }


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


    public enum SortingMode implements Serializable {
        MOST_POPULAR, HIGHEST_RATED, FAVORITES
    }

}