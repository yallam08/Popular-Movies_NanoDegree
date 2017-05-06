package com.yallam.nanodegree.popularmovies2017.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.yallam.nanodegree.popularmovies2017.R;
import com.yallam.nanodegree.popularmovies2017.SettingsFragment;
import com.yallam.nanodegree.popularmovies2017.data.DBContract;
import com.yallam.nanodegree.popularmovies2017.utils.MoviesJsonUtils;
import com.yallam.nanodegree.popularmovies2017.utils.MoviesNetworkUtils;

import java.net.URL;

public class MoviesSyncTask {


    /**
     * Performs the network request for updated movies, parses the JSON from that request, and
     * inserts the new movies information into our ContentProvider.
     *
     * @param context Used to access utility methods and the ContentResolver
     */
    synchronized public static void syncMovies(Context context) {


        try {
            String sortBy = SettingsFragment.getSorBy(context);
            String tmdb_api_key = context.getString(R.string.tmdb_api_key);
            URL moviesRequestUrl = MoviesNetworkUtils.buildUrl(tmdb_api_key, sortBy);

            String jsonMoviesResponse = MoviesNetworkUtils
                    .getResponseFromHttpUrl(moviesRequestUrl);

            ContentValues[] moviesValues = MoviesJsonUtils
                    .getMoviesContentValuesFromJson(context, jsonMoviesResponse, sortBy);

            if (moviesValues != null && moviesValues.length != 0) {
                ContentResolver contentResolver = context.getContentResolver();
                contentResolver.bulkInsert(DBContract.MovieEntry.CONTENT_URI, moviesValues);
            }

            /* If the code reaches this point, we have successfully performed our sync */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}