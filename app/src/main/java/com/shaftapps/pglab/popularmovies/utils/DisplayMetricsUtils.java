package com.shaftapps.pglab.popularmovies.utils;

import android.util.DisplayMetrics;

/**
 * Created by Paulina on 2015-09-30.
 */
public class DisplayMetricsUtils {

    public static int getSmallestWidth(DisplayMetrics displayMetrics) {
        return (int) Math.min(displayMetrics.widthPixels / displayMetrics.density,
                displayMetrics.heightPixels / displayMetrics.density);
    }
}
