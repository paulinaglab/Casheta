package com.shaftapps.pglab.popularmovies.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.asynctasks.FetchMoviesTask;
import com.shaftapps.pglab.popularmovies.data.MovieContract;

/**
 * Created by Paulina on 2015-10-08.
 */
public class MostPopularFragment extends NetworkMoviesCategoryFragment
        implements View.OnClickListener {

    private RecyclerView moviesGridRecycleView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_most_popular, container, false);

        moviesGridRecycleView = (RecyclerView) fragmentView.findViewById(R.id.movies_recycler_view);

        ProgressBar progressBar = (ProgressBar) fragmentView.findViewById(R.id.movies_progress_bar);
        setupProgressBar(progressBar);

        View emptyStateView = fragmentView.findViewById(R.id.movies_empty_state_loading_error);
        setupEmptyStateView(emptyStateView);

        Button tryAgainButton = (Button) fragmentView.findViewById(R.id.movies_try_again_loading_button);
        tryAgainButton.setOnClickListener(this);

        return fragmentView;
    }

    @Override
    @FetchMoviesTask.QueryType
    protected int getQueryType() {
        return FetchMoviesTask.MOST_POPULAR;
    }

    @NonNull
    @Override
    protected RecyclerView getMoviesGridRecyclerView() {
        return moviesGridRecycleView;
    }

    @Override
    protected CursorLoader getMoviesCursorLoader() {
        return new CursorLoader(
                getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_MOST_POPULAR + "<>?",
                new String[]{"NULL"},
                MovieContract.MovieEntry.COLUMN_MOST_POPULAR + " DESC");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.movies_try_again_loading_button) {
            tryFetchAgain();
        }
    }
}
