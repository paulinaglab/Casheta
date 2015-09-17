package com.shaftapps.pglab.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shaftapps.pglab.popularmovies.R;

/**
 * Created by Paulina on 2015-09-14.
 */
public abstract class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieItemViewHolder> {

    protected Context context;

    public MoviesAdapter(Context context) {
        this.context = context;
    }

    /**
     * Method called when any item has been clicked.
     *
     * @param clickedPos clicked item position
     */
    public abstract void itemClicked(int clickedPos);

    @Override
    public MovieItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_grid_item, parent, false);
        final MovieItemViewHolder movieItemViewHolder = new MovieItemViewHolder(view);
        // Setting listener for item view
        movieItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClicked(movieItemViewHolder.getAdapterPosition());
            }
        });
        return movieItemViewHolder;
    }

    public class MovieItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView poster;

        public MovieItemViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.poster_image);
        }
    }

}
