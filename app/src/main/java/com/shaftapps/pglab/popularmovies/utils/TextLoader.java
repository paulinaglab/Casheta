package com.shaftapps.pglab.popularmovies.utils;

import android.database.Cursor;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

/**
 * Utility for texts.
 * <p/>
 * Created by Paulina on 2015-10-06.
 */
public class TextLoader {

    /**
     * Loads text from cursor to TextView.
     *
     * @param textView   TextView which will be filled with text.
     * @param cursor     cursor containing text.
     * @param columnName name of the column with text.
     */
    public static void loadText(TextView textView, Cursor cursor, String columnName) {
        String textFromCursor = cursor.getString(cursor.getColumnIndex(columnName));
        textView.setText(textFromCursor);
    }

    /**
     * Load text from cursor to TextView. When column is empty it displays placeholderText.
     *
     * @param textView        TextView to be filled with content.
     * @param cursor          cursor which may contain text.
     * @param columnName      name of the column with text.
     * @param placeholderText text which will be displayed if cursor cell is null or empty
     */
    public static void loadText(TextView textView, Cursor cursor, String columnName, String placeholderText) {
        String textFromCursor = cursor.getString(cursor.getColumnIndex(columnName));
        textView.setText(TextUtils.isEmpty(textFromCursor) ? placeholderText : textFromCursor);
    }

    /**
     * Loads date from cursor into TextView. Uses elegant format.
     *
     * @param textView   TextView to be filled with date string.
     * @param cursor     cursor containing date formatted like this: YYYY-MM-DD
     * @param columnName name of the column containing date.
     */
    public static void loadDate(TextView textView, Cursor cursor, String columnName) {
        String dateFromCursor = cursor.getString(cursor.getColumnIndex(columnName));
        // date from API is formatted YYYY-MM-DD
        if (dateFromCursor != null && !dateFromCursor.isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Integer.parseInt(dateFromCursor.substring(0, 4)));
            calendar.set(Calendar.MONTH, Integer.parseInt(dateFromCursor.substring(5, 7)));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateFromCursor.substring(8, 10)));

            // For this app version I want only Locale.US name of month
            textView.setText(calendar.get(Calendar.DAY_OF_MONTH)
                    + " " + calendar.getDisplayName(calendar.MONTH, Calendar.LONG, Locale.US)
                    + " " + calendar.get(Calendar.YEAR));
        }
    }
}
