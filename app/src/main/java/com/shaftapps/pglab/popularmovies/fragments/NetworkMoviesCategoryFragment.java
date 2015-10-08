package com.shaftapps.pglab.popularmovies.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;

import com.shaftapps.pglab.popularmovies.asynctasks.FetchMoviesTask;

/**
 * Created by Paulina on 2015-10-08.
 */
public abstract class NetworkMoviesCategoryFragment extends BaseMoviesCategoryFragment
        implements FetchMoviesTask.DurationListener {

    private static final String MOVIES_FETCHED_KEY = "movies_fetched_key";

    private ProgressBar progressBar;

    private FetchMoviesTask fetchMoviesTask;

    private boolean moviesFetched;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            moviesFetched = savedInstanceState.getBoolean(MOVIES_FETCHED_KEY);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!moviesFetched) {
            fetchMoviesTask = new FetchMoviesTask(getActivity(), getQueryType());
            fetchMoviesTask.setDurationListener(this);
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
        outState.putBoolean(MOVIES_FETCHED_KEY, moviesFetched);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void initMoviesGridLoader() {
        if (moviesFetched)
            super.initMoviesGridLoader();
    }

    protected void setupProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    protected abstract FetchMoviesTask.QueryType getQueryType();

    @Override
    public void onTaskStart() {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
        hideEmptyStateView();
    }

    @Override
    public void onTaskEnd(FetchMoviesTask.QueryType queryType) {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        hideEmptyStateView();

        moviesFetched = true;
        getLoaderManager().initLoader(getMoviesGridLoaderId(), null, this);
    }

    @Override
    public void onTaskFailed(FetchMoviesTask.QueryType queryType) {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        showEmptyStateView();
    }
}
