package com.shaftapps.pglab.popularmovies.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

/**
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

    public void swapCursor(Cursor cursor) {
        if (this.cursor != cursor) {
            this.cursor = cursor;
            notifyDataSetChanged();
        }
    }
}
