package com.yallam.nanodegree.popularmovies2017.utils;

import android.content.ContentValues;
import android.content.Context;

import com.yallam.nanodegree.popularmovies2017.data.DBContract;
import com.yallam.nanodegree.popularmovies2017.data.MovieModel;
import com.yallam.nanodegree.popularmovies2017.data.MovieReview;
import com.yallam.nanodegree.popularmovies2017.data.MovieVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class MoviesJsonUtils {

    public static List<MovieModel> getMoviesListFromJson(String moviesJsonStr)
            throws JSONException {

        List<MovieModel> moviesList = new ArrayList<>();

        final String TMDB_RESULTS = "results";
        final String TMDB_MOVIEID = "id";
        final String TMDB_POSTER = "poster_path";
        final String TMDB_TITLE = "original_title";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_RATING = "vote_average";
        final String TMDB_REALEASE_DATE = "release_date";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(TMDB_RESULTS);

        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movieObject = moviesArray.getJSONObject(i);

            String id = movieObject.getString(TMDB_MOVIEID);
            String poster = movieObject.getString(TMDB_POSTER);
            String title = movieObject.getString(TMDB_TITLE);
            String overview = movieObject.getString(TMDB_OVERVIEW);
            double rating = movieObject.getDouble(TMDB_RATING);
            String releaseDate = movieObject.getString(TMDB_REALEASE_DATE);

            MovieModel movie = new MovieModel(
                    Integer.parseInt(id),
                    poster, title, overview,
                    rating, releaseDate, 0
            );

            moviesList.add(movie);
        }

        return moviesList;
    }

    public static ContentValues[] getMoviesContentValuesFromJson(Context context, String moviesJsonStr, String sortBy)
            throws JSONException {

        final String TMDB_RESULTS = "results";
        final String TMDB_MOVIEID = "id";
        final String TMDB_POSTER = "poster_path";
        final String TMDB_TITLE = "original_title";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_RATING = "vote_average";
        final String TMDB_REALEASE_DATE = "release_date";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(TMDB_RESULTS);

        ContentValues[] moviesContentValues = new ContentValues[moviesArray.length()];

        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movieObject = moviesArray.getJSONObject(i);

            String id = movieObject.getString(TMDB_MOVIEID);
            String poster = movieObject.getString(TMDB_POSTER);
            String title = movieObject.getString(TMDB_TITLE);
            String overview = movieObject.getString(TMDB_OVERVIEW);
            double rating = movieObject.getDouble(TMDB_RATING);
            String releaseDate = movieObject.getString(TMDB_REALEASE_DATE);

            ContentValues movieValues = new ContentValues();
            movieValues.put(DBContract.MovieEntry.COLUMN_MOVIE_ID, Integer.parseInt(id));
            movieValues.put(DBContract.MovieEntry.COLUMN_POSTER_PATH, poster);
            movieValues.put(DBContract.MovieEntry.COLUMN_TITLE, title);
            movieValues.put(DBContract.MovieEntry.COLUMN_OVERVIEW, overview);
            movieValues.put(DBContract.MovieEntry.COLUMN_VOTE_AVERAGE, rating);
            movieValues.put(DBContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            movieValues.put(DBContract.MovieEntry.COLUMN_SORT_BY, sortBy);

            moviesContentValues[i] = movieValues;
        }

        return moviesContentValues;
    }

    public static MovieReview[] getMovieReviewsFromJson(String reviewsJsonStr)
            throws JSONException {

        final String TMDB_RESULTS = "results";
        final String TMDB_AUTHOR = "author";
        final String TMDB_REVIEW_CONTENT = "content";

        JSONObject resultJSON = new JSONObject(reviewsJsonStr);
        JSONArray reviewsArray = resultJSON.getJSONArray(TMDB_RESULTS);

        MovieReview[] movieReviews = new MovieReview[reviewsArray.length()];

        for(int i = 0; i < reviewsArray.length(); i++) {
            JSONObject reviewObject = reviewsArray.getJSONObject(i);

            movieReviews[i] = new MovieReview(
                    reviewObject.getString(TMDB_AUTHOR),
                    reviewObject.getString(TMDB_REVIEW_CONTENT)
            );
        }

        return movieReviews;
    }

    public static MovieVideo[] getMovieVideosFromJson(String reviewsJsonStr)
            throws JSONException {

        final String TMDB_RESULTS = "results";
        final String TMDB_KEY = "key";
        final String TMDB_NAME = "name";

        JSONObject resultJSON = new JSONObject(reviewsJsonStr);
        JSONArray videosArray = resultJSON.getJSONArray(TMDB_RESULTS);

        MovieVideo[] movieVideos = new MovieVideo[videosArray.length()];

        for(int i = 0; i < videosArray.length(); i++) {
            JSONObject videoObject = videosArray.getJSONObject(i);

            movieVideos[i] = new MovieVideo(
                    videoObject.getString(TMDB_KEY),
                    videoObject.getString(TMDB_NAME)
            );
        }

        return movieVideos;
    }
}