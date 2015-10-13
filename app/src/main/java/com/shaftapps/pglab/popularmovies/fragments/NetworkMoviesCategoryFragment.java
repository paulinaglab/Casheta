package com.shaftapps.pglab.popularmovies.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.shaftapps.pglab.popularmovies.FetchingState;
import com.shaftapps.pglab.popularmovies.asynctasks.BaseMovieDBTask;
import com.shaftapps.pglab.popularmovies.asynctasks.FetchMoviesTask;

/**
 * Abstract for Fragments with grid filled with movies from API, which are visible only offline.
 * Gives possibility to attach a ProgressBar. It is able to manage an empty state/error View
 * if setup.
 * <p/>
 * Created by Paulina on 2015-10-08.
 */
public abstract class NetworkMoviesCategoryFragment extends BaseMoviesCategoryFragment
        implements BaseMovieDBTask.DurationListener {

    private static final String MOVIES_FETCHED_KEY = "movies_fetched_key";

    private static final int FETCH_MOVIES_TASK_ID = 1;

    private ProgressBar progressBar;

    private FetchMoviesTask fetchMoviesTask;

    @FetchingState.State
    private int moviesFetchedState;


    //
    //  FRAGMENT LIFECYCLE METHODS
    //

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            moviesFetchedState = FetchingState.get(savedInstanceState.getInt(MOVIES_FETCHED_KEY));
        } else {
            moviesFetchedState = FetchingState.NOT_FINISHED;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateEmptyStateVisibility();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (moviesFetchedState == FetchingState.NOT_FINISHED) {
            initFetchMovieTask();
            fetchMoviesTask.execute();
        }
    }

    @Override
    public void onPause() {
        if (fetchMoviesTask != null)
            fetchMoviesTask.cancel(true);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(MOVIES_FETCHED_KEY, moviesFetchedState);
        super.onSaveInstanceState(outState);
    }


    //
    //  INITIALIZATION HELPER METHODS
    //

    @Override
    protected void initMoviesGridLoader() {
        if (moviesFetchedState == FetchingState.FETCHED)
            super.initMoviesGridLoader();
    }

    private void initFetchMovieTask() {
        fetchMoviesTask = new FetchMoviesTask(FETCH_MOVIES_TASK_ID, getActivity(), getQueryType());
        fetchMoviesTask.setDurationListener(this);
    }


    //
    //  SETUP METHODS
    //

    protected void setupProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }


    //
    //  OTHER HELPER METHODS
    //

    public void tryFetchAgain() {
        if (fetchMoviesTask != null && fetchMoviesTask.getStatus() == AsyncTask.Status.RUNNING)
            return;

        initFetchMovieTask();
        fetchMoviesTask.execute();
    }

    protected void updateEmptyStateVisibility() {
        switch (moviesFetchedState) {
            case FetchingState.FAILED:
                showEmptyStateView();
                break;
            default:
                hideEmptyStateView();
        }
    }


    //
    //  ABSTRACT METHODS
    //

    @FetchMoviesTask.QueryType
    protected abstract int getQueryType();


    //
    //  INTERFACE METHODS
    //  FetchMoviesTask.DurationListener
    //

    @Override
    public void onTaskStart(BaseMovieDBTask task) {
        if (task.getId() == FETCH_MOVIES_TASK_ID) {
            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
            moviesFetchedState = FetchingState.NOT_FINISHED;
            updateEmptyStateVisibility();
        }
    }

    @Override
    public void onTaskEnd(BaseMovieDBTask task) {
        if (task.getId() == FETCH_MOVIES_TASK_ID) {
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);

            moviesFetchedState = FetchingState.FETCHED;
            updateEmptyStateVisibility();

            getLoaderManager().initLoader(getMoviesGridLoaderId(), null, this);
        }
    }

    @Override
    public void onTaskFailed(BaseMovieDBTask task) {
        if (task.getId() == FETCH_MOVIES_TASK_ID) {
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);
            moviesFetchedState = FetchingState.FAILED;
            updateEmptyStateVisibility();
        }
    }
}
