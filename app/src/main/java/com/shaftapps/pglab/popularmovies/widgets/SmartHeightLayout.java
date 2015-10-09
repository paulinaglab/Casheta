package com.shaftapps.pglab.popularmovies.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.shaftapps.pglab.popularmovies.R;

/**
 * Created by Paulina on 2015-10-09.
 */
public class SmartHeightLayout extends RelativeLayout {

    public SmartHeightLayout(Context context) {
        super(context);
    }

    public SmartHeightLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SmartHeightLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthPx = MeasureSpec.getSize(widthMeasureSpec);

        int heightPx = getResources().getDisplayMetrics().heightPixels -
                getResources().getDimensionPixelSize(R.dimen.status_bar_height) +
                getResources().getDimensionPixelSize(R.dimen.translucent_status_bar_padding);

        super.onMeasure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(Math.min(widthPx, heightPx), MeasureSpec.EXACTLY));
    }
}
