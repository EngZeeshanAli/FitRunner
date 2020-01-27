package com.example.fitrunner.MusicPlayer;

import android.graphics.Bitmap;

import com.google.gson.Gson;

import java.io.Serializable;

public class Music implements Serializable {
    private String title;
    private String uri;


    public Music() {
    }

    public Music(String title, String uri) {
        this.title = title;
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public String getUri() {
        return uri;
    }

    public String getJsonObject(Music m) {
        Gson gson = new Gson();
        String json = gson.toJson(m);
        return json;
    }

    public Music getMusicObject(String json) {
        Gson gson = new Gson();
        Music m = gson.fromJson(json, Music.class);
        return m;
    }
}
