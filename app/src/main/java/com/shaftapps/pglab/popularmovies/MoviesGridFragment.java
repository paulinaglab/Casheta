package com.shaftapps.pglab.popularmovies;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnMovieSelectListener} interface
 * to handle interaction events.
 * Use the {@link MoviesGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoviesGridFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnMovieSelectListener movieSelectListener;
    private MoviesGridAdapter adapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MoviesGridFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoviesGridFragment newInstance(String param1, String param2) {
        MoviesGridFragment fragment = new MoviesGridFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_movies_grid, container, false);

        adapter = new MoviesGridAdapter(getActivity(), new MovieData[0]);
        adapter.setOnItemClickListener(new MoviesGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(MovieData clicked) {
                movieSelected(clicked);
            }
        });

        GridLayoutManager layoutManager =
                new GridLayoutManager(getActivity(), 3);

        RecyclerView recyclerView =
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

    @Override
    public void onStart() {
        super.onStart();
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMovieSelectListener {

        void onMovieSelect(MovieData selected);
    }

    public class FetchMoviesTask extends AsyncTask<String, Integer, MovieData[]> {

        private static final String API_KEY = "";

        @Override
        protected MovieData[] doInBackground(String[] params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            try {
                URL url = new URL(String.format("https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=%s", API_KEY));

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    moviesJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    moviesJsonStr = null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(this.getClass().getName(), "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(this.getClass().getName(), "Error closing stream", e);
                    }
                }
            }

            try {
                return MovieDataParser.getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(this.getClass().getName(), "Error parsing JSON", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieData[] movieDatas) {
            if (movieDatas == null)
                Toast.makeText(getActivity(), R.string.error_fetching_movies, Toast.LENGTH_SHORT).show();
            else {
                adapter.setMovieDatas(movieDatas);
                adapter.notifyDataSetChanged();
            }
        }
    }

}
