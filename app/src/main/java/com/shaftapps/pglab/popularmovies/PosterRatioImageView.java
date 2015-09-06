package com.shaftapps.pglab.popularmovies;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Custom ImageView, which can keep ratio 2:3 fitting one of its side.
 * <p/>
 * Created by Paulina on 2015-08-31.
 */
public class PosterRatioImageView extends ImageView {

    private static final float RATIO = 2f / 3f;   // width:height

    private Fit fit;

    public PosterRatioImageView(Context context) {
        super(context);
    }

    public PosterRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadCustomAttrs(attrs);
    }

    public PosterRatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadCustomAttrs(attrs);
    }

    private void loadCustomAttrs(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PosterRatioImageView,
                0, 0);

        try {
            fit = Fit.values()[a.getInt(R.styleable.PosterRatioImageView_fitToRatio, 0)];
        } finally {
            a.recycle();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (fit.equals(Fit.WIDTH)) {
            int heightPx = MeasureSpec.getSize(heightMeasureSpec)
                    - getPaddingTop() - getPaddingBottom();
            int widthPx = (int) (heightPx * RATIO)
                    + getPaddingLeft() + getPaddingRight();
            super.onMeasure(
                    MeasureSpec.makeMeasureSpec(widthPx, MeasureSpec.EXACTLY),
                    heightMeasureSpec);
        } else {
            int widthPx = MeasureSpec.getSize(widthMeasureSpec)
                    - getPaddingLeft() - getPaddingRight();
            int heightPx = (int) (widthPx / RATIO)
                    + getPaddingTop() + getPaddingBottom();
            super.onMeasure(
                    widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(heightPx, MeasureSpec.EXACTLY));
        }

    }

    /**
     * Custom attribute specifying, which side should be changed to keep ratio.
     */
    public enum Fit {
        WIDTH, HEIGHT
    }

}
