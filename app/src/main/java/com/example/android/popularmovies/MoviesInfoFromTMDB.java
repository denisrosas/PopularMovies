package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.popularmovies.data.FavoriteMoviesContract;

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

        if(MainActivity.sortType == MainActivity.SORT_BY_FAVORITES){

            return getFavoriteMoviesFromContProv();

        } else{
            //build the URI
            URL tmdbMoviesRequestUrl = NetworkUtils.buildUrlMovieList(activityContext.getString(R.string.tmdb_api_key));
            String jsonTMDBMovieList = "";
            Log.i("MoviesInfoFromTMDB", "loadInBackground() - tmdbMoviesRequestUrl: " + tmdbMoviesRequestUrl.toString());

            try {
                //download the JSON in this separated thread
                jsonTMDBMovieList = NetworkUtils
                        .getResponseFromHttpUrl(tmdbMoviesRequestUrl);

                if (jsonTMDBMovieList != null) {
                    //Parse the JSON to get the movie list
                    Log.i("MoviesInfoFromTMDB", "loadInBackground() - Downloaded JSON from TMDB with lenght: " + jsonTMDBMovieList.length());
                    return returnMoviesArrayList(jsonTMDBMovieList);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return null;
    }

    private ArrayList<MovieDetails> getFavoriteMoviesFromContProv() {

        //build the uri to get all movies marked as favorite
        Uri uri = FavoriteMoviesContract.FavoriteMoviesEntry.CONTENT_URI;
        uri = uri.buildUpon().build();

        ArrayList<MovieDetails> favoriteMovies = new ArrayList<>();

        //download the favorite movies from the ContentPrivider
        Cursor cursor = activityContext.getContentResolver().query(uri, null, null, null, null);

        Log.i("MoviesInfoFromTMDB", "getFavoriteMoviesFromContProv() - "+cursor.getCount()+" favorite movies found in database");

        if(cursor.getCount()==0) {
            return favoriteMovies;
        }

        cursor.moveToFirst();

        do {
            MovieDetails movieDetails = new MovieDetails(
                    cursor.getInt(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_VOTE_COUNT)),
                    cursor.getInt(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID)),
                    cursor.getDouble(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_VOTE_AVERAGE)),
                    cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TITLE)),
                    cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_POSTER_PATH)),
                    cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_OVERVIEW)),
                    cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_RELEASE_DATE))
            );
            favoriteMovies.add(movieDetails);
        } while (cursor.moveToNext());

        cursor.close();
        return favoriteMovies;

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
        int maxTitleSize=0, maxOverviewSize=0;

        try {
            jsonObject = new JSONObject(jsonToParse);
            jsonArray = jsonObject.getJSONArray("results");
            Log.i("MoviesInfoFromTMDB", "returnMoviesArrayList() - Successfully found JSON Array \"results\"" +
                    " in JSON Object of size: "+jsonArray.length());

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        if (jsonArray != null) {
            for(int index=0; index<jsonArray.length(); index++){
                try {
                    jsonObject = jsonArray.getJSONObject(index);

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

            return movieDetailsList;

        } else {
            return null;
        }

    }



}

