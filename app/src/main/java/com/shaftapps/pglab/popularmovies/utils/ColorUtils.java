package com.shaftapps.pglab.popularmovies.utils;

import android.graphics.Color;

/**
 * Utility class performing actions on colors.
 * <p/>
 * Created by Paulina on 2015-09-03.
 */
public class ColorUtils {

    /**
     * Method returning given color modified to lighter color (positive translation) or darker
     * (negative translation).
     *
     * @param primaryColor basic color
     * @param translation  positive or negative value of color translation
     * @return lighter/darker color
     */
    public static int getColorWithTranslateBrightness(int primaryColor, int translation) {
        if (translation >= 0) {
            return Color.argb(Color.alpha(primaryColor),
                    Math.min(Color.red(primaryColor) + translation, 255),
                    Math.min(Color.green(primaryColor) + translation, 255),
                    Math.min(Color.blue(primaryColor) + translation, 255));
        } else {
            return Color.argb(Color.alpha(primaryColor),
                    Math.max(Color.red(primaryColor) + translation, 0),
                    Math.max(Color.green(primaryColor) + translation, 0),
                    Math.max(Color.blue(primaryColor) + translation, 0));
        }
    }


    /**
     * Method returning color with modified alpha proportional to given values.
     *
     * @param color     color to modify
     * @param fullValue total value
     * @param partValue part of fullValue. When partValue equals fullValue, the alpha is 255.
     * @return color with alpha relative to partValue/fullValue ratio.
     */
    public static int getColorWithProportionalAlpha(int color, float fullValue, float partValue) {
        float progress = Math.min(Math.max(partValue, 0), fullValue) / fullValue;
        return Color.argb(
                (int) (Color.alpha(color) * progress),
                Color.red(color),
                Color.green(color),
                Color.blue(color));
    }


    /**
     * Method returning color between start and end color proportional to given values.
     *
     * @param colorStart start color
     * @param colorEnd end color
     * @param fullValue total value
     * @param partValue part of fullValue. When partValue equals 0, returning color is colorStart,
     *                  when partValue is fullValue returning color is endColor. Otherwise returning
     *                  color is from between those, relative to partValue/fullValue ratio.
     * @return color from between start and end color relative to partValue/fullValue ratio.
     */
    public static int getProportionalColor(int colorStart, int colorEnd, float fullValue, float partValue) {
        float progress = Math.min(Math.max(partValue, 0), fullValue) / fullValue;
        return Color.argb(
                (int) (Color.alpha(colorStart) * (1 - progress) + Color.alpha(colorEnd) * progress),
                (int) (Color.red(colorStart) * (1 - progress) + Color.red(colorEnd) * progress),
                (int) (Color.green(colorStart) * (1 - progress) + Color.green(colorEnd) * progress),
                (int) (Color.blue(colorStart) * (1 - progress) + Color.blue(colorEnd) * progress));
    }
}