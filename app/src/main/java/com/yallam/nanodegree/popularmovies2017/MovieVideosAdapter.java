package com.yallam.nanodegree.popularmovies2017;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yallam.nanodegree.popularmovies2017.data.MovieVideo;


class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideosAdapter.MovieVideosAdapterViewHolder> {

    private Context mContext;
    private final MovieVideosOnClickHandler mClickHandler;

    private MovieVideo[] videos;


    interface MovieVideosOnClickHandler {
        void onClick(String youtubeLinkKey);
    }


    MovieVideosAdapter(Context context, MovieVideo[] videos, MovieVideosOnClickHandler clickHandler) {
        mContext = context;
        this.videos = videos;
        mClickHandler = clickHandler;
    }

    class MovieVideosAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        final TextView mMovieVideoName;

        MovieVideosAdapterViewHolder(View view) {
            super(view);
            mMovieVideoName = (TextView) view.findViewById(R.id.tv_video_name);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(videos[adapterPosition].getYoutubeLinkKey());
        }
    }

    @Override
    public MovieVideosAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.videos_list_item, viewGroup, false);
        return new MovieVideosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieVideosAdapterViewHolder videosAdapterViewHolder, int position) {
        videosAdapterViewHolder.mMovieVideoName.setText(videos[position].getName());
    }

    @Override
    public int getItemCount() {
        if (null == videos) return 0;
        return videos.length;
    }

    public void setVideos(MovieVideo[] videos) {
        this.videos = videos;
        notifyDataSetChanged();
    }
}