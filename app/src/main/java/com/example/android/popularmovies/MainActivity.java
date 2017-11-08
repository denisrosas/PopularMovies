package com.example.android.popularmovies;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static boolean sortPopularity = true;
    public static int LAYOUT_NUM_COLUMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this app was designed to portrait mode only
        setNumColumsBasedOnDisplaySize();

        MoviesInfoFromTMDB downloadTask = new MoviesInfoFromTMDB(this);

        downloadTask.execute(this);
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

            if(((item.getItemId() == R.id.sort_by_popularity)&&(sortPopularity == true))
                ||((item.getItemId() == R.id.sort_by_top_rated)&&(sortPopularity == false))) {

                //Nothing to do... sort order is already correct
                return super.onOptionsItemSelected(item);
            }

            RecyclerView mRecycleView = (RecyclerView) findViewById(R.id.rv_movie_posters);
            mRecycleView.removeAllViews();

            if(item.getItemId() == R.id.sort_by_popularity){
                sortPopularity = true;
            }else{
                sortPopularity = false;
            }

            MoviesInfoFromTMDB downloadTask = new MoviesInfoFromTMDB(this);
            downloadTask.execute(this);

            (findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);

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

}

