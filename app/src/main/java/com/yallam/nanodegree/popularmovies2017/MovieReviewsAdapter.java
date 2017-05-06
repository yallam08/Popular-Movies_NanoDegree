package com.yallam.nanodegree.popularmovies2017;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yallam.nanodegree.popularmovies2017.data.MovieReview;


class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewsAdapterViewHolder> {

    private Context mContext;

    private MovieReview[] reviews;


    MovieReviewsAdapter(Context context, MovieReview[] reviews) {
        mContext = context;
        this.reviews = reviews;
    }

    class MovieReviewsAdapterViewHolder extends RecyclerView.ViewHolder {
        final TextView mAuthorName;
        final TextView mReviewContent;

        MovieReviewsAdapterViewHolder(View view) {
            super(view);
            mAuthorName = (TextView) view.findViewById(R.id.tv_author_value);
            mReviewContent = (TextView) view.findViewById(R.id.tv_review_content);
        }
    }

    @Override
    public MovieReviewsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.reviews_list_item, viewGroup, false);
        return new MovieReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewsAdapterViewHolder videosAdapterViewHolder, int position) {
        videosAdapterViewHolder.mAuthorName.setText(reviews[position].getAuthor());
        videosAdapterViewHolder.mReviewContent.setText(reviews[position].getContent());
    }

    @Override
    public int getItemCount() {
        if (null == reviews) return 0;
        return reviews.length;
    }

    public void setReviews(MovieReview[] reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }
}