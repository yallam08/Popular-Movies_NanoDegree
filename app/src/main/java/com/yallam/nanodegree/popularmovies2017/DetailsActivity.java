package com.yallam.nanodegree.popularmovies2017;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yallam.nanodegree.popularmovies2017.data.DBContract;
import com.yallam.nanodegree.popularmovies2017.data.MovieModel;
import com.yallam.nanodegree.popularmovies2017.data.MovieReview;
import com.yallam.nanodegree.popularmovies2017.data.MovieVideo;
import com.yallam.nanodegree.popularmovies2017.utils.MoviesJsonUtils;
import com.yallam.nanodegree.popularmovies2017.utils.MoviesNetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class DetailsActivity extends AppCompatActivity
        implements MovieVideosAdapter.MovieVideosOnClickHandler,
        LoaderManager.LoaderCallbacks<MovieVideo[]> {

    private static final int MOVIE_VIDEOS_LOADER = 15;
    private static final int MOVIE_REVIEWS_LOADER = 17;

    LoaderManager.LoaderCallbacks<MovieReview[]> mReviewsLoader;

    MovieModel mMovie;

    ImageView mMoviePoster;
    TextView mMovieTitle, mMovieReleaseDate, mMovieVoteAverage, mMovieOverview;
    Button mFavBtn;

    RecyclerView mMovieVideosRecyclerView;
    MovieVideosAdapter mMovieVideosRecyclerViewAdapter;
    ProgressBar mLoadingVideosProgressBar;

    RecyclerView mMovieReviewsRecyclerView;
    MovieReviewsAdapter mMovieReviewsRecyclerViewAdapter;
    ProgressBar mLoadingReviewsProgressBar;

    MovieVideo mFirstTrailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (getIntent().hasExtra(MainActivity.MOVIE_INTENT_KEY)) {
            mMovie = getIntent().getParcelableExtra(MainActivity.MOVIE_INTENT_KEY);
        } else {
            return;
        }

        mMoviePoster = (ImageView) findViewById(R.id.img_movie_poster_details);
        mMovieTitle = (TextView) findViewById(R.id.tv_movie_title_details);
        mMovieReleaseDate = (TextView) findViewById(R.id.tv_movie_release_date_details);
        mMovieVoteAverage = (TextView) findViewById(R.id.tv_movie_vote_average_details);
        mMovieOverview = (TextView) findViewById(R.id.tv_movie_overview_details);
        mFavBtn = (Button) findViewById(R.id.btn_fav);

        mMovieTitle.setText(mMovie.getOriginalTitle());
        mMovieReleaseDate.setText(mMovie.getReleaseDate());
        mMovieVoteAverage.setText(String.valueOf(mMovie.getVoteAverage()) + "/10");
        mMovieOverview.setText(mMovie.getOverview());
        if (mMovie.isFav()) {
            setFavBtnLeftDrawable(mFavBtn, android.R.drawable.btn_star_big_on);
        } else {
            setFavBtnLeftDrawable(mFavBtn, android.R.drawable.btn_star_big_off);
        }

        mLoadingVideosProgressBar = (ProgressBar) findViewById(R.id.pb_loading_movie_videos_list);
        mMovieVideosRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_videos);
        LinearLayoutManager videosLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMovieVideosRecyclerView.setLayoutManager(videosLayoutManager);
        mMovieVideosRecyclerViewAdapter = new MovieVideosAdapter(this, null, this);
        mMovieVideosRecyclerView.setAdapter(mMovieVideosRecyclerViewAdapter);


        mLoadingReviewsProgressBar = (ProgressBar) findViewById(R.id.pb_loading_movie_reviews_list);
        mMovieReviewsRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_reviews);
        LinearLayoutManager reviewsLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMovieReviewsRecyclerView.setLayoutManager(reviewsLayoutManager);
        mMovieReviewsRecyclerViewAdapter = new MovieReviewsAdapter(this, null);
        mMovieReviewsRecyclerView.setAdapter(mMovieReviewsRecyclerViewAdapter);

        implementReviewsLoader();

        getSupportLoaderManager().initLoader(MOVIE_VIDEOS_LOADER, null, this);
        getSupportLoaderManager().initLoader(MOVIE_REVIEWS_LOADER, null, mReviewsLoader);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMovie != null) {
            Picasso picasso = Picasso.with(DetailsActivity.this);
            picasso.load(MoviesNetworkUtils.DETAILS_MOVIE_POSTER_URL_BASE + mMovie.getPosterPath())
                    .placeholder(R.drawable.movie_poster_placeholder)
                    .error(R.drawable.movie_poster_placeholder)
                    .into(mMoviePoster);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_activity_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            if (mFirstTrailer == null) {
                Toast.makeText(this, "No trailer to share", Toast.LENGTH_SHORT).show();
                return true;
            }

            ShareCompat.IntentBuilder
                    .from(this)
                    .setChooserTitle("Share " + mMovie.getOriginalTitle() + " Trailer")
                    .setType("text/plain")
                    .setText(
                            mMovie.getOriginalTitle() + " " + mFirstTrailer.getName() +
                                    "\nhttp://www.youtube.com/watch?v=" + mFirstTrailer.getYoutubeLinkKey() +
                                    "\n#PopularMoviesApp"
                    )
                    .startChooser();
        }

        return super.onOptionsItemSelected(item);
    }

    public void favBtnClick(View view) {
        Button favBtn = (Button) view;
        String whereString = DBContract.MovieEntry.COLUMN_MOVIE_ID + " = " + mMovie.getId();

        if (mMovie.isFav()) {
            ContentValues values = new ContentValues();
            values.put(DBContract.MovieEntry.COLUMN_IS_FAV, 0);

            int updatedRows = getContentResolver().update(DBContract.MovieEntry.CONTENT_URI, values, whereString, null);

            if (updatedRows > 0) {
                setFavBtnLeftDrawable(favBtn, android.R.drawable.btn_star_big_off);
                mMovie.setIsFav(0);
                Toast.makeText(this, R.string.movie_unfavorited, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.error_unfavoriting, Toast.LENGTH_LONG).show();
            }
        } else {
            ContentValues values = new ContentValues();
            values.put(DBContract.MovieEntry.COLUMN_IS_FAV, 1);

            int updatedRows = getContentResolver().update(DBContract.MovieEntry.CONTENT_URI, values, whereString, null);

            if (updatedRows > 0) {
                setFavBtnLeftDrawable(favBtn, android.R.drawable.btn_star_big_on);
                mMovie.setIsFav(0);
                Toast.makeText(this, R.string.movie_favorited, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.error_favoriting, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setFavBtnLeftDrawable(Button btn, int drawable) {
        btn.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(this, drawable)
                , null, null, null);
    }

    @Override
    public void onClick(String youtubeLinkKey) {
        Uri videoUrl = Uri.parse("http://www.youtube.com/watch?v=" + youtubeLinkKey);

        Intent intent = new Intent(Intent.ACTION_VIEW, videoUrl);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    @Override
    public Loader<MovieVideo[]> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<MovieVideo[]>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public MovieVideo[] loadInBackground() {
                URL movieVideosUrl = MoviesNetworkUtils.buildUrl(getString(R.string.tmdb_api_key), mMovie.getId() + "/videos");
                try {
                    String movieVideosJsonStr = MoviesNetworkUtils.getResponseFromHttpUrl(movieVideosUrl);
                    return MoviesJsonUtils.getMovieVideosFromJson(movieVideosJsonStr);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;

                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieVideo[]> loader, MovieVideo[] videos) {
        if (videos != null && videos.length > 0) {
            mMovieVideosRecyclerViewAdapter.setVideos(videos);
            mLoadingVideosProgressBar.setVisibility(View.GONE);
            mFirstTrailer = videos[0];
        } else {
            mLoadingVideosProgressBar.setVisibility(View.GONE);
            findViewById(R.id.tv_no_videos).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<MovieVideo[]> loader) {
    }


    private void implementReviewsLoader() {
        mReviewsLoader = new LoaderManager.LoaderCallbacks<MovieReview[]>() {
            @Override
            public Loader<MovieReview[]> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<MovieReview[]>(DetailsActivity.this) {

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        forceLoad();
                    }

                    @Override
                    public MovieReview[] loadInBackground() {
                        URL movieReviewsUrl = MoviesNetworkUtils.buildUrl(getString(R.string.tmdb_api_key), mMovie.getId() + "/reviews");
                        try {
                            String movieReviewsJsonStr = MoviesNetworkUtils.getResponseFromHttpUrl(movieReviewsUrl);
                            return MoviesJsonUtils.getMovieReviewsFromJson(movieReviewsJsonStr);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return null;

                        }
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<MovieReview[]> loader, MovieReview[] reviews) {
                if (reviews != null && reviews.length > 0) {
                    mMovieReviewsRecyclerViewAdapter.setReviews(reviews);
                    mLoadingReviewsProgressBar.setVisibility(View.GONE);
                } else {
                    mLoadingReviewsProgressBar.setVisibility(View.GONE);
                    findViewById(R.id.tv_no_reviews).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoaderReset(Loader<MovieReview[]> loader) {
            }
        };
    }
}
