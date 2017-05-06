package com.yallam.nanodegree.popularmovies2017.data;


public class MovieVideo {
    private String youtubeLinkKey, name;

    public MovieVideo(String youtubeLinkKey, String name) {
        this.youtubeLinkKey = youtubeLinkKey;
        this.name = name;
    }

    public String getYoutubeLinkKey() {
        return youtubeLinkKey;
    }

    public void setYoutubeLinkKey(String youtubeLinkKey) {
        this.youtubeLinkKey = youtubeLinkKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
