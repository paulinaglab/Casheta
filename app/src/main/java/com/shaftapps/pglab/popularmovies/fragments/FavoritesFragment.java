package com.shaftapps.pglab.popularmovies.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.data.MovieContract;

/**
 * Created by Paulina on 2015-10-07.
 */
public class FavoritesFragment extends BaseMoviesCategoryFragment {

    private RecyclerView moviesGridRecycleView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_favorites, container, false);

        moviesGridRecycleView = (RecyclerView) fragmentView.findViewById(R.id.movies_recycler_view);

        View emptyStateView = fragmentView.findViewById(R.id.movies_empty_state_no_favorite_movies);
        setupEmptyStateView(emptyStateView);

        return fragmentView;
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
                MovieContract.MovieEntry.COLUMN_FAVORITE + "=?",
                new String[]{Integer.toString(1)},
                null);
    }
}
