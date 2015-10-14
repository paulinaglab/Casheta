package com.shaftapps.pglab.popularmovies.widgets;

import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Recycler ItemDecoration drawing inset dividers.
 * <p/>
 * Created by Paulina on 2015-09-30.
 */
public class ReviewItemSeparator extends RecyclerView.ItemDecoration {

    private int width;
    private int leftInsetPixel;
    private GradientDrawable separatorDrawable;

    public ReviewItemSeparator(int color, int width, int leftInsetPixel) {
        this.width = width;
        this.leftInsetPixel = leftInsetPixel;

        separatorDrawable = new GradientDrawable();
        separatorDrawable.setColor(color);
        separatorDrawable.setShape(GradientDrawable.RECTANGLE);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft() + leftInsetPixel;
        int right = parent.getWidth() - parent.getPaddingRight();

        int lastChildIndex = parent.getChildCount() - 1;
        for (int i = 0; i < lastChildIndex; i++) {
            View child = parent.getChildAt(i);

            int top = child.getBottom() - width;
            int bottom = child.getBottom();

            separatorDrawable.setBounds(left, top, right, bottom);
            separatorDrawable.draw(c);
        }
    }

}
