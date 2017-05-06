package com.yallam.nanodegree.popularmovies2017.data;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieModel implements Parcelable {

    private int id;
    private String posterPath;
    private String originalTitle;
    private String overview;
    private double voteAverage;
    private String releaseDate;
    private int isFav;

    public MovieModel() {
    }

    public MovieModel(int id, String posterPath, String originalTitle, String overview, double voteAverage,
                      String releaseDate, int isFav) {
        this.id = id;
        this.posterPath = posterPath;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.isFav = isFav;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getIsFav() {
        return isFav;
    }

    public void setIsFav(int isFav) {
        this.isFav = isFav;
    }

    public boolean isFav() {
        return isFav == 1;
    }

    @Override
    public String toString() {
        return "Movie [id=" + id + ", posterPath=" + posterPath + ", originalTitle=" + originalTitle
                + ", overview=" + overview + ", voteAverage=" + voteAverage
                + ", releaseDate=" + releaseDate + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(posterPath);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeDouble(voteAverage);
        dest.writeString(releaseDate);
        dest.writeInt(isFav);
    }

    public static final Parcelable.Creator<MovieModel> CREATOR
            = new Parcelable.Creator<MovieModel>() {

        public MovieModel createFromParcel(Parcel in) {
            return new MovieModel(in);
        }

        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };

    private MovieModel(Parcel in) {
        this.id = in.readInt();
        this.posterPath = in.readString();
        this.originalTitle = in.readString();
        this.overview = in.readString();
        this.voteAverage = in.readDouble();
        this.releaseDate = in.readString();
        this.isFav = in.readInt();
    }
}