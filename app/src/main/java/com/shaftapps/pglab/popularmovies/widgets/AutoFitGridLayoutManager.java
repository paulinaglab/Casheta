package com.shaftapps.pglab.popularmovies.widgets;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Paulina on 2015-10-03.
 */
public class AutoFitGridLayoutManager extends GridLayoutManager {

    private float approxSpanSize;

    public AutoFitGridLayoutManager(Context context, int approxSpanSizeResId) {
        super(context, 1);
        approxSpanSize = context.getResources().getDimensionPixelSize(approxSpanSizeResId);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (approxSpanSize > 0) {
            float totalSpace;
            if (getOrientation() == VERTICAL) {
                totalSpace = getWidth() - getPaddingRight() - getPaddingLeft();
            } else {
                totalSpace = getHeight() - getPaddingTop() - getPaddingBottom();
            }
            int spanCount = Math.max(1, Math.round(totalSpace / approxSpanSize));
            setSpanCount(spanCount);
        }
        super.onLayoutChildren(recycler, state);
    }
}
