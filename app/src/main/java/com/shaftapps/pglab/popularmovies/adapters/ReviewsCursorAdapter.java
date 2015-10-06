package com.shaftapps.pglab.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.data.MovieContract;

/**
 * Created by Paulina on 2015-09-29.
 */
public class ReviewsCursorAdapter extends CursorAdapter<ReviewsCursorAdapter.ReviewItemViewHolder> {

    private static final String EXPANDED_INDEX_KEY = "expanded_index";

    protected Context context;
    private int expandedIndex = -1;
    private OnItemSelectListener onItemSelectListener;


    public ReviewsCursorAdapter(Context context, Bundle savedInstanceState) {
        if (savedInstanceState != null)
            expandedIndex = savedInstanceState.getInt(EXPANDED_INDEX_KEY, -1);

        this.context = context;
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXPANDED_INDEX_KEY, expandedIndex);
    }

    @Override
    public ReviewItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        final ReviewItemViewHolder holder = new ReviewItemViewHolder(view);

        if (getItemCount() > 1) { // Because when we have only one review, I want it stay expanded.
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClicked(holder.getAdapterPosition());
                }
            });
        }
        return holder;
    }

    /**
     * Method called when any item has been clicked.
     *
     * @param clickedPos clicked item position
     */
    public void itemClicked(int clickedPos) {
        int oldExpanded = expandedIndex;
        if (expandedIndex != clickedPos) {
            expandedIndex = clickedPos;
            notifyItemChanged(expandedIndex);
        } else {
            //collapse
            expandedIndex = -1;
        }
        notifyItemChanged(oldExpanded);

        if (onItemSelectListener != null)
            onItemSelectListener.onItemSelect(clickedPos);
    }

    @Override
    public void swapCursor(Cursor cursor) {
        super.swapCursor(cursor);

        // Because when we have only one review, I want it stay expanded.
        if (getItemCount() == 1)
            expandedIndex = 0;
    }

    @Override
    public void onBindViewHolder(final ReviewItemViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.authorName.setText(cursor.getString(
                cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR)));
        holder.content.setText(cursor.getString(
                cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT)));

        if (expandedIndex != position) {
            holder.content.setMaxLines(context.getResources().getInteger(R.integer.review_collapsed_max_lines));
        } else
            holder.content.setMaxLines(Integer.MAX_VALUE);

    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    public class ReviewItemViewHolder extends RecyclerView.ViewHolder {

        TextView authorName;
        TextView content;

        public ReviewItemViewHolder(View itemView) {
            super(itemView);
            authorName = (TextView) itemView.findViewById(R.id.review_author_name_view);
            content = (TextView) itemView.findViewById(R.id.review_content_view);
        }
    }

    public interface OnItemSelectListener {

        void onItemSelect(int adapterPosition);
    }
}
