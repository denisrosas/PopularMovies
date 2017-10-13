package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.android.popularmovies.MoviesInfoFromTMDB.context;

public class MoviePosterAdapter extends RecyclerView.Adapter <MoviePosterAdapter.ImageViewHolder>{

    private final ArrayList<MovieDetails> localMovieList;
    public static final String TMDB_BASE_URL = "http://image.tmdb.org/t/p/";
    public static String TMDB_LIST_IMAGE_SIZE = "";
    public static String TMDB_DETAIL_IMAGE_SIZE = "";

    public MoviePosterAdapter(ArrayList<MovieDetails> movieList){
        localMovieList = movieList;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.posters_image_items, parent, false);

        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder iv_holder, int position) {

        //we're using this tag to identify the position later in setOnClickListener
        iv_holder.imageView.setTag(Integer.toString(position));

        //checking if it's an image file, to prevent downloading wrong files
        if (localMovieList.get(position).getPosterPath().endsWith(".jpg")
                || localMovieList.get(position).getPosterPath().endsWith(".png")) {

            Uri builtUri = returnImageUri(localMovieList.get(position).getPosterPath());
            Picasso.with(context).load(builtUri.toString()).into(iv_holder.imageView);
            //Log.i("denis", "link da imagem: " + builtUri.toString());

        } else {
            //if it's not image file, get fail image from Resources
            Picasso.with(context).load(R.drawable.erro_loading_image).into(iv_holder.imageView);
            Log.i("denis", "Fail to load the image.");

        }

        iv_holder.imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Log.i("denis OnClickListener", "a tag da view eh "+view.getTag());

                Intent i = new Intent(context, MovieDetailsActivity.class);

                //We're sending 3 vectors
                MovieDetails movieDetails = localMovieList.get(Integer.valueOf((String)view.getTag()));
                String [] extras_string = {movieDetails.getTitle(), movieDetails.getPosterPath(), movieDetails.getOverview(), movieDetails.getReleaseDate()};
                Double voteAvarage = movieDetails.getVoteAvarage();
                int voteCount = movieDetails.getVoteCount();

                i.putExtra("extras_string", extras_string);
                i.putExtra("voteAvarage", voteAvarage);
                i.putExtra("voteCount", voteCount);

                context.startActivity(i);

            }
        });
    }

    @Override
    public int getItemCount() {
        return localMovieList.size();
    }

    private Uri returnImageUri(String imageFileName){

        Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendPath(TMDB_LIST_IMAGE_SIZE)
                .appendPath(imageFileName)
                .build();

        Log.i("MoviePosterAdapter", "complete Link: "+ builtUri.toString());

        return builtUri;
    }

    class ImageViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView = null;

        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_poster);
        }
    }
}