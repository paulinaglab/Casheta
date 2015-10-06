package com.shaftapps.pglab.popularmovies.utils;

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

    public static int getProportionalColor(int colorStart, int colorEnd, float fullValue, float partValue) {
        float progress = Math.min(Math.max(partValue, 0), fullValue) / fullValue;
        return Color.argb(
                (int) (Color.alpha(colorStart) * (1 - progress) + Color.alpha(colorEnd) * progress),
                (int) (Color.red(colorStart) * (1 - progress) + Color.red(colorEnd) * progress),
                (int) (Color.green(colorStart) * (1 - progress) + Color.green(colorEnd) * progress),
                (int) (Color.blue(colorStart) * (1 - progress) + Color.blue(colorEnd) * progress));
    }

    public static int getOverlayColor(int bottomColor, int topColor) {
        if (Color.alpha(bottomColor) == 0 && Color.alpha(topColor) == 0)
            return Color.TRANSPARENT;
        else
            return Color.argb(
                    Math.min(Color.alpha(bottomColor) + Color.alpha(topColor), 255),
                    (Color.red(bottomColor) * Color.alpha(bottomColor) + Color.red(topColor) * 255) / (Color.alpha(bottomColor) + 255),
                    (Color.green(bottomColor) * Color.alpha(bottomColor) + Color.green(topColor) * 255) / (Color.alpha(bottomColor) + 255),
                    (Color.blue(bottomColor) * Color.alpha(bottomColor) + Color.blue(topColor) * 255) / (Color.alpha(bottomColor) + 255));
    }


}