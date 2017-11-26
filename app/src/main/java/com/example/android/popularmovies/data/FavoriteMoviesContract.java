package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;


public class FavoriteMoviesContract {
    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.android.popularmovies";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String FAVORITE_MOVIES_PATH = "favoritemovies";

    /* FavoriteMoviesEntry is an inner class that defines the contents of the favoritemovies table */
    public static final class FavoriteMoviesEntry implements BaseColumns {

        // FavoriteMoviesEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(FAVORITE_MOVIES_PATH).build();

        // Task table and column names
        public static final String TABLE_NAME = "favoritemovies";

        // Since FavoriteMoviesEntry implements the interface "BaseColumns", it has an
        // automatically produced "_ID" column
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_POSTER_PATH = "movie_poster_path";
        public static final String COLUMN_MOVIE_OVERVIEW = "movie_overview";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movie_release_date";
        public static final String COLUMN_MOVIE_VOTE_COUNT = "movie_vote_count";
        public static final String COLUMN_MOVIE_VOTE_AVERAGE = "movie_vote_average";

    }
}
