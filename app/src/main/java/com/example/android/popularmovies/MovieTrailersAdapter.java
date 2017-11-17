package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Denis on 15/11/2017.
 */

public class MovieTrailersAdapter extends RecyclerView.Adapter  <MovieTrailersAdapter.TrailerViewHolder> {

    private final ArrayList<String> trailerList;
    public static final String YOUTUBE_BASE_URL = "http://img.youtube.com/vi/";
    public static final String YOUTUBE_THUMBNAIL_FILE = "0.jpg";
    private Context activityContext;

    MovieTrailersAdapter(ArrayList<String> trailerList, Context context){
        this.trailerList = trailerList;
        activityContext = context;
    }

    @Override
    public MovieTrailersAdapter.TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View trailerView = inflater.inflate(R.layout.trailer_item, parent, false);

        return new TrailerViewHolder(trailerView);
    }

    @Override
    public void onBindViewHolder(MovieTrailersAdapter.TrailerViewHolder holder, int position) {

        //the tag will be the ID of a youtube trailer like https://www.youtube.com/watch?v=XXXX
        holder.trailerImageView.setTag(trailerList.get(position));

        //set the text view above the trailer link
        //holder.trailerTextView.setText(activityContext.getString(R.string.trailer)+": "+(position+1));

        //set the image to the Thumbnail of Youtube Video
        Uri builtUri = returnThumbnailUri(trailerList.get(position));
        Picasso.with(activityContext).load(builtUri.toString()).into(holder.trailerImageView);

        holder.trailerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String youtubeVideoId = (String) view.getTag();

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + youtubeVideoId));

                //check if app can resolve the intent to launch Youtube app
                if (intent.resolveActivity(activityContext.getPackageManager()) != null) {
                    activityContext.startActivity(intent);
                } else{
                    Toast.makeText(activityContext, activityContext.getString(R.string.youtube_app_not_found), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private Uri returnThumbnailUri(String videoId) {

        Uri builtUri =  Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendPath(videoId)
                .appendPath(YOUTUBE_THUMBNAIL_FILE)
                .build();

        Log.i("MovieTrailerAdapter", "complete Link: "+ builtUri.toString());

        return builtUri;
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder {

        //TextView trailerTextView;
        ImageView trailerImageView;

        TrailerViewHolder(View itemView) {
            super(itemView);
            //trailerTextView = (TextView) itemView.findViewById(R.id.tv_trailer_id);
            trailerImageView = (ImageView) itemView.findViewById(R.id.iv_trailer_thumbnail);
        }
    }
}
