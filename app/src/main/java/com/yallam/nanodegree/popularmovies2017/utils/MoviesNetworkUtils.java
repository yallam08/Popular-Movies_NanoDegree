package com.yallam.nanodegree.popularmovies2017.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MoviesNetworkUtils {

    private static final String TAG = MoviesNetworkUtils.class.getSimpleName();

    public static final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";
    public final static String GRID_MOVIE_POSTER_URL_BASE = "http://image.tmdb.org/t/p/w185/";
    public final static String DETAILS_MOVIE_POSTER_URL_BASE = "http://image.tmdb.org/t/p/w342/";

    public final static String APIKEY_PARAM = "api_key";


    /**
     *
     * @param apiKeyQueryValue API key from TMDB
     * @param partAfterBaseUrl Without beginning slash
     * @return The built URL
     */
    public static URL buildUrl(String apiKeyQueryValue, String partAfterBaseUrl) {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL + partAfterBaseUrl).buildUpon()
                .appendQueryParameter(APIKEY_PARAM, apiKeyQueryValue)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
