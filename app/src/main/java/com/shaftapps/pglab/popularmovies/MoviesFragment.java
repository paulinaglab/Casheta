package com.shaftapps.pglab.popularmovies;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Fragment with grid of loaded movies.
 * <p/>
 * Created by Paulina on 2015-08-30.
 */
public class MoviesFragment extends Fragment {

    private static final String MOST_POPULAR_KEY = "most_popular";
    private static final String HIGHEST_RATED_KEY = "highest_rated";

    private ArrayList<MovieData> mostPopularMovies;
    private ArrayList<MovieData> highestRatedMovies;

    private OnMovieSelectListener movieSelectListener;
    private RecyclerView recyclerView;
    private MoviesGridAdapter adapter;


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
        adapter = new MoviesGridAdapter(getActivity(), new ArrayList<MovieData>());
        adapter.setOnItemClickListener(new MoviesGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(MovieData clicked) {
                movieSelected(clicked);
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);

        recyclerView =
                (RecyclerView) fragmentView.findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

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
    public void onDetach() {
        super.onDetach();
        movieSelectListener = null;
    }


    public void movieSelected(MovieData selected) {
        if (movieSelectListener != null) {
            movieSelectListener.onMovieSelect(selected);
        }
    }

    public void loadRequiredMovies(SortingMode sortingMode, boolean scrollTop) {
        switch (sortingMode) {
            case MOST_POPULAR:
                if (mostPopularMovies == null) {
                    FetchMoviesTask fetchMoviesTask = new FetchMostPopularTask();
                    fetchMoviesTask.execute();
                } else {
                    adapter.setMovieDatas(mostPopularMovies);
                    updateGrid(scrollTop);
                }
                break;
            case HIGHEST_RATED:
                if (highestRatedMovies == null) {
                    FetchMoviesTask fetchMoviesTask = new FetchHighestRatedTask();
                    fetchMoviesTask.execute();
                } else {
                    adapter.setMovieDatas(highestRatedMovies);
                    updateGrid(scrollTop);
                }
                break;
        }
    }

    private void updateGrid(boolean scrollTop){
        adapter.notifyDataSetChanged();
        if (scrollTop)
            recyclerView.smoothScrollToPosition(0);
    }


    /**
     * Listener for movie selection.
     */
    public interface OnMovieSelectListener {
        /**
         * Triggered when user selects a movie from grid.
         *
         * @param selected selected movie
         */
        void onMovieSelect(MovieData selected);
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
            adapter.setMovieDatas(new ArrayList<MovieData>());
            updateGrid(false);
        }

        @Override
        protected void onPostExecute(ArrayList<MovieData> movieDatas) {
            if (movieDatas == null)
                Toast.makeText(getActivity(), R.string.error_fetching_movies, Toast.LENGTH_SHORT).show();
            else {
                mostPopularMovies = movieDatas;
                adapter.setMovieDatas(movieDatas);
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
            adapter.setMovieDatas(new ArrayList<MovieData>());
            updateGrid(false);
        }

        @Override
        protected void onPostExecute(ArrayList<MovieData> movieDatas) {
            if (movieDatas == null)
                Toast.makeText(getActivity(), R.string.error_fetching_movies, Toast.LENGTH_SHORT).show();
            else {
                highestRatedMovies = movieDatas;
                adapter.setMovieDatas(movieDatas);
                updateGrid(true);
            }
        }
    }

    public enum SortingMode implements Serializable {
        MOST_POPULAR, HIGHEST_RATED
    }

}
