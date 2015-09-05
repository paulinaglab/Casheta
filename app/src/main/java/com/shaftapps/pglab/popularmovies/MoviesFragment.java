package com.shaftapps.pglab.popularmovies;

import android.app.Activity;
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
 *
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mostPopularMovies = savedInstanceState.getParcelableArrayList(MOST_POPULAR_KEY);
            highestRatedMovies = savedInstanceState.getParcelableArrayList(HIGHEST_RATED_KEY);
        }

        View fragmentView = inflater.inflate(R.layout.fragment_movies, container, false);

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

    public void movieSelected(MovieData selected) {
        if (movieSelectListener != null) {
            movieSelectListener.onMovieSelect(selected);
        }
    }

    public void loadRequiredMovies(SortingMode sortingMode) {
        switch (sortingMode) {
            case MOST_POPULAR:
                if (mostPopularMovies == null) {
                    FetchMoviesTask fetchMoviesTask = new FetchMostPopularTask();
                    fetchMoviesTask.execute();
                } else {
                    adapter.setMovieDatas(mostPopularMovies);
                    adapter.notifyDataSetChanged();
                }
                break;
            case HIGHEST_RATED:
                if (highestRatedMovies == null) {
                    FetchMoviesTask fetchMoviesTask = new FetchHighestRatedTask();
                    fetchMoviesTask.execute();
                } else {
                    adapter.setMovieDatas(highestRatedMovies);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
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

    public interface OnMovieSelectListener {
        void onMovieSelect(MovieData selected);
    }


    public class FetchMostPopularTask extends FetchMoviesTask {

        @Override
        protected String getUrl() {
            return "https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=%s";
        }

        @Override
        protected void onPostExecute(ArrayList<MovieData> movieDatas) {
            if (movieDatas == null)
                Toast.makeText(getActivity(), R.string.error_fetching_movies, Toast.LENGTH_SHORT).show();
            else {
                mostPopularMovies = movieDatas;
                adapter.setMovieDatas(movieDatas);
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
            }
        }
    }

    public class FetchHighestRatedTask extends FetchMoviesTask {

        @Override
        protected String getUrl() {
            return "https://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&vote_count.gte=1000&api_key=%s";
        }

        @Override
        protected void onPostExecute(ArrayList<MovieData> movieDatas) {
            if (movieDatas == null)
                Toast.makeText(getActivity(), R.string.error_fetching_movies, Toast.LENGTH_SHORT).show();
            else {
                highestRatedMovies = movieDatas;
                adapter.setMovieDatas(movieDatas);
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
            }
        }
    }

    public enum SortingMode implements Serializable {
        MOST_POPULAR, HIGHEST_RATED
    }

}
