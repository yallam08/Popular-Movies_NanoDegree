package com.yallam.nanodegree.popularmovies2017;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yallam.nanodegree.popularmovies2017.data.DBContract;
import com.yallam.nanodegree.popularmovies2017.data.MovieModel;
import com.yallam.nanodegree.popularmovies2017.utils.MoviesNetworkUtils;


class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private Context mContext;
    private final MoviesAdapterOnClickHandler mClickHandler;

    private Cursor mCursor;


    interface MoviesAdapterOnClickHandler {
        void onClick(MovieModel movie);
    }


    MoviesAdapter(Context context, MoviesAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        ImageView mMoviePosterView;
        TextView mMovieTitle;

        MoviesAdapterViewHolder(View view) {
            super(view);
            mMoviePosterView = (ImageView) view.findViewById(R.id.img_movie_poster);
            mMovieTitle = (TextView) view.findViewById(R.id.tv_movie_title);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            MovieModel movie = new MovieModel(
                    mCursor.getInt(DBContract.MovieEntry.COL_MOVIE_ID),
                    mCursor.getString(DBContract.MovieEntry.COL_POSTER_PATH),
                    mCursor.getString(DBContract.MovieEntry.COL_TITLE),
                    mCursor.getString(DBContract.MovieEntry.COL_OVERVIEW),
                    mCursor.getDouble(DBContract.MovieEntry.COL_VOTE_AVERAGE),
                    mCursor.getString(DBContract.MovieEntry.COL_RELEASE_DATE),
                    mCursor.getInt(DBContract.MovieEntry.COL_IS_FAV)
            );
            mClickHandler.onClick(movie);
        }
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.movies_grid_item, viewGroup, false);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MoviesAdapterViewHolder moviesAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);

        String movieTitle = mCursor.getString(DBContract.MovieEntry.COL_TITLE);
        if (movieTitle.length() > 12) {
            movieTitle = movieTitle.substring(0, 10) + "..";
        }
        moviesAdapterViewHolder.mMovieTitle.setText(movieTitle);

        final Picasso picasso = Picasso.with(mContext);
        final String moviePosterUrl = MoviesNetworkUtils.GRID_MOVIE_POSTER_URL_BASE + mCursor.getString(DBContract.MovieEntry.COL_POSTER_PATH);
        picasso.load(moviePosterUrl)
                .into(moviesAdapterViewHolder.mMoviePosterView);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }
}