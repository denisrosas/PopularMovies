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

    public static Context context;

    @Override
    protected ArrayList<MovieDetails> doInBackground(Context... contexts) {

        if(contexts.length==0) {
            return null;
        }

        context = contexts[0];

        if(isNetworkConnected() == true) {

            URL tmdbMoviesRequestUrl = NetworkUtils.buildUrl(context.getString(R.string.tmdb_api_key));

            try {
                String jsonTMDBResponse = NetworkUtils
                        .getResponseFromHttpUrl(tmdbMoviesRequestUrl);

                if(jsonTMDBResponse.isEmpty()==false){

                    Log.i("denis getMoviesInfo", "jsonTMDBResponse length: " + jsonTMDBResponse.length());
                    return returnMoviesArrayList(jsonTMDBResponse);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<MovieDetails> movieDetailsArrayList) {
        super.onPostExecute(movieDetailsArrayList);

        ProgressBar progressBar = (ProgressBar) ((Activity) context).findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        if(movieDetailsArrayList == null){
            Toast.makeText(context, context.getString(R.string.check_internet), Toast.LENGTH_LONG).show();
        } else {

            RecyclerView mRecycleView = (RecyclerView) ((Activity) context).findViewById(R.id.rv_movie_posters);

            GridLayoutManager layoutManager = new GridLayoutManager(context, MainActivity.LAYOUT_NUM_COLUMS);

            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            mRecycleView.setLayoutManager(layoutManager);

            mRecycleView.setAdapter(new MoviePosterAdapter(movieDetailsArrayList));
        }

    }

    public ArrayList<MovieDetails> returnMoviesArrayList(String jsonToParse){
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

                    movieDetailsList.add(new MovieDetails(jsonObject.getInt(context.getString(R.string.json_tmdb_vote_count)),
                            jsonObject.getInt(context.getString(R.string.json_tmdb_id)),
                            jsonObject.getDouble(context.getString(R.string.json_tmdb_voteavarage)),
                            jsonObject.getString(context.getString(R.string.json_tmdb_title)),
                            jsonObject.getString(context.getString(R.string.json_tmdb_posterpath)).replaceFirst("/",""),
                            jsonObject.getString(context.getString(R.string.json_tmdb_overview)),
                            jsonObject.getString(context.getString(R.string.json_tmdb_release_date))));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            return movieDetailsList;

        } else {
            return null;
        }

    }

    private static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}

