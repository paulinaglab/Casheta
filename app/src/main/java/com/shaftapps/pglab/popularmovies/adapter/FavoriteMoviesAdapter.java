package com.shaftapps.pglab.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.bumptech.glide.Glide;
import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.data.MovieContract;

/**
 * Created by Paulina on 2015-09-14.
 */
public class FavoriteMoviesAdapter extends MoviesAdapter {

    private Cursor cursor;
    protected OnItemClickListener onItemClickListener;

    public FavoriteMoviesAdapter(Context context) {
        super(context);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
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
            if (this.cursor != null)
                this.cursor.close();
            this.cursor = cursor;
            notifyDataSetChanged();
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
