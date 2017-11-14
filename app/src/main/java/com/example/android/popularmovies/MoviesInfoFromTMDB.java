package com.example.android.popularmovies;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class MoviesInfoFromTMDB extends AsyncTaskLoader<ArrayList<MovieDetails>> {

    private Context activityContext;

    MoviesInfoFromTMDB(Context context ){
        super(context);
        activityContext = context;
    }

    @Override
    public ArrayList<MovieDetails> loadInBackground() {

        if(NetworkUtils.isNetworkConnected(activityContext)) {

            //build the URI
            Log.i("denis loadInBackground", "  ");
            URL tmdbMoviesRequestUrl = NetworkUtils.buildUrlMovieList(activityContext.getString(R.string.tmdb_api_key));
            String jsonTMDBMovieList = "";

            try {
                //download the JSON in this separated thread
                jsonTMDBMovieList = NetworkUtils
                        .getResponseFromHttpUrl(tmdbMoviesRequestUrl);

                if(jsonTMDBMovieList != null){
                    //Parse the JSON to get the movie list
                    Log.i("denis getMoviesInfo", "jsonTMDBMovieList length: " + jsonTMDBMovieList.length());
                    return returnMoviesArrayList(jsonTMDBMovieList);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return null;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    private ArrayList<MovieDetails> returnMoviesArrayList(String jsonToParse){
        JSONObject jsonObject;
        JSONArray jsonArray;
        ArrayList<MovieDetails> movieDetailsList = new ArrayList<>();

        try {
            jsonObject = new JSONObject(jsonToParse);
            jsonArray = jsonObject.getJSONArray("results");

            Log.i("denis Jsonarray", "posicao 0 do json: "+jsonArray.get(0));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        if (jsonArray != null) {
            for(int index=0; index<jsonArray.length(); index++){
                try {
                    jsonObject = jsonArray.getJSONObject(index);
                    //TODO - aqui vou baixar os cdigos dos trailers e os comentarios

                    movieDetailsList.add(new MovieDetails(jsonObject.getInt(activityContext.getString(R.string.json_tmdb_vote_count)),
                            jsonObject.getInt(activityContext.getString(R.string.json_tmdb_id)),
                            jsonObject.getDouble(activityContext.getString(R.string.json_tmdb_voteavarage)),
                            jsonObject.getString(activityContext.getString(R.string.json_tmdb_title)),
                            jsonObject.getString(activityContext.getString(R.string.json_tmdb_posterpath)).replaceFirst("/",""),
                            jsonObject.getString(activityContext.getString(R.string.json_tmdb_overview)),
                            jsonObject.getString(activityContext.getString(R.string.json_tmdb_release_date))));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            //NetworkUtils.buildAndDownloadReviewsList(activityContext.getString(R.string.tmdb_api_key), 440021);

            return movieDetailsList;

        } else {
            return null;
        }

    }



}

