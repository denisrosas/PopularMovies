package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.android.popularmovies.data.favoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID;
import static com.example.android.popularmovies.data.favoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_OVERVIEW;
import static com.example.android.popularmovies.data.favoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_POSTER_PATH;
import static com.example.android.popularmovies.data.favoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_RELEASE_DATE;
import static com.example.android.popularmovies.data.favoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TITLE;
import static com.example.android.popularmovies.data.favoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_VOTE_AVERAGE;
import static com.example.android.popularmovies.data.favoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_VOTE_COUNT;
import static com.example.android.popularmovies.data.favoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME;
import static com.example.android.popularmovies.data.favoriteMoviesContract.FavoriteMoviesEntry._ID;

/**
 * Created by Denis on 08/11/2017.
 */

public class favoriteMoviesDbHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "favoriteMoviesDb.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 1;

    public favoriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Create tasks table (careful to follow SQL formatting rules)

        final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "  + TABLE_NAME + " (" +
                _ID  + " INTEGER PRIMARY KEY, " +
                COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                COLUMN_MOVIE_TITLE + " VARCHAR (65), " +
                COLUMN_MOVIE_POSTER_PATH  + " VARCHAR (40), " +
                COLUMN_MOVIE_OVERVIEW + " VARCHAR (1000), " +
                COLUMN_MOVIE_RELEASE_DATE + " DATE, " +
                COLUMN_MOVIE_VOTE_COUNT + " INTEGER NOT NULL, " +
                COLUMN_MOVIE_VOTE_AVERAGE + " REAL);";

        try {
            sqLiteDatabase.execSQL(CREATE_TABLE);
        } catch (SQLException e) {
            Log.i("denis","error to create database");
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
