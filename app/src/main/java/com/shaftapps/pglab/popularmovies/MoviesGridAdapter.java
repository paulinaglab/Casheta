package com.shaftapps.pglab.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by Paulina on 2015-08-28.
 */
public class MoviesGridAdapter extends RecyclerView.Adapter {

    private Context context;
    private MovieData[] movieDatas;
    private OnItemClickListener onItemClickListener;

    public MoviesGridAdapter(Context context, MovieData[] movieDatas) {
        this.context = context;
        this.movieDatas = movieDatas;
    }

    public void setMovieDatas(MovieData[] movieDatas) {
        this.movieDatas = movieDatas;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void itemClicked(int clickedPos){
       if (onItemClickListener != null)
           onItemClickListener.onItemClicked(movieDatas[clickedPos]);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_grid_item, parent, false);
        final MovieItemViewHolder movieItemViewHolder = new MovieItemViewHolder(view);
        movieItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClicked(movieItemViewHolder.getAdapterPosition());
            }
        });
        return movieItemViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MovieItemViewHolder movieItemViewHolder = (MovieItemViewHolder) holder;
        Glide.with(context)
                .load(movieDatas[position].posterUrl)
                .fitCenter()
                .placeholder(R.color.grid_placeholder_bg)
                .into(movieItemViewHolder.poster);
    }

    @Override
    public int getItemCount() {
        return movieDatas.length;
    }

    public class MovieItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView poster;

        public MovieItemViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.poster_image);
        }
    }

    public interface OnItemClickListener {
        public void onItemClicked(MovieData clicked);
    }
}
