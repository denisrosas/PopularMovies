package com.example.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Denis on 15/11/2017.
 */

public class MovieReviewsAdapter extends RecyclerView.Adapter <MovieReviewsAdapter.ReviewViewHolder> {
    @Override
    public MovieReviewsAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MovieReviewsAdapter.ReviewViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        public ReviewViewHolder(View itemView) {
            super(itemView);
        }
    }
}
