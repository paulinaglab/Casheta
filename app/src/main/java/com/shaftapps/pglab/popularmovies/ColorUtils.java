package com.shaftapps.pglab.popularmovies;

import android.graphics.Color;

/**
 * Created by Paulina on 2015-09-03.
 */
public class ColorUtils {

    public static int getColorWithTranslateBrightness(int primaryColor, int translation) {
        int color;
        if (translation >= 0) {
           return color = Color.argb(Color.alpha(primaryColor),
                    (Color.red(primaryColor) + translation < 255) ? Color.red(primaryColor) + translation : 255,
                    (Color.green(primaryColor) + translation < 255) ? Color.green(primaryColor) + translation : 255,
                    (Color.blue(primaryColor) + translation < 255) ? Color.blue(primaryColor) + translation : 255);
        } else {
           return color = Color.argb(Color.alpha(primaryColor),
                    (Color.red(primaryColor) + translation > 0) ? Color.red(primaryColor) + translation : 0,
                    (Color.green(primaryColor) + translation > 0) ? Color.green(primaryColor) + translation : 0,
                    (Color.blue(primaryColor) + translation > 0) ? Color.blue(primaryColor) + translation : 0);
        }
    }
}