package com.yallam.nanodegree.popularmovies2017.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.yallam.nanodegree.popularmovies2017.SettingsFragment;
import com.yallam.nanodegree.popularmovies2017.data.DBContract;

public class MoviesSyncUtils {

    private static boolean sInitialized;

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     *
     * @param context Context that will be passed to other methods and used to access the
     *                ContentResolver
     */
    synchronized public static void initialize(@NonNull final Context context) {

        if (sInitialized) return;

        sInitialized = true;

        new AsyncTask<Void, Void, Void>() {
            @Override
            public Void doInBackground( Void... voids ) {

                Uri moviesQueryUri = DBContract.MovieEntry.CONTENT_URI;

                String sortBy = SettingsFragment.getSorBy(context);
                String[] projectionColumns = {DBContract.MovieEntry._ID};
                String selectionStatement = DBContract.MovieEntry.COLUMN_SORT_BY + " = '" + sortBy + "'";

                Cursor cursor = context.getContentResolver().query(
                        moviesQueryUri,
                        projectionColumns,
                        selectionStatement,
                        null,
                        null);

                if (null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                cursor.close();
                return null;
            }
        }.execute();
    }

    /**
     * Helper method to perform a sync immediately using an IntentService for asynchronous
     * execution.
     *
     * @param context The Context used to start the IntentService for the sync.
     */
    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, MoviesSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}