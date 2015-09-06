package com.shaftapps.pglab.popularmovies;

import android.graphics.Color;

/**
 * Utility class performing actions on colors.
 *
 * Created by Paulina on 2015-09-03.
 */
public class ColorUtils {

    /**
     * Method modifying given color to lighter (positive translation) or darker
     * (negative translation).
     *
     * @param primaryColor basic color
     * @param translation positive or negative value of color translation
     * @return lighter/darker color
     */
    public static int getColorWithTranslateBrightness(int primaryColor, int translation) {
        if (translation >= 0) {
           return Color.argb(Color.alpha(primaryColor),
                    (Color.red(primaryColor) + translation < 255) ? Color.red(primaryColor) + translation : 255,
                    (Color.green(primaryColor) + translation < 255) ? Color.green(primaryColor) + translation : 255,
                    (Color.blue(primaryColor) + translation < 255) ? Color.blue(primaryColor) + translation : 255);
        } else {
           return Color.argb(Color.alpha(primaryColor),
                    (Color.red(primaryColor) + translation > 0) ? Color.red(primaryColor) + translation : 0,
                    (Color.green(primaryColor) + translation > 0) ? Color.green(primaryColor) + translation : 0,
                    (Color.blue(primaryColor) + translation > 0) ? Color.blue(primaryColor) + translation : 0);
        }
    }
}