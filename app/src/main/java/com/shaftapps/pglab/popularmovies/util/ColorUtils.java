package com.shaftapps.pglab.popularmovies.util;

import android.graphics.Color;

/**
 * Utility class performing actions on colors.
 * <p/>
 * Created by Paulina on 2015-09-03.
 */
public class ColorUtils {

    /**
     * Method modifying given color to lighter (positive translation) or darker
     * (negative translation).
     *
     * @param primaryColor basic color
     * @param translation  positive or negative value of color translation
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

    public static int getColorWithProportionalAlpha(int color, float fullValue, float partValue) {
        float progress = Math.min(Math.max(partValue, 0), fullValue) / fullValue;
        return Color.argb(
                (int) (255 * progress),
                Color.red(color),
                Color.green(color),
                Color.blue(color));
    }
}