package com.shaftapps.pglab.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Paulina on 2015-09-11.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private MovieDbHelper movieDbHelper;

    static final int CODE_MOVIE = 100;
    static final int CODE_MOVIE_ITEM = 101;
    static final int CODE_REVIEW = 200;
    static final int CODE_VIDEO = 300;

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case CODE_MOVIE:
                cursor = getMovies(projection, selection, selectionArgs, sortOrder);
                break;
            case CODE_MOVIE_ITEM:
                cursor = getMovieItem(uri, projection, selection, selectionArgs, sortOrder);
                break;
            case CODE_REVIEW:
                cursor = getReviews(uri, projection, selection, selectionArgs, sortOrder);
                break;
            case CODE_VIDEO:
                cursor = getVideos(uri, projection, selection, selectionArgs, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Undefined URI code: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Cursor getMovies(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return movieDbHelper.getReadableDatabase().query(
                MovieContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getMovieItem(Uri uri, String[] projection, String selection,
                                String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME);
        queryBuilder.appendWhere(MovieContract.MovieEntry._ID + "=" +
                ContentUris.parseId(uri));

        return queryBuilder.query(movieDbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor getReviews(Uri uri, String[] projection, String selection,
                              String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MovieContract.ReviewEntry.TABLE_NAME);
        queryBuilder.appendWhere(
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + "=" +
                        uri.getPathSegments().get(MovieContract.PATH_REVIEW_MOVIE_ID_INDEX));

        return queryBuilder.query(movieDbHelper.getReadableDatabase(), projection, selection,
                selectionArgs, null, null, sortOrder);
    }

    private Cursor getVideos(Uri uri, String[] projection, String selection,
                             String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MovieContract.VideoEntry.TABLE_NAME);
        queryBuilder.appendWhere(
                MovieContract.VideoEntry.COLUMN_MOVIE_ID + "=" +
                        uri.getPathSegments().get(MovieContract.PATH_VIDEO_MOVIE_ID_INDEX));

        return queryBuilder.query(movieDbHelper.getReadableDatabase(), projection, selection,
                selectionArgs, null, null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case CODE_MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case CODE_MOVIE_ITEM:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case CODE_REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case CODE_VIDEO:
                return MovieContract.VideoEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Undefined URI code: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = movieDbHelper.getWritableDatabase();
        Uri returnUri;

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIE: {
                try {
                    long _id = database.insertOrThrow(MovieContract.MovieEntry.TABLE_NAME, null, values);
                    returnUri = MovieContract.MovieEntry.buildUri(_id);
                } catch (SQLException e) {
                    return null;
                }
                break;
            }
            case CODE_REVIEW: {
                long _id = database.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    long movieId = Long.parseLong(
                            uri.getPathSegments().get(MovieContract.PATH_REVIEW_MOVIE_ID_INDEX));
                    returnUri = MovieContract.ReviewEntry.buildUri(movieId, _id);
                } else
                    return null;
                break;
            }
            case CODE_VIDEO: {
                long _id = database.insert(MovieContract.VideoEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    long movieId = Long.parseLong(
                            uri.getPathSegments().get(MovieContract.PATH_VIDEO_MOVIE_ID_INDEX));
                    returnUri = MovieContract.VideoEntry.buildUri(movieId, _id);
                } else
                    return null;
                break;
            }
            default:
                throw new UnsupportedOperationException("Undefined URI code: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = movieDbHelper.getWritableDatabase();
        int rowsDeleted;

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIE:
                rowsDeleted = database.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case CODE_REVIEW:
                rowsDeleted = database.delete(
                        MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CODE_VIDEO:
                rowsDeleted = database.delete(
                        MovieContract.VideoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Undefined URI code: " + uri);
        }

        // Null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = movieDbHelper.getWritableDatabase();
        int rowsUpdated;

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIE:
                rowsUpdated = database.update(
                        MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CODE_REVIEW:
                rowsUpdated = database.update(
                        MovieContract.ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CODE_VIDEO:
                rowsUpdated = database.update(
                        MovieContract.VideoEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Undefined URI code: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIE,
                CODE_MOVIE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIE + "/#",
                CODE_MOVIE_ITEM);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIE + "/#/" + MovieContract.PATH_REVIEW,
                CODE_REVIEW);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIE + "/#/" + MovieContract.PATH_VIDEO,
                CODE_VIDEO);

        return matcher;
    }
}
