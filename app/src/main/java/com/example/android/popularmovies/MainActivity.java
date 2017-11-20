package com.example.android.popularmovies;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
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

    RecyclerView mRecycleView;
    Parcelable layoutManagerSavedState = null;

    public static final int SORT_BY_POPULARITY = 0;
    public static final int SORT_BY_TOP_RATED = 1;
    public static final int SORT_BY_FAVORITES = 2;

    public static int sortType = SORT_BY_POPULARITY;

    public static final String BUNDLE_SORTTYPE = "bundle_sortType";
    public static final String BUNDLE_LAYOUT_MANAGER = "bundle_layout_manager";
    public static int LAYOUT_NUM_COLUMS;

    private static final int LOADER_MOVIES_FROM_TMDB = 31;
    private static final int LOADER_FAV_MOVIES_FROM_DATABASE = 27;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //clear all views of the recyclerView
        clearRecyclerView();

        //this app was designed to portrait mode only
        setNumColumnsBasedOnDisplaySize();

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

            //check if user is already in the selected option
            if(((item.getItemId() == R.id.sort_by_popularity)&&(sortType == SORT_BY_POPULARITY))
                    ||((item.getItemId() == R.id.sort_by_top_rated)&&(sortType == SORT_BY_TOP_RATED))
                    ||((item.getItemId() == R.id.my_favorite_movies)&&(sortType == SORT_BY_FAVORITES))) {

                //Nothing to do... sort order is already correct
                return super.onOptionsItemSelected(item);
            }

            //set sortType global variable
            if(item.getItemId() == R.id.sort_by_popularity)  {sortType = SORT_BY_POPULARITY;}
            else if (item.getItemId() == R.id.sort_by_top_rated) {sortType = SORT_BY_TOP_RATED;}
            else if (item.getItemId() == R.id.my_favorite_movies) {sortType = SORT_BY_FAVORITES;}

            //clear the recyclerview and start Loader Task to do load the movie lists again
            clearRecyclerView();
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

    //help method to start a Loader
    private void startLoaderTask(int loaderId){

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<MovieDetails>> loaderTaskTrailers = loaderManager.getLoader(loaderId);

        if(loaderTaskTrailers == null) {
            loaderManager.initLoader(loaderId, null, this);
        } else {
            loaderManager.restartLoader(loaderId, null, this);
        }
    }

    //checks the display resolution and set the image size
    private void setNumColumnsBasedOnDisplaySize(){

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

    //clear all views (Movie Images in this case) of the RecyclerView
    private void clearRecyclerView() {
        //clear all vies in recyclerview
        RecyclerView mRecycleView = (RecyclerView) findViewById(R.id.rv_movie_posters);
        mRecycleView.removeAllViews();
    }

    @Override
    public Loader<ArrayList<MovieDetails>> onCreateLoader(int id, final Bundle args) {

        if ((id==LOADER_MOVIES_FROM_TMDB)||(id==LOADER_FAV_MOVIES_FROM_DATABASE)) {
            return new MoviesInfoFromTMDB(this);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MovieDetails>> loader, ArrayList<MovieDetails> movieDetailsArrayList) {

        //make progress bar invisible again
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        if(movieDetailsArrayList==null){
            movieDetailsArrayList = new ArrayList<>();
        }

        if((movieDetailsArrayList.size() == 0)&&(loader.getId()==LOADER_FAV_MOVIES_FROM_DATABASE)){

            Toast.makeText(this,getString(R.string.empty_favorite_movies_list), Toast.LENGTH_LONG).show();

        }

        //get the RecyclerView resource and bind to a GridLayoutManager
        mRecycleView = (RecyclerView) findViewById(R.id.rv_movie_posters);
        GridLayoutManager layoutManager = new GridLayoutManager(this, MainActivity.LAYOUT_NUM_COLUMS);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        //restoring the scroll position
        if(layoutManagerSavedState!=null)
            layoutManager.onRestoreInstanceState(layoutManagerSavedState);

        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setAdapter(new MoviePosterAdapter(movieDetailsArrayList, this));

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieDetails>> loader) {
        //do nothing
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_SORTTYPE, sortType);
        outState.putParcelable(BUNDLE_LAYOUT_MANAGER, mRecycleView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        sortType = savedInstanceState.getInt(BUNDLE_SORTTYPE);
        layoutManagerSavedState = savedInstanceState.getParcelable(BUNDLE_LAYOUT_MANAGER);
    }

}

