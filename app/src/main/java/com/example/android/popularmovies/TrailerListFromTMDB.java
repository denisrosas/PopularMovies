package com.example.android.popularmovies;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

class TrailerListFromTMDB extends AsyncTaskLoader<ArrayList<String>>{

    private int movieId = 0;
    private String apikey = "";
    private Context context;

    TrailerListFromTMDB(Context context, int movieId, String apikey) {
        super(context);
        this.movieId = movieId;
        this.apikey = apikey;
        this.context = context;
    }

    @Override
    public ArrayList<String> loadInBackground() {
        return NetworkUtils.buildAndDownloadTrailerList(apikey, movieId);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
