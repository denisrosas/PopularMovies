package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Denis on 15/11/2017.
 */

public class MovieReviewsAdapter extends RecyclerView.Adapter <MovieReviewsAdapter.ReviewViewHolder> {

    ArrayList<Review> reviewList;
    Context activityContext;

    MovieReviewsAdapter(ArrayList<String> arrayListReviews, Context context){

        this.reviewList = new ArrayList<Review>();

        //understand that the Review Author and Text are received in a single string
        //they are separated by an '@'. we need to split them before accessing
        for(String review_author_and_text : arrayListReviews){

            String[] temp1 = review_author_and_text.split("@",2);
            Review review = new Review(temp1[0], temp1[1]);
            this.reviewList.add(review);

        }

        activityContext = context;
    }


    @Override
    public MovieReviewsAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View reviewView = inflater.inflate(R.layout.review_item, parent, false);

        return new ReviewViewHolder(reviewView);
    }

    @Override
    public void onBindViewHolder(MovieReviewsAdapter.ReviewViewHolder holder, int position) {

        holder.textViewReviewAuthor.setText(reviewList.get(position).author+":");
        holder.textViewReviewText.setText(reviewList.get(position).text);

    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView textViewReviewAuthor;
        TextView textViewReviewText;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            textViewReviewAuthor = (TextView) itemView.findViewById(R.id.tv_review_author);
            textViewReviewText = (TextView) itemView.findViewById(R.id.tv_review_text);
        }
    }

    class Review{
        String author;
        String text;

        Review(String author, String text){
            this.author = author;
            this.text = text;
        }
    }
}
