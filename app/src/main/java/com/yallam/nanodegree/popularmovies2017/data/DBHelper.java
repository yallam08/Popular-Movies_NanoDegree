package com.yallam.nanodegree.popularmovies2017.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yallam.nanodegree.popularmovies2017.data.DBContract.MovieEntry;


public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "popular_movies.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " ( " +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " VARCHAR(255) NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " VARCHAR(255) NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " VARCHAR(12) NOT NULL, " +
                MovieEntry.COLUMN_IS_FAV + " INTEGER DEFAULT 0, " +
                MovieEntry.COLUMN_SORT_BY + " VARCHAR(255) NOT NULL, " +
                "UNIQUE(" + MovieEntry.COLUMN_MOVIE_ID + ")) ";

        db.execSQL(CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);

        this.onCreate(db);
    }
}
