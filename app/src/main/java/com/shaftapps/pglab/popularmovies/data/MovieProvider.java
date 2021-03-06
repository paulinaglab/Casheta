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

import com.shaftapps.pglab.popularmovies.MovieDBApiKeys;

/**
 * Created by Paulina on 2015-09-11.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private MovieDbHelper movieDbHelper;

    static final int CODE_MOVIES = 100;
    static final int CODE_MOVIE_ITEM = 101;
    static final int CODE_REVIEWS_OF_MOVIE = 200;
    static final int CODE_VIDEOS_OF_MOVIE = 300;
    static final int CODE_VIDEO_ITEM = 301;
    static final int CODE_VIDEO_BEST_TO_SHARE = 302;

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case CODE_MOVIES:
                cursor = getMovies(projection, selection, selectionArgs, sortOrder);
                break;
            case CODE_MOVIE_ITEM:
                cursor = getMovieItem(uri, projection, selection, selectionArgs, sortOrder);
                break;
            case CODE_REVIEWS_OF_MOVIE:
                cursor = getReviewsForMovie(uri, projection, selection, selectionArgs, sortOrder);
                break;
            case CODE_VIDEOS_OF_MOVIE:
                cursor = getVideosForMovie(uri, projection, selection, selectionArgs, sortOrder);
                break;
            case CODE_VIDEO_ITEM:
                cursor = getVideoItem(uri, projection, selection, selectionArgs, sortOrder);
                break;
            case CODE_VIDEO_BEST_TO_SHARE:
                cursor = getVideoBestToShare(uri, projection, selection, selectionArgs, sortOrder);
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

    private Cursor getReviewsForMovie(Uri uri, String[] projection, String selection,
                                      String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MovieContract.ReviewEntry.TABLE_NAME);
        queryBuilder.appendWhere(MovieContract.ReviewEntry.COLUMN_MOVIE_ID + "=" +
                uri.getPathSegments().get(MovieContract.PATH_REVIEW_MOVIE_ID_INDEX));

        return queryBuilder.query(movieDbHelper.getReadableDatabase(), projection, selection,
                selectionArgs, null, null, sortOrder);
    }

    private Cursor getVideosForMovie(Uri uri, String[] projection, String selection,
                                     String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MovieContract.VideoEntry.TABLE_NAME);
        queryBuilder.appendWhere(MovieContract.VideoEntry.COLUMN_MOVIE_ID + "=" +
                uri.getPathSegments().get(MovieContract.PATH_VIDEO_MOVIE_ID_INDEX));

        return queryBuilder.query(movieDbHelper.getReadableDatabase(), projection, selection,
                selectionArgs, null, null, sortOrder);
    }

    private Cursor getVideoItem(Uri uri, String[] projection, String selection,
                                String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MovieContract.VideoEntry.TABLE_NAME);
        queryBuilder.appendWhere(MovieContract.VideoEntry._ID + "=" +
                ContentUris.parseId(uri));

        return queryBuilder.query(movieDbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor getVideoBestToShare(Uri uri, String[] projection, String selection,
                                       String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MovieContract.VideoEntry.TABLE_NAME);

        // I assume that the most preferred video to share is any trailer.
        queryBuilder.appendWhere(
                MovieContract.VideoEntry.COLUMN_MOVIE_ID + "=" +
                        uri.getPathSegments().get(MovieContract.PATH_TRAILER_MOVIE_ID_INDEX) +
                        " AND " +
                        MovieContract.VideoEntry.COLUMN_TYPE + "=\"" +
                        MovieDBApiKeys.VIDEO_TYPE_TRAILER + "\"");

        Cursor cursor = queryBuilder.query(movieDbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder, "1");
        if (cursor.moveToFirst())
            return cursor;

        cursor.close();

        // If there is no trailer for this movie, other video has to be sufficient.
        queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.appendWhere(
                MovieContract.VideoEntry.COLUMN_MOVIE_ID + "=" +
                        uri.getPathSegments().get(MovieContract.PATH_TRAILER_MOVIE_ID_INDEX));
        queryBuilder.setTables(MovieContract.VideoEntry.TABLE_NAME);

        return queryBuilder.query(movieDbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder, "1");
    }

    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case CODE_MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case CODE_MOVIE_ITEM:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case CODE_REVIEWS_OF_MOVIE:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case CODE_VIDEOS_OF_MOVIE:
                return MovieContract.VideoEntry.CONTENT_TYPE;
            case CODE_VIDEO_ITEM:
            case CODE_VIDEO_BEST_TO_SHARE:
                return MovieContract.VideoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Undefined URI code: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = movieDbHelper.getWritableDatabase();
        Uri returnUri;

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIES: {
                try {
                    long _id = database.insertOrThrow(MovieContract.MovieEntry.TABLE_NAME, null, values);
                    returnUri = MovieContract.MovieEntry.buildUri(_id);
                } catch (SQLException e) {
                    return null;
                }
                break;
            }
            case CODE_REVIEWS_OF_MOVIE: {
                long _id = database.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    long movieId = Long.parseLong(
                            uri.getPathSegments().get(MovieContract.PATH_REVIEW_MOVIE_ID_INDEX));
                    returnUri = MovieContract.ReviewEntry.buildUriByMovieId(movieId);
                } else
                    return null;
                break;
            }
            case CODE_VIDEOS_OF_MOVIE: {
                long _id = database.insert(MovieContract.VideoEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    long movieId = Long.parseLong(
                            uri.getPathSegments().get(MovieContract.PATH_VIDEO_MOVIE_ID_INDEX));
                    returnUri = MovieContract.VideoEntry.buildUriByMovieId(movieId);
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
            case CODE_MOVIES:
                rowsDeleted = database.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CODE_REVIEWS_OF_MOVIE: {
                long movieId = ContentUris.parseId(uri);
                rowsDeleted = database.delete(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{Long.toString(movieId)}
                );
                break;
            }
            case CODE_VIDEOS_OF_MOVIE: {
                long movieId = ContentUris.parseId(uri);
                rowsDeleted = database.delete(
                        MovieContract.VideoEntry.TABLE_NAME,
                        MovieContract.VideoEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{Long.toString(movieId)}
                );
                break;
            }
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
            case CODE_MOVIES:
                rowsUpdated = database.update(
                        MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CODE_REVIEWS_OF_MOVIE:
                rowsUpdated = database.update(
                        MovieContract.ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CODE_VIDEOS_OF_MOVIE:
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

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase database = movieDbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIES:
                int updatedCount = 0;
                database.beginTransaction();
                try {
                    // If insert is not possible, Provider is trying to update row.
                    for (ContentValues value : values) {
                        try {
                            database.insertOrThrow(MovieContract.MovieEntry.TABLE_NAME, null, value);
                            updatedCount++;
                        } catch (SQLException e) {
                            updatedCount += database.update(
                                    MovieContract.MovieEntry.TABLE_NAME,
                                    value,
                                    MovieContract.MovieEntry._ID + "=?",
                                    new String[]{value.getAsString(MovieContract.MovieEntry._ID)});
                        }
                    }
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return updatedCount;
            default:
                throw new UnsupportedOperationException("Unsupported URI code: " + uri);
        }
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Movies
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIE, CODE_MOVIES);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIE + "/#", CODE_MOVIE_ITEM);

        // Reviews
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_REVIEW + "/" +
                        MovieContract.PATH_MOVIE + "/#", CODE_REVIEWS_OF_MOVIE);

        // Videos
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_VIDEO + "/" +
                        MovieContract.PATH_MOVIE + "/#", CODE_VIDEOS_OF_MOVIE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_VIDEO + "/#", CODE_VIDEO_ITEM);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_VIDEO + "/" +
                        MovieContract.PATH_TRAILER + "/" +
                        MovieContract.PATH_MOVIE + "/#", CODE_VIDEO_BEST_TO_SHARE);

        return matcher;
    }
}
