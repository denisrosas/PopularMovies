package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Denis on 08/11/2017.
 */

public class favoriteMoviesContract {
    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.android.popularmovies";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String FAVORITE_MOVIES_PATH = "favoritemovies";

    /* FavoriteMoviesEntry is an inner class that defines the contents of the favoritemovies table */
    public static final class FavoriteMoviesEntry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(FAVORITE_MOVIES_PATH).build();

        // Task table and column names
        public static final String TABLE_NAME = "favoritemovies";

        // Since TaskEntry implements the interface "BaseColumns", it has an automatically produced
        // "_ID" column in addition to the two below
        public static final String COLUMN_MOVIE_ID = "movie_id";

    }
}
