package com.shaftapps.pglab.popularmovies.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

/**
 * Abstract for RecyclerView adapters that expose data from a Cursor.
 * <p/>
 * Created by Paulina on 2015-09-27.
 */
public abstract class CursorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected Cursor cursor;

    @Override
    public int getItemCount() {
        if (cursor != null)
            return cursor.getCount();
        else
            return 0;
    }

    /**
     * Method to swap old cursor for a new one.
     *
     * @param cursor new cursor.
     */
    public void swapCursor(Cursor cursor) {
        if (this.cursor != cursor) {
            this.cursor = cursor;
            notifyDataSetChanged();
        }
    }
}
