package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static com.example.android.popularmovies.MoviePosterAdapter.TMDB_BASE_URL;
import static com.example.android.popularmovies.MoviePosterAdapter.TMDB_DETAIL_IMAGE_SIZE;

public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Intent i = getIntent();

        String [] extras_string = i.getStringArrayExtra("extras_string");
        Double voteAvarage = i.getDoubleExtra("voteAvarage", 0.0);
        int voteCount = i.getIntExtra("voteCount", 0);

//        Log.i("MovieDetailsActivity", "denis - extra_string[0]: "+extras_string[0]
//                        +"\nextra_string[1]: "+extras_string[1]
//                        +"\nextra_string[2]: "+extras_string[2]
//                        +"\nextra_string[3]: "+extras_string[3]
//                        +"\nvoteAvarage: "+voteAvarage
//                        +"\nvoteCount: "+voteCount);

        //TODO - Corrigir ultimo parametro. i.getExtra("movie_trailers");
        MovieDetails movieDetails = new MovieDetails(voteCount, 0, voteAvarage, extras_string[0], extras_string[1], extras_string[2], extras_string[3], extras_string);

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
}
