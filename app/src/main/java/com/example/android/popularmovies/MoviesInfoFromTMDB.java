package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class MoviesInfoFromTMDB extends AsyncTask<Context, Void, ArrayList<MovieDetails>> {

    private Context activityContext;
    public static final int REVIEW_AUTHOR_INDEX = 0;
    public static final int REVIEW_CONTENT_INDEX = 1;

    MoviesInfoFromTMDB(Context context){
        activityContext = context;
    }

    @Override
    protected ArrayList<MovieDetails> doInBackground(Context... contexts) {

        //ArrayList<MovieDetails> moviesArrayList = new ArrayList<MovieDetails>();

        if(contexts.length==0) {
            return null;
        }

        activityContext = contexts[0];

        if(isNetworkConnected() == true) {

            //build the URI
            URL tmdbMoviesRequestUrl = NetworkUtils.buildUrlMovieList(activityContext.getString(R.string.tmdb_api_key));

            try {

                //download the JSON in this separated thread
                String jsonTMDBMovieList = NetworkUtils
                        .getResponseFromHttpUrl(tmdbMoviesRequestUrl);

                if(jsonTMDBMovieList.isEmpty()==false){
                    //Parse the JSON to get the movie list
                    Log.i("denis getMoviesInfo", "jsonTMDBMovieList length: " + jsonTMDBMovieList.length());
                    return returnMoviesArrayList(jsonTMDBMovieList);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            //return moviesArrayList;
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<MovieDetails> movieDetailsArrayList) {
        super.onPostExecute(movieDetailsArrayList);

        if(movieDetailsArrayList == null){
            Toast.makeText(activityContext, activityContext.getString(R.string.check_internet), Toast.LENGTH_LONG).show();
        } else {

            //make progress bar invisible again
            ProgressBar progressBar = (ProgressBar) ((Activity) activityContext).findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);

            //get the RecyclerView resource and bind to a GridLayoutManager
            RecyclerView mRecycleView = (RecyclerView) ((Activity) activityContext).findViewById(R.id.rv_movie_posters);
            GridLayoutManager layoutManager = new GridLayoutManager(activityContext, MainActivity.LAYOUT_NUM_COLUMS);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecycleView.setLayoutManager(layoutManager);
            mRecycleView.setHasFixedSize(true);
            mRecycleView.setAdapter(new MoviePosterAdapter(movieDetailsArrayList, activityContext));
        }



    }

    private ArrayList<MovieDetails> returnMoviesArrayList(String jsonToParse){
        JSONObject jsonObject;
        JSONArray jsonArray;
        ArrayList<MovieDetails> movieDetailsList = new ArrayList<>();

        try {
            jsonObject = new JSONObject(jsonToParse);
            jsonArray = jsonObject.getJSONArray("results");

            //Log.i("denis Jsonarray", "posicao 0 do json: "+jsonArray.get(0));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        if (jsonArray != null) {
            for(int index=0; index<jsonArray.length(); index++){
                try {
                    jsonObject = jsonArray.getJSONObject(index);
                    //TODO - aqui vou baixar os cdigos dos trailers e os comentarios
                    String[] trailers_youtube = {""};

                    movieDetailsList.add(new MovieDetails(jsonObject.getInt(activityContext.getString(R.string.json_tmdb_vote_count)),
                            jsonObject.getInt(activityContext.getString(R.string.json_tmdb_id)),
                            jsonObject.getDouble(activityContext.getString(R.string.json_tmdb_voteavarage)),
                            jsonObject.getString(activityContext.getString(R.string.json_tmdb_title)),
                            jsonObject.getString(activityContext.getString(R.string.json_tmdb_posterpath)).replaceFirst("/",""),
                            jsonObject.getString(activityContext.getString(R.string.json_tmdb_overview)),
                            jsonObject.getString(activityContext.getString(R.string.json_tmdb_release_date)), trailers_youtube));

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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) activityContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}

