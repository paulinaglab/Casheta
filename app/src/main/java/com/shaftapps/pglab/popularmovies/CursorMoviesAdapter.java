package com.shaftapps.pglab.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.shaftapps.pglab.popularmovies.data.MovieContract;

/**
 * Created by Paulina on 2015-09-14.
 */
public class CursorMoviesAdapter extends RecyclerView.Adapter<CursorMoviesAdapter.MovieItemViewHolder> {

    protected Context context;
    private Cursor cursor;
    protected OnItemClickListener onItemClickListener;

    public CursorMoviesAdapter(Context context) {
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


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

    /**
     * Method called when any item has been clicked.
     *
     * @param clickedPos clicked item position
     */
    public void itemClicked(int clickedPos) {
        if (onItemClickListener != null) {
            cursor.moveToPosition(clickedPos);
            int idColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry._ID);
            Uri clickedUri = MovieContract.MovieEntry.buildUri(cursor.getLong(idColumnIndex));
            onItemClickListener.onItemClicked(clickedUri);
        }
    }

    @Override
    public void onBindViewHolder(MovieItemViewHolder holder, int position) {
        cursor.moveToPosition(position);
        int posterColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_URL);
        Glide.with(context)
                .load(cursor.getString(posterColumnIndex))
                .fitCenter()
                .placeholder(R.color.grid_placeholder_bg)
                .into(holder.poster);
    }

    @Override
    public int getItemCount() {
        if (cursor != null)
            return cursor.getCount();
        else
            return 0;
    }

    public void swapCursor(Cursor cursor) {
        if (this.cursor != cursor) {
            this.cursor = cursor;
            notifyDataSetChanged();
        }
    }

    public class MovieItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView poster;

        public MovieItemViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.poster_image);
        }
    }


    /**
     * Listener for item (movie) selection.
     */
    public interface OnItemClickListener {
        /**
         * Triggered when user click an item from grid.
         *
         * @param uri uri of movie clicked by user
         */
        void onItemClicked(Uri uri);
    }
}
