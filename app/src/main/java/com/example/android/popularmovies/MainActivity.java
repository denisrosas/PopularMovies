package com.example.android.popularmovies;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<MovieDetails>> {

    public static int sortType = 0;
    public static final int SORT_BY_POPULARITY = 0;
    public static final int SORT_BY_TOP_RATED = 1;
    public static final int SORT_BY_FAVORITES = 2;
    public static final String BUNDLE_SORTTYPE = "bundle_sortType";
    public static int LAYOUT_NUM_COLUMS;

    private static final int LOADER_MOVIES_FROM_TMDB = 31;
    private static final int LOADER_FAV_MOVIES_FROM_DATABASE = 27;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this app was designed to portrait mode only
        setNumColumsBasedOnDisplaySize();

        //loader Task will download the Movie list from TMDB page
        startLoaderTask(LOADER_MOVIES_FROM_TMDB);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.change_order_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.i("denis Mainactivity", "onOptionsItemSelected - : item_id: "+item.getItemId());

        if((item.getItemId() == R.id.sort_by_popularity)||
                (item.getItemId()==R.id.sort_by_top_rated)||
                (item.getItemId()==R.id.my_favorite_movies)) {

            if(((item.getItemId() == R.id.sort_by_popularity)&&(sortType == SORT_BY_POPULARITY))
                    ||((item.getItemId() == R.id.sort_by_top_rated)&&(sortType == SORT_BY_TOP_RATED))
                    ||((item.getItemId() == R.id.my_favorite_movies)&&(sortType == SORT_BY_FAVORITES))) {

                //Nothing to do... sort order is already correct
                return super.onOptionsItemSelected(item);
            }

            //clear all vies in recyclerview
            RecyclerView mRecycleView = (RecyclerView) findViewById(R.id.rv_movie_posters);
            mRecycleView.removeAllViews();

            //set sortType global variable
            if(item.getItemId() == R.id.sort_by_popularity)  {sortType = SORT_BY_POPULARITY;}
            else if (item.getItemId() == R.id.sort_by_top_rated) {sortType = SORT_BY_TOP_RATED;}
            else if (item.getItemId() == R.id.my_favorite_movies) {sortType = SORT_BY_FAVORITES;}

            //start Loader Task to do load the movie details
            LoaderManager loaderManager = getSupportLoaderManager();

            if(sortType==SORT_BY_FAVORITES){
                startLoaderTask(LOADER_FAV_MOVIES_FROM_DATABASE);
            } else {
                startLoaderTask(LOADER_MOVIES_FROM_TMDB);
            }
            (findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }

    private void startLoaderTask(int loaderId){

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<MovieDetails>> loaderTaskTrailers = loaderManager.getLoader(loaderId);

        if(loaderTaskTrailers == null) {
            loaderManager.initLoader(loaderId, null, this);
        } else {
            loaderManager.restartLoader(loaderId, null, this);
        }
    }

    private void setNumColumsBasedOnDisplaySize(){

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        if(size.x < 400) {
            LAYOUT_NUM_COLUMS = 2;
            MoviePosterAdapter.TMDB_LIST_IMAGE_SIZE = "w185";
            MoviePosterAdapter.TMDB_DETAIL_IMAGE_SIZE = "w342";
            //options are: w92, w154, w185, w342, w500, w780, or original
        } else{
            LAYOUT_NUM_COLUMS = 3;
            MoviePosterAdapter.TMDB_LIST_IMAGE_SIZE = "w342";
            MoviePosterAdapter.TMDB_DETAIL_IMAGE_SIZE = "w500";
        }
    }

    @Override
    public Loader<ArrayList<MovieDetails>> onCreateLoader(int id, final Bundle args) {

        //Extraimos os dados do Bundle
        if ((id==LOADER_MOVIES_FROM_TMDB)||(id==LOADER_FAV_MOVIES_FROM_DATABASE)) {
//            String queryType = args.getString("QueryType");
            return new MoviesInfoFromTMDB(this);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MovieDetails>> loader, ArrayList<MovieDetails> movieDetailsArrayList) {

        if(movieDetailsArrayList == null){
            Toast.makeText(this,getString(R.string.check_internet), Toast.LENGTH_LONG).show();
        } else {

            //make progress bar invisible again
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);

            //get the RecyclerView resource and bind to a GridLayoutManager
            RecyclerView mRecycleView = (RecyclerView) findViewById(R.id.rv_movie_posters);
            GridLayoutManager layoutManager = new GridLayoutManager(this, MainActivity.LAYOUT_NUM_COLUMS);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecycleView.setLayoutManager(layoutManager);
            mRecycleView.setHasFixedSize(true);
            mRecycleView.setAdapter(new MoviePosterAdapter(movieDetailsArrayList, this));
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieDetails>> loader) {
        //do nothing
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_SORTTYPE, sortType);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        sortType = savedInstanceState.getInt(BUNDLE_SORTTYPE);
    }

}

