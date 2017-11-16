package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.favoriteMoviesContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.android.popularmovies.MoviePosterAdapter.TMDB_BASE_URL;
import static com.example.android.popularmovies.MoviePosterAdapter.TMDB_DETAIL_IMAGE_SIZE;

public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<String>> {

    private static final int LOADER_TRAILERS_FROM_TMDB = 49;
    private static final int LOADER_REVIEWS_FROM_TMDB = 55;
    private static boolean movieIsFavorite;
    Button favoriteButton;

    //Extra Variable:
    MovieDetails movieDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pbLoadMovieDetails);
        progressBar.setVisibility(View.VISIBLE);

        movieDetails = getExtraVariablesFromIntent();
        favoriteButton = (Button) findViewById(R.id.add_to_favorite);

        startLoaderTask(LOADER_TRAILERS_FROM_TMDB);
        startLoaderTask(LOADER_REVIEWS_FROM_TMDB);

        movieIsFavorite = isMovieInFavoriteList(movieDetails.getId());

        if(movieIsFavorite)
            favoriteButton.setText(getString(R.string.remove_movie_from_favorites));
        else
            favoriteButton.setText(getString(R.string.add_movie_to_favorites));

        //TODO - check fi this movie is already a favorite. if is, set the heart icon to red

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.back_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.back_menu){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private Uri returnImageUri(String imageFileName){

        Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendPath(TMDB_DETAIL_IMAGE_SIZE)
                .appendPath(imageFileName)
                .build();

        Log.i("MoviePosterAdapter", "complete Link: "+ builtUri.toString());

        return builtUri;

    }

    private void setTextViewsAndImages(MovieDetails movieDetails){

        Uri image_uri = returnImageUri(movieDetails.getPosterPath());

        Log.i("denis","image_uri: "+image_uri.toString());

        Picasso.with(this).load(image_uri.toString()).into((ImageView)findViewById(R.id.iv_detail_poster));

        ((TextView) findViewById(R.id.tv_movie_title)).setText(movieDetails.getTitle());
        ((TextView) findViewById(R.id.tv_rating)).setText("Rating: "+
                Double.toString(movieDetails.getVoteAvarage())
                +" ("+movieDetails.getVoteCount()+" voters)");

        ((TextView) findViewById(R.id.tv_release_date)).setText(getString(R.string.release_date)+movieDetails.getReleaseDate());
        ((TextView) findViewById(R.id.tv_synopsis)).setText(movieDetails.getOverview());
    }

    private void startLoaderTask(int loaderId){

        Bundle bundle = new Bundle();

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<String>> loaderTaskTrailers = loaderManager.getLoader(loaderId);

        if(loaderTaskTrailers == null) {
            loaderManager.initLoader(loaderId, bundle, this);
        } else {
            loaderManager.restartLoader(loaderId, bundle, this);
        }
    }

    private MovieDetails getExtraVariablesFromIntent() {

        String [] extras_string;
        Double voteAvarage;
        int movieId;
        int voteCount;
        Intent i = getIntent();

        extras_string = i.getStringArrayExtra("extras_string");
        voteAvarage = i.getDoubleExtra("voteAvarage", 0.0);
        movieId = i.getIntExtra("movieId", 0);
        voteCount = i.getIntExtra("voteCount", 0);

        return new MovieDetails(voteCount, movieId, voteAvarage, extras_string[0], extras_string[1], extras_string[2], extras_string[3]);

    }

    public void addDeleteMovieFavorites(View view){

       if(view.getId()==R.id.add_to_favorite){

           Button favoriteButton = (Button) findViewById(R.id.add_to_favorite);

           if(!movieIsFavorite) {

               //insert movie in favorites database
               Uri uri = favoriteMoviesContract.FavoriteMoviesEntry.CONTENT_URI;
               uri = uri.buildUpon().build();

               ContentValues contentValues = new ContentValues();
               contentValues.put(favoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID, movieDetails.getId());
               contentValues.put(favoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TITLE, movieDetails.getTitle());
               contentValues.put(favoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_OVERVIEW, movieDetails.getOverview());
               contentValues.put(favoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_POSTER_PATH, movieDetails.getPosterPath());
               contentValues.put(favoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_RELEASE_DATE, movieDetails.getReleaseDate());
               contentValues.put(favoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_VOTE_AVERAGE, movieDetails.getVoteAvarage());
               contentValues.put(favoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_VOTE_COUNT, movieDetails.getVoteCount());

               Uri resultUri = null;
               resultUri = getContentResolver().insert(uri, contentValues);
               if(resultUri!=null) {
                   favoriteButton.setText(getString(R.string.remove_movie_from_favorites));
                   movieIsFavorite = true;
               }
               Log.i("denis", "MovieDetailsActivity - uri: " + resultUri.toString());

           } else{

               Uri uri = favoriteMoviesContract.FavoriteMoviesEntry.CONTENT_URI;
               uri = uri.buildUpon().appendPath(Integer.toString(movieDetails.getId())).build();

               int deleteCount = getContentResolver().delete(uri, null, null);
               if(deleteCount==1){
                   favoriteButton.setText(getString(R.string.add_movie_to_favorites));
                   movieIsFavorite=false;
                   Log.i("denis", "MovieDetailsActivity - addDeleteMovieFavorites - movie Successfully Removed from Favorites");
               } else {
                   Log.i("denis", "MovieDetailsActivity - addDeleteMovieFavorites - Error to remove. deleteCount: "+deleteCount);
                   Toast.makeText(this, "Error to remove movie from Favorites", Toast.LENGTH_LONG).show();
               }

           }
       }

    }

    private boolean isMovieInFavoriteList(int movieId) {

        Uri uri = favoriteMoviesContract.FavoriteMoviesEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(Integer.toString(movieId)).build();

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor.getCount()==1){
            cursor.close();
            return true;
        } else if (cursor.getCount()==0){
            cursor.close();
            return false;
        }
        return false;
    }

    @Override
    public Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {

        //Extraimos os dados do Bundle
        if(id==LOADER_TRAILERS_FROM_TMDB) {
//            String queryType = args.getString("QueryType");
            return new TrailerListFromTMDB(this, movieDetails.getId(), getString(R.string.tmdb_api_key));
        } else if (id==LOADER_REVIEWS_FROM_TMDB){
            return new ReviewListFromTMDB(this, movieDetails.getId(), getString(R.string.tmdb_api_key));
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> loaderResult) {

        if (loaderResult == null) {
            Toast.makeText(this, getString(R.string.check_internet), Toast.LENGTH_LONG).show();
            return;
        } else {
            //set textViews before trailers and reviews
            setTextViewsAndImages(movieDetails);
        }

        if(loader.getId()==LOADER_TRAILERS_FROM_TMDB) {

            //get the RecyclerView resource and bind to a GridLayoutManager
            RecyclerView mRecycleView = (RecyclerView) findViewById(R.id.rv_trailer_list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mRecycleView.setLayoutManager(layoutManager);
            mRecycleView.setHasFixedSize(true);
            mRecycleView.setAdapter(new MovieTrailersAdapter(loaderResult, this));

        } else if (loader.getId() == LOADER_REVIEWS_FROM_TMDB){

            ProgressBar progressBar = (ProgressBar) findViewById(R.id.pbLoadMovieDetails);
            progressBar.setVisibility(View.INVISIBLE);

            Log.i("denis", "LOADER_REVIEWS_FROM_TMDB");

            for(String text : loaderResult){
                String[] temp1 = text.split("@",2);
                Log.i("denis", "onLoadFinished() - review author: " + temp1[0]);
                Log.i("denis", "onLoadFinished() - review text: " + temp1[1]);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String>> loader) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
