package com.yallam.nanodegree.popularmovies2017.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DBContract {

    public static final String CONTENT_AUTHORITY = "com.yallam.nanodegree.popularmovies2017";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        // Table name
        public static final String TABLE_NAME = "movies";

        // Columns names
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_TITLE = "original_title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_IS_FAV = "is_fav";
        public static final String COLUMN_SORT_BY = "sort_by";

        // Columns indices
        public static final int COL_ID = 0;
        public static final int COL_MOVIE_ID = 1;
        public static final int COL_POSTER_PATH = 2;
        public static final int COL_TITLE = 3;
        public static final int COL_OVERVIEW = 4;
        public static final int COL_VOTE_AVERAGE = 5;
        public static final int COL_RELEASE_DATE = 6;
        public static final int COL_IS_FAV = 7;
        public static final int COL_SORT_BY = 8;

        public static Uri buildMovieUri(int id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
