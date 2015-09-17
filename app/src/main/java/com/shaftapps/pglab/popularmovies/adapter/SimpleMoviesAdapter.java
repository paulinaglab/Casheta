package com.shaftapps.pglab.popularmovies.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.shaftapps.pglab.popularmovies.MovieData;
import com.shaftapps.pglab.popularmovies.R;

import java.util.ArrayList;

/**
 * Adapter class of grid with movies.
 * <p/>
 * Created by Paulina on 2015-08-28.
 */
public class SimpleMoviesAdapter extends MoviesAdapter {

    private ArrayList<MovieData> movieDatas;
    protected OnItemClickListener onItemClickListener;

    public SimpleMoviesAdapter(Context context, ArrayList<MovieData> movieDatas) {
        super(context);
        this.movieDatas = movieDatas;
    }

    public void setMovieDatas(ArrayList<MovieData> movieDatas) {
        this.movieDatas = movieDatas;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void itemClicked(int clickedPos) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClicked(movieDatas.get(clickedPos));
    }

    @Override
    public void onBindViewHolder(MovieItemViewHolder holder, int position) {
        Glide.with(context)
                .load(movieDatas.get(position).posterUrl)
                .fitCenter()
                .placeholder(R.color.grid_placeholder_bg)
                .into(holder.poster);
    }

    @Override
    public int getItemCount() {
        return movieDatas.size();
    }

    /**
     * Listener for item (movie) selection.
     */
    public interface OnItemClickListener {
        /**
         * Triggered when user click an item from grid.
         *
         * @param clickedMovieData data of movie clicked by user
         */
        void onItemClicked(MovieData clickedMovieData);
    }

}
