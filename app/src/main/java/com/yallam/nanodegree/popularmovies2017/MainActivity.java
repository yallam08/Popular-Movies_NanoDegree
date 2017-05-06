package com.yallam.nanodegree.popularmovies2017;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yallam.nanodegree.popularmovies2017.data.DBContract;
import com.yallam.nanodegree.popularmovies2017.data.MovieModel;
import com.yallam.nanodegree.popularmovies2017.sync.MoviesSyncUtils;


public class MainActivity extends AppCompatActivity
        implements MoviesAdapter.MoviesAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int ID_MOVIES_LOADER = 13;

    public final static String MOVIE_INTENT_KEY = "THE_MOVIE";

    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    private TextView mTVErrorMessage;
    private ProgressBar mLoadingIndicator;

    private String currentSortingCriteria;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        mTVErrorMessage = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_movies_grid);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies_grid);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        }
        mRecyclerView.setHasFixedSize(true);
        mMoviesAdapter = new MoviesAdapter(MainActivity.this, MainActivity.this);
        mRecyclerView.setAdapter(mMoviesAdapter);

        showLoading();

        currentSortingCriteria = SettingsFragment.getSorBy(this);
        Bundle sortingBundle = new Bundle();
        sortingBundle.putString(getString(R.string.pref_sort_by_key), currentSortingCriteria);
        getSupportLoaderManager().initLoader(ID_MOVIES_LOADER, sortingBundle, this);

        MoviesSyncUtils.initialize(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_activity_settings) {
            Intent openSettingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(openSettingsActivity);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handling the click on an item of the movies grid
     *
     * @param movie the clicked movie
     */
    @Override
    public void onClick(MovieModel movie) {
        Intent openMovieDetailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
        openMovieDetailsIntent.putExtra(MOVIE_INTENT_KEY, movie);
        startActivity(openMovieDetailsIntent);
    }

    private void showMoviesGrid() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mTVErrorMessage.setVisibility(View.INVISIBLE);
    }

    private void showLoading() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mTVErrorMessage.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage(String msg) {
        mTVErrorMessage.setText(msg);
        mTVErrorMessage.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        if (args == null) {
            return null;
        }
        String sortBy = args.getString(
                getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_value_popular)
        );

        switch (loaderId) {
            case ID_MOVIES_LOADER:
                Uri moviesQueryUri = DBContract.MovieEntry.CONTENT_URI;

                String selectionStatement;
                if (sortBy.equals(getString(R.string.pref_sort_by_value_favorites))) {
                    selectionStatement = DBContract.MovieEntry.COLUMN_IS_FAV + " = 1";
                } else {
                    selectionStatement = DBContract.MovieEntry.COLUMN_SORT_BY + " = '" + sortBy + "'";
                }

                return new CursorLoader(this,
                        moviesQueryUri,
                        null,
                        selectionStatement,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviesAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);

        if (data.getCount() != 0) {
            showMoviesGrid();
        } else if (currentSortingCriteria.equals(getString(R.string.pref_sort_by_value_favorites))) {
            showErrorMessage(getString(R.string.no_favorites));
        }

        this.setTitle(getString(R.string.app_name) + "(" + getCurrentSortingFriendlyText(currentSortingCriteria) + ")");
    }

    private String getCurrentSortingFriendlyText(String sortingCriteriaValue) {
        if (sortingCriteriaValue.equals(getString(R.string.pref_sort_by_value_popular))) {
            return getString(R.string.pref_sort_by_label_popular);
        } else if (sortingCriteriaValue.equals(getString(R.string.pref_sort_by_value_top_rated))) {
            return getString(R.string.pref_sort_by_label_top_rated);
        } else {
            return getString(R.string.pref_sort_by_label_favorites);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sort_by_key))) {
            String newSortingCriteria = sharedPreferences.getString(key, getString(R.string.pref_sort_by_value_popular));
            if (!currentSortingCriteria.equals(newSortingCriteria)) {
                Bundle sortingBundle = new Bundle();
                sortingBundle.putString(getString(R.string.pref_sort_by_key), newSortingCriteria);

                showLoading();
                getSupportLoaderManager().restartLoader(ID_MOVIES_LOADER, sortingBundle, this);

                currentSortingCriteria = newSortingCriteria;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
}
