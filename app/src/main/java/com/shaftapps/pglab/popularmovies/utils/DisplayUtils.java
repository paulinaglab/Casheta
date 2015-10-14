package com.shaftapps.pglab.popularmovies.utils;

import android.content.Context;

import com.shaftapps.pglab.popularmovies.R;

/**
 * Utility for screen properties.
 * <p/>
 * Created by Paulina on 2015-09-30.
 */
public class DisplayUtils {

    public static boolean isSmallestWidth600dp(Context context) {
        return context.getResources().getBoolean(R.bool.sw600dp);
    }
}
