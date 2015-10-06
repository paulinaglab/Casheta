package com.shaftapps.pglab.popularmovies.widgets;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import com.shaftapps.pglab.popularmovies.adapters.ReviewsCursorAdapter;

/**
 * Created by Paulina on 2015-10-06.
 */
public class ReviewsLinearLayoutManager extends LinearLayoutManager
        implements ReviewsCursorAdapter.OnItemSelectListener {

    public ReviewsLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public void onItemSelect(int adapterPosition) {
        scrollToPositionWithOffset(adapterPosition, 0);
    }
}
