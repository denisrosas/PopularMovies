package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.android.popularmovies.MoviePosterAdapter.TMDB_BASE_URL;
import static com.example.android.popularmovies.MoviePosterAdapter.TMDB_DETAIL_IMAGE_SIZE;

public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<String>> {

    private static final int LOADER_TRAILERS_FROM_TMDB = 49;
    private static final int LOADER_REVIEWS_FROM_TMDB = 55;

    //Extra Variables:
    String [] extras_string;
    Double voteAvarage;
    int movieId;
    int voteCount;
    MovieDetails movieDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Intent i = getIntent();

        extras_string = i.getStringArrayExtra("extras_string");
        voteAvarage = i.getDoubleExtra("voteAvarage", 0.0);
        movieId = i.getIntExtra("movieId", 0);
        voteCount = i.getIntExtra("voteCount", 0);

        movieDetails = new MovieDetails(voteCount, movieId, voteAvarage, extras_string[0], extras_string[1], extras_string[2], extras_string[3]);

        Bundle bundle = new Bundle();

        LoaderManager loaderManagerTrailers = getSupportLoaderManager();
        Loader<ArrayList<String>> loaderTask = loaderManagerTrailers.getLoader(LOADER_TRAILERS_FROM_TMDB);

        if(loaderTask == null) {
            loaderManagerTrailers.initLoader(LOADER_TRAILERS_FROM_TMDB, bundle, this);
        } else {
            loaderManagerTrailers.restartLoader(LOADER_TRAILERS_FROM_TMDB, bundle, this);
        }

        LoaderManager loaderManagerReviews = getSupportLoaderManager();
        Loader<ArrayList<String>> loaderTaskReviews = loaderManagerReviews.getLoader(LOADER_REVIEWS_FROM_TMDB);

        if(loaderTaskReviews == null) {
            loaderManagerReviews.initLoader(LOADER_REVIEWS_FROM_TMDB, bundle, this);
        } else {
            loaderManagerReviews.restartLoader(LOADER_REVIEWS_FROM_TMDB, bundle, this);
        }

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

    @Override
    public Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {


        //Extraimos os dados do Bundle
        if(id==LOADER_TRAILERS_FROM_TMDB) {
//            String queryType = args.getString("QueryType");
            return new TrailerListFromTMDB(this, movieId, getString(R.string.tmdb_api_key));
        } else if (id==LOADER_REVIEWS_FROM_TMDB){
            return new ReviewListFromTMDB(this, movieId, getString(R.string.tmdb_api_key));
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> arrayList) {

        if (arrayList == null) {
            Toast.makeText(this, getString(R.string.check_internet), Toast.LENGTH_LONG).show();
            return;
        } else
            setTextViewsAndImages(movieDetails);

        if(loader.getId()==LOADER_TRAILERS_FROM_TMDB) {

            Log.i("denis", "LOADER_TRAILERS_FROM_TMDB");

            for (String text : arrayList) {
                Log.i("denis", "trailer numero: " + text);
            }

        } else if (loader.getId() == LOADER_REVIEWS_FROM_TMDB){

            Log.i("denis", "LOADER_REVIEWS_FROM_TMDB");

            for(String text : arrayList){
                String[] temp1 = text.split("@",2);
                Log.i("denis", "onLoadFinished() - review author: " + temp1[0]);
                Log.i("denis", "onLoadFinished() - review text: " + temp1[1]);
            }

        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String>> loader) {

    }
}
