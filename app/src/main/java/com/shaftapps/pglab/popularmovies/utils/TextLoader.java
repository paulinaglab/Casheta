package com.shaftapps.pglab.popularmovies.utils;

import android.database.Cursor;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Paulina on 2015-10-06.
 */
public class TextLoader {

    public static void loadText(TextView textView, Cursor cursor, String columnName) {
        String textFromCursor = cursor.getString(cursor.getColumnIndex(columnName));
        textView.setText(textFromCursor);
    }

    public static void loadText(TextView textView, Cursor cursor, String columnName, String placeholderText) {
        String textFromCursor = cursor.getString(cursor.getColumnIndex(columnName));
        textView.setText(textFromCursor == null || textFromCursor.isEmpty()
                ? placeholderText : textFromCursor);
    }

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
