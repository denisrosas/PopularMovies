package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.FavoriteMoviesContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.android.popularmovies.MoviePosterAdapter.TMDB_BASE_URL;
import static com.example.android.popularmovies.MoviePosterAdapter.TMDB_DETAIL_IMAGE_SIZE;

public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<String>> {

    private static final int LOADER_TRAILERS_FROM_TMDB = 49;
    private static final int LOADER_REVIEWS_FROM_TMDB = 55;
    private static boolean movieIsFavorite;
    Button favoriteButton;
    private static boolean trailers_shrinked = false;
    private static boolean reviews_shrinked = false;
    private static String BUNDLE_TRAILERS_SHRINKED = "TRAILERS_SHRINKED";
    private static String BUNDLE_REVIEWS_SHRINKED = "REVIEWS_SHRINKED";
    private static String BUNDLE_TRAILERS_REC_VIEW = "TRAILERS_REC_VIEW";
    private static String BUNDLE_REVIEWS_REC_VIEW = "REVIEWS_REC_VIEW";
    private static String BUNDLE_SCROLL_POSITION = "SCROLL_POSITION";

    Parcelable layoutManSavedStateTrailers = null;
    Parcelable layoutManSavedStateReviews = null;

    private static int scrollY = 0;

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

        Picasso.with(this).load(image_uri.toString()).into((ImageView)findViewById(R.id.iv_detail_poster));

        ((TextView) findViewById(R.id.tv_movie_title)).setText(movieDetails.getTitle());
        ((TextView) findViewById(R.id.tv_rating)).setText("Rating: "+
                Double.toString(movieDetails.getVoteAvarage())
                +" ("+movieDetails.getVoteCount()+" voters)");

        ((TextView) findViewById(R.id.tv_release_date)).setText(getString(R.string.release_date)+" "+movieDetails.getReleaseDate().replaceAll("-","/"));
        ((TextView) findViewById(R.id.tv_synopsis)).setText(movieDetails.getOverview());
    }

    private void startLoaderTask(int loaderId){

        if(NetworkUtils.isNetworkConnected(this)) {

            Bundle bundle = new Bundle();

            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<ArrayList<String>> loaderTaskTrailers = loaderManager.getLoader(loaderId);

            if (loaderTaskTrailers == null) {
                loaderManager.initLoader(loaderId, bundle, this);
            } else {
                loaderManager.restartLoader(loaderId, bundle, this);
            }
        } else{
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.pbLoadMovieDetails);
            progressBar.setVisibility(View.INVISIBLE);
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
               Uri uri = FavoriteMoviesContract.FavoriteMoviesEntry.CONTENT_URI;
               uri = uri.buildUpon().build();

               ContentValues contentValues = new ContentValues();
               contentValues.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID, movieDetails.getId());
               contentValues.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TITLE, movieDetails.getTitle());
               contentValues.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_OVERVIEW, movieDetails.getOverview());
               contentValues.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_POSTER_PATH, movieDetails.getPosterPath());
               contentValues.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_RELEASE_DATE, movieDetails.getReleaseDate());
               contentValues.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_VOTE_AVERAGE, movieDetails.getVoteAvarage());
               contentValues.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_VOTE_COUNT, movieDetails.getVoteCount());

               Uri resultUri = null;
               resultUri = getContentResolver().insert(uri, contentValues);
               Log.i("MovieDetailsActivity", "addDeleteMovieFavorites() - resultUri: " + resultUri.toString());

               if(resultUri!=null) {
                   favoriteButton.setText(getString(R.string.remove_movie_from_favorites));
                   movieIsFavorite = true;
                   Log.i("MovieDetailsActivity", "addDeleteMovieFavorites() - movie successfully added to favorite list");
               } else {
                   Log.i("MovieDetailsActivity", "addDeleteMovieFavorites() - failed to add movie to favorite list");
               }

           } else{

               Uri uri = FavoriteMoviesContract.FavoriteMoviesEntry.CONTENT_URI;
               uri = uri.buildUpon().appendPath(Integer.toString(movieDetails.getId())).build();

               Log.i("MovieDetailsActivity", "addDeleteMovieFavorites() - uri: " + uri.toString());

               int deleteCount = getContentResolver().delete(uri, null, null);
               if(deleteCount==1){
                   favoriteButton.setText(getString(R.string.add_movie_to_favorites));
                   movieIsFavorite=false;
                   Log.i("MovieDetailsActivity", "addDeleteMovieFavorites() - movie Successfully Removed from Favorites");
               } else {
                   Log.i("MovieDetailsActivity", "addDeleteMovieFavorites() - Error to remove. deleteCount: "+deleteCount);
                   Toast.makeText(this, "Error to remove movie from Favorites", Toast.LENGTH_LONG).show();
               }

           }
       }

    }

    private boolean isMovieInFavoriteList(int movieId) {

        Uri uri = FavoriteMoviesContract.FavoriteMoviesEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(Integer.toString(movieId)).build();

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor.getCount()==1){
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
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
            //Toast.makeText(this, getString(R.string.check_internet), Toast.LENGTH_LONG).show();
            Log.i("MovieDetailsActivity", "onLoadFinished() returned null Arraylist");
            return;
        } else {
            //set textViews before trailers and reviews
            setTextViewsAndImages(movieDetails);
        }

        if(loader.getId()==LOADER_TRAILERS_FROM_TMDB) {

            //get the RecyclerView resource and bind to a GridLayoutManager
            RecyclerView mRecycleView = (RecyclerView) findViewById(R.id.rv_trailer_list);
            //LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

            //restoring the scroll position
            if(layoutManSavedStateTrailers!=null){
                layoutManager.onRestoreInstanceState(layoutManSavedStateTrailers);
            }

            mRecycleView.setLayoutManager(layoutManager);
            mRecycleView.setHasFixedSize(true);
            mRecycleView.setAdapter(new MovieTrailersAdapter(loaderResult, this));

        } else if (loader.getId() == LOADER_REVIEWS_FROM_TMDB){

            ProgressBar progressBar = (ProgressBar) findViewById(R.id.pbLoadMovieDetails);
            progressBar.setVisibility(View.INVISIBLE);

            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rv_review_list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

            //restoring the scroll position
            if(layoutManSavedStateReviews!=null){
                layoutManager.onRestoreInstanceState(layoutManSavedStateReviews);
            }

            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setAdapter(new MovieReviewsAdapter(loaderResult, this));

        }

        //restore scroll position after the recyclerviews are filled
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollViewMovieDetails);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, scrollY);
            }
        });

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String>> loader) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //store in bundle if the RecyclerViews are shrinked
        outState.putBoolean(BUNDLE_TRAILERS_SHRINKED, trailers_shrinked);
        outState.putBoolean(BUNDLE_REVIEWS_SHRINKED, reviews_shrinked);

        //store on a bundle the scrollView position of the recyclerViews (trailer and reviews)
        RecyclerView recViewTrailers = (RecyclerView) findViewById(R.id.rv_trailer_list);
        RecyclerView recViewReviews = (RecyclerView) findViewById(R.id.rv_review_list);

        outState.putParcelable(BUNDLE_REVIEWS_REC_VIEW, recViewTrailers.getLayoutManager().onSaveInstanceState());
        outState.putParcelable(BUNDLE_TRAILERS_REC_VIEW, recViewTrailers.getLayoutManager().onSaveInstanceState());

        //saving the scroll position
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollViewMovieDetails);
        outState.putInt(BUNDLE_SCROLL_POSITION, scrollView.getScrollY());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        TextView textView;
        RecyclerView recyclerView;

        //Restoring if trailers and reviews is schrinked
        //either if BUNDLE_TRAILERS_SHRINKED is false or is not saved in the bundle, the default is false
        if(savedInstanceState.getBoolean(BUNDLE_TRAILERS_SHRINKED, false)){
            textView  = (TextView) findViewById(R.id.tv_trailer_title);
            recyclerView = (RecyclerView) findViewById(R.id.rv_trailer_list);
            textView.setText(getString(R.string.trailers_shrinked));
            recyclerView.setVisibility(View.GONE);
            trailers_shrinked = true;
        } else
            trailers_shrinked = false;

        if(savedInstanceState.getBoolean(BUNDLE_REVIEWS_SHRINKED, false)){
            textView = (TextView) findViewById(R.id.tv_reviews_title);
            recyclerView = (RecyclerView) findViewById(R.id.rv_review_list);
            textView.setText(getString(R.string.reviews_shrinked));
            recyclerView.setVisibility(View.GONE);
            reviews_shrinked = true;
        } else
            reviews_shrinked = false;

        //restoring the recylerviews state to global variables
        layoutManSavedStateReviews = savedInstanceState.getParcelable(BUNDLE_REVIEWS_REC_VIEW);
        layoutManSavedStateTrailers = savedInstanceState.getParcelable(BUNDLE_TRAILERS_REC_VIEW);

        //restoring the scroll position
        scrollY = savedInstanceState.getInt(BUNDLE_SCROLL_POSITION);

    }

    public void hideShowTrailers(View view){

        TextView textView  = (TextView) view;
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_trailer_list);

        if(trailers_shrinked) {
            textView.setText(getString(R.string.trailers_expanded));
            recyclerView.setVisibility(View.VISIBLE);
            trailers_shrinked = false;

        } else {
            textView.setText(getString(R.string.trailers_shrinked));
            recyclerView.setVisibility(View.GONE);
            trailers_shrinked = true;
        }

    }

    public void hideShowReviews(View view){

        TextView textView  = (TextView) view;
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_review_list);

        if(reviews_shrinked) {
            textView.setText(getString(R.string.reviews_expanded));
            recyclerView.setVisibility(View.VISIBLE);
            reviews_shrinked = false;

        } else {
            textView.setText(getString(R.string.reviews_shrinked));
            recyclerView.setVisibility(View.GONE);
            reviews_shrinked = true;
        }

    }
}
