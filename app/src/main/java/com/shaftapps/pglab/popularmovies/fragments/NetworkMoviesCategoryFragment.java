package com.shaftapps.pglab.popularmovies.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.ProgressBar;

import com.shaftapps.pglab.popularmovies.asynctasks.FetchMoviesTask;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Abstract for Fragments with grid filled with movies from API, which are visible only offline.
 * Gives possibility to attach a ProgressBar. It is able to manage an empty state/error View
 * if setup.
 * <p/>
 * Created by Paulina on 2015-10-08.
 */
public abstract class NetworkMoviesCategoryFragment extends BaseMoviesCategoryFragment
        implements FetchMoviesTask.DurationListener {

    private static final String MOVIES_FETCHED_KEY = "movies_fetched_key";

    private ProgressBar progressBar;

    private FetchMoviesTask fetchMoviesTask;

    @FetchingState
    private int moviesFetchedState;


    //
    //  FRAGMENT LIFECYCLE METHODS
    //

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            moviesFetchedState = getFetchingState(savedInstanceState.getInt(MOVIES_FETCHED_KEY));
        } else {
            moviesFetchedState = NOT_FINISHED;
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
        if (moviesFetchedState == NOT_FINISHED) {
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
        if (moviesFetchedState == FETCHED)
            super.initMoviesGridLoader();
    }

    private void initFetchMovieTask() {
        fetchMoviesTask = new FetchMoviesTask(getActivity(), getQueryType());
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
            case FAILED:
                showEmptyStateView();
                break;
            default:
                hideEmptyStateView();
        }
    }


    //
    //  ABSTRACT METHODS
    //

    protected abstract FetchMoviesTask.QueryType getQueryType();


    //
    //  INTERFACE METHODS
    //  FetchMoviesTask.DurationListener
    //

    @Override
    public void onTaskStart() {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
        moviesFetchedState = NOT_FINISHED;
        updateEmptyStateVisibility();
    }

    @Override
    public void onTaskEnd(FetchMoviesTask.QueryType queryType) {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        moviesFetchedState = FETCHED;
        updateEmptyStateVisibility();

        getLoaderManager().initLoader(getMoviesGridLoaderId(), null, this);
    }

    @Override
    public void onTaskFailed(FetchMoviesTask.QueryType queryType) {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        moviesFetchedState = FAILED;
        updateEmptyStateVisibility();
    }


    /**
     * Logical type describes movies fetching state.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FETCHED, NOT_FINISHED, FAILED})
    public @interface FetchingState {
    }

    public static final int FETCHED = 0;
    public static final int NOT_FINISHED = 1;
    public static final int FAILED = 2;


    @FetchingState
    public int getFetchingState(int code) {
        switch (code) {
            case FETCHED:
            case NOT_FINISHED:
            case FAILED:
                return code;
            default:
                throw new RuntimeException("FetchingState: Undefined code: " + code);
        }
    }
}
