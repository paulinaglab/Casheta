package com.shaftapps.pglab.popularmovies.fragment;

import android.app.Activity;
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
import com.shaftapps.pglab.popularmovies.MovieData;
import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.adapter.FavoriteMoviesAdapter;
import com.shaftapps.pglab.popularmovies.adapter.SimpleMoviesAdapter;
import com.shaftapps.pglab.popularmovies.data.MovieContract;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Fragment with grid of loaded movies.
 * <p/>
 * Created by Paulina on 2015-08-30.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int FAVORITE_LOADER_ID = 1;

    private static final String MOST_POPULAR_KEY = "most_popular";
    private static final String HIGHEST_RATED_KEY = "highest_rated";

    private ArrayList<MovieData> mostPopularMovies;
    private ArrayList<MovieData> highestRatedMovies;

    private OnMovieSelectListener movieSelectListener;
    private RecyclerView recyclerView;
    private SimpleMoviesAdapter simpleMoviesAdapter;
    private FavoriteMoviesAdapter favoriteMoviesAdapter;
    private ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Restoring previously loaded movies (if it is possible).
        if (savedInstanceState != null) {
            mostPopularMovies = savedInstanceState.getParcelableArrayList(MOST_POPULAR_KEY);
            highestRatedMovies = savedInstanceState.getParcelableArrayList(HIGHEST_RATED_KEY);
        }

        View fragmentView = inflater.inflate(R.layout.fragment_movies, container, false);

        // RecyclerView initialization
        simpleMoviesAdapter = new SimpleMoviesAdapter(getActivity(), new ArrayList<MovieData>());
        simpleMoviesAdapter.setOnItemClickListener(new SimpleMoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(MovieData clickedMovieData) {
                movieSelected(clickedMovieData);
            }
        });

        favoriteMoviesAdapter = new FavoriteMoviesAdapter(getActivity());
        favoriteMoviesAdapter.setOnItemClickListener(new FavoriteMoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(Uri uri) {
                movieSelected(uri);
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);

        recyclerView =
                (RecyclerView) fragmentView.findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(simpleMoviesAdapter);

        // ProgressBar initialization
        progressBar = (ProgressBar) fragmentView.findViewById(R.id.movies_progress_bar);

        return fragmentView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOST_POPULAR_KEY, mostPopularMovies);
        outState.putParcelableArrayList(HIGHEST_RATED_KEY, highestRatedMovies);
        super.onSaveInstanceState(outState);
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
        getLoaderManager().initLoader(FAVORITE_LOADER_ID, null, this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        movieSelectListener = null;
    }


    public void movieSelected(MovieData movieData) {
        if (movieSelectListener != null) {
            movieSelectListener.onMovieSelect(movieData);
        }
    }

    public void movieSelected(Uri uri) {
        if (movieSelectListener != null) {
            movieSelectListener.onMovieSelect(uri);
        }
    }

    public void loadRequiredMovies(SortingMode sortingMode, boolean scrollTop) {
        switchAdapter(sortingMode);
        switch (sortingMode) {
            case MOST_POPULAR:
                if (mostPopularMovies == null) {
                    FetchMoviesTask fetchMoviesTask = new FetchMostPopularTask();
                    fetchMoviesTask.execute();
                } else {
                    simpleMoviesAdapter.setMovieDatas(mostPopularMovies);
                    updateGrid(scrollTop);
                }
                break;
            case HIGHEST_RATED:
                if (highestRatedMovies == null) {
                    FetchMoviesTask fetchMoviesTask = new FetchHighestRatedTask();
                    fetchMoviesTask.execute();
                } else {
                    simpleMoviesAdapter.setMovieDatas(highestRatedMovies);
                    updateGrid(scrollTop);
                }
                break;
        }
    }

    private void switchAdapter(SortingMode sortingMode) {
        switch (sortingMode) {
            case MOST_POPULAR:
            case HIGHEST_RATED:
                if (recyclerView.getAdapter() != simpleMoviesAdapter)
                    recyclerView.setAdapter(simpleMoviesAdapter);
                break;
            case FAVORITES:
                if (recyclerView.getAdapter() != favoriteMoviesAdapter)
                    recyclerView.setAdapter(favoriteMoviesAdapter);
                break;
        }
    }

    private void updateGrid(boolean scrollTop) {
        simpleMoviesAdapter.notifyDataSetChanged();
        if (scrollTop)
            recyclerView.smoothScrollToPosition(0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_FAVORITE + "=?",
                new String[]{Integer.toString(1)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        favoriteMoviesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        favoriteMoviesAdapter.swapCursor(null);
    }


    /**
     * Listener for movie selection.
     */
    public interface OnMovieSelectListener {
        /**
         * Triggered when user selects a movie from grid.
         *
         * @param movieData data of selected movie
         */
        void onMovieSelect(MovieData movieData);

        /**
         * Triggered when user selects a movie from grid.
         *
         * @param uri uri of selected movie
         */
        void onMovieSelect(Uri uri);
    }


    /**
     * Class used to get most popular movies from themoviedb.org.
     */
    public class FetchMostPopularTask extends FetchMoviesTask {

        private static final String SORT_BY = "sort_by";
        private static final String POPULARITY = "popularity.desc";

        @Override
        protected String getUrl() {
            return getUriBuilder()
                    .appendQueryParameter(SORT_BY, POPULARITY)
                    .build()
                    .toString();
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            simpleMoviesAdapter.setMovieDatas(new ArrayList<MovieData>());
            updateGrid(false);
        }

        @Override
        protected void onPostExecute(ArrayList<MovieData> movieDatas) {
            progressBar.setVisibility(View.GONE);
            if (movieDatas == null)
                Toast.makeText(getActivity(), R.string.error_fetching_movies, Toast.LENGTH_SHORT).show();
            else {
                mostPopularMovies = movieDatas;
                simpleMoviesAdapter.setMovieDatas(movieDatas);
                updateGrid(true);
            }
        }
    }


    /**
     * Class used to get highest rated movies from themoviedb.org.
     */
    public class FetchHighestRatedTask extends FetchMoviesTask {

        private static final String SORT_BY = "sort_by";
        private static final String VOTE_AVERAGE = "vote_average.desc";
        private static final String VOTE_MIN = "vote_count.gte";
        private static final String VOTE_MIN_VALUE = "1000";


        @Override
        protected String getUrl() {
            return getUriBuilder()
                    .appendQueryParameter(SORT_BY, VOTE_AVERAGE)
                    .appendQueryParameter(VOTE_MIN, VOTE_MIN_VALUE)
                    .build()
                    .toString();
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            simpleMoviesAdapter.setMovieDatas(new ArrayList<MovieData>());
            updateGrid(false);
        }

        @Override
        protected void onPostExecute(ArrayList<MovieData> movieDatas) {
            progressBar.setVisibility(View.GONE);
            if (movieDatas == null)
                Toast.makeText(getActivity(), R.string.error_fetching_movies, Toast.LENGTH_SHORT).show();
            else {
                highestRatedMovies = movieDatas;
                simpleMoviesAdapter.setMovieDatas(movieDatas);
                updateGrid(true);
            }
        }
    }

    public enum SortingMode implements Serializable {
        MOST_POPULAR, HIGHEST_RATED, FAVORITES
    }

}
