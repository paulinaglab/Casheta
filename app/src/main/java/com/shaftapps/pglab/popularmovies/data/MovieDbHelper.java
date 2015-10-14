package com.shaftapps.pglab.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import static com.shaftapps.pglab.popularmovies.data.MovieContract.MovieEntry;
import static com.shaftapps.pglab.popularmovies.data.MovieContract.ReviewEntry;
import static com.shaftapps.pglab.popularmovies.data.MovieContract.VideoEntry;

/**
 * Manages a local database for movie data.
 *
 * Created by Paulina on 2015-09-11.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "popularmovies.db";


    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                        MovieEntry._ID + " INTEGER PRIMARY KEY," +
                        MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_POSTER_URL + " TEXT, " +
                        MovieEntry.COLUMN_BACKDROP_URL + " TEXT, " +
                        MovieEntry.COLUMN_AVERAGE_RATE + " REAL, " +
                        MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_GENRE + " TEXT, " +
                        MovieEntry.COLUMN_COUNTRY + " TEXT, " +
                        MovieEntry.COLUMN_FAVORITE + " INTEGER, " +
                        MovieEntry.COLUMN_MOST_POPULAR + " REAL, " +
                        MovieEntry.COLUMN_HIGHEST_RATED + " INTEGER" +
                        " )";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);

        final String SQL_CREATE_REVIEW_TABLE =
                "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                        ReviewEntry._ID + " INTEGER PRIMARY KEY," +
                        ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        ReviewEntry.COLUMN_REVIEW_API_ID + " TEXT UNIQUE NOT NULL, " +
                        ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                        ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                        "FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                        MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + " ) ON DELETE CASCADE)";
        db.execSQL(SQL_CREATE_REVIEW_TABLE);

        final String SQL_CREATE_VIDEO_TABLE =
                "CREATE TABLE " + VideoEntry.TABLE_NAME + " (" +
                        VideoEntry._ID + " INTEGER PRIMARY KEY," +
                        VideoEntry.COLUMN_VIDEO_API_ID + " TEXT UNIQUE NOT NULL, " +
                        VideoEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        VideoEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                        VideoEntry.COLUMN_SITE + " TEXT NOT NULL, " +
                        VideoEntry.COLUMN_KEY + " TEXT NOT NULL, " +
                        VideoEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                        "FOREIGN KEY (" + VideoEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                        MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + " ) ON DELETE CASCADE)";
        db.execSQL(SQL_CREATE_VIDEO_TABLE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                db.setForeignKeyConstraintsEnabled(true);
            } else {
                db.execSQL("PRAGMA foreign_keys=ON");
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not needed for now
    }
}
