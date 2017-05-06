package com.yallam.nanodegree.popularmovies2017.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.yallam.nanodegree.popularmovies2017.data.DBContract.MovieEntry;

public class DBProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DBHelper mOpenHelper;

    static final int ALL_MOVIES = 100;
    static final int ONE_MOVIE = 101;

    private static final String sMovieSelection =
            MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DBContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DBContract.PATH_MOVIES, ALL_MOVIES);
        matcher.addURI(authority, DBContract.PATH_MOVIES + "/#", ONE_MOVIE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ALL_MOVIES:
                return MovieEntry.CONTENT_TYPE;
            case ONE_MOVIE:
                return MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "movie"
            case ALL_MOVIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "movie/#"
            case ONE_MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        null
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri = null;

        switch (match) {
            case ALL_MOVIES: {
                try {
                    // Instead of insertWithOnConflict, because it's not working
                    // https://issuetracker.google.com/issues/36923483
                    long id = db.insertOrThrow(MovieEntry.TABLE_NAME, null, values);
                    if (id > 0)
                        returnUri = MovieEntry.buildMovieUri((int) values.get(MovieEntry.COLUMN_MOVIE_ID));
                } catch (SQLException e) {
                    //Do nothing (IGNORE)
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ALL_MOVIES:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        try {
                            long id = db.insertOrThrow(MovieEntry.TABLE_NAME, null, value);
                            rowsInserted++;
                        } catch (SQLException e) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case ALL_MOVIES:
                rowsDeleted = db.delete(
                        MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ONE_MOVIE:
                rowsDeleted = db.delete(
                        MovieEntry.TABLE_NAME, MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)}
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case ALL_MOVIES:
                rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}