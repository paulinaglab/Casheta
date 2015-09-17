package com.shaftapps.pglab.popularmovies.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Custom ScrollView notifying that scrolling has occurred.
 *
 * Created by Paulina on 2015-09-05.
 */
public class NotifyingScrollView extends ScrollView {

    private OnScrollChangedListener onScrollChangedListener;

    public NotifyingScrollView(Context context) {
        super(context);
    }

    public NotifyingScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotifyingScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollChangedListener != null) {
            onScrollChangedListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        onScrollChangedListener = listener;
    }

    /**
     * Listener for scrolling.
     */
    public interface OnScrollChangedListener {
        /**
         * Triggered when scrolling has occurred.
         *
         * @param l current horizontal scroll origin
         * @param t current vertical scroll origin
         * @param oldl previous horizontal scroll origin
         * @param oldt previous vertical scroll origin
         */
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }
}