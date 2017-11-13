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
    public static int LAYOUT_NUM_COLUMS;

    private static final int LOADER_MOVIES_FROM_TMDB = 31;
    private static final int LOADER_MOVIES_FROM_DATABASE = 27;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this app was designed to portrait mode only
        setNumColumsBasedOnDisplaySize();

//        MoviesInfoFromTMDB downloadTask = new MoviesInfoFromTMDB(this);
//        downloadTask.execute(this);

        Bundle bundle = new Bundle();

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<MovieDetails>> loaderTask = loaderManager.getLoader(LOADER_MOVIES_FROM_TMDB);

        if(loaderTask == null) {
            loaderManager.initLoader(LOADER_MOVIES_FROM_TMDB, bundle, this);
        } else {
            loaderManager.restartLoader(LOADER_MOVIES_FROM_TMDB, bundle, this);
        }
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

        if((item.getItemId() == R.id.sort_by_popularity)||(item.getItemId()==R.id.sort_by_top_rated)){

            if(((item.getItemId() == R.id.sort_by_popularity)&&(sortType == SORT_BY_POPULARITY))
                    ||((item.getItemId() == R.id.sort_by_top_rated)&&(sortType == SORT_BY_TOP_RATED))
                    ||((item.getItemId() == R.id.my_favorite_movies)&&(sortType == SORT_BY_FAVORITES))) {

                //Nothing to do... sort order is already correct
                return super.onOptionsItemSelected(item);
            }

            RecyclerView mRecycleView = (RecyclerView) findViewById(R.id.rv_movie_posters);
            mRecycleView.removeAllViews();

            if(item.getItemId() == R.id.sort_by_popularity){
                sortType = SORT_BY_POPULARITY;
            }else if (item.getItemId() == R.id.sort_by_top_rated){
                sortType = SORT_BY_TOP_RATED;
            }else if (item.getItemId() == R.id.my_favorite_movies){
                sortType = SORT_BY_FAVORITES;
            }

            if((sortType == SORT_BY_POPULARITY)||(sortType==SORT_BY_TOP_RATED)) {
                //start AsynTask to download the movie details
//                MoviesInfoFromTMDB downloadTask = new MoviesInfoFromTMDB(this);
//                downloadTask.execute(this);

                Bundle bundle = new Bundle();

                LoaderManager loaderManager = getSupportLoaderManager();
                Loader<ArrayList<MovieDetails>> loaderTask = loaderManager.getLoader(LOADER_MOVIES_FROM_TMDB);

                if(loaderTask == null) {
                    loaderManager.initLoader(LOADER_MOVIES_FROM_TMDB, bundle, this);
                } else {
                    loaderManager.restartLoader(LOADER_MOVIES_FROM_TMDB, bundle, this);
                }

                (findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
            }
        }

        return super.onOptionsItemSelected(item);
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
        if(id==LOADER_MOVIES_FROM_TMDB) {
            String queryType = args.getString("QueryType");
            return new MoviesInfoFromTMDB(this);
        } else if (id==LOADER_MOVIES_FROM_DATABASE){
            Log.i("denis", "LOADER_MOVIES_FROM_DATABASE");
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

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}

