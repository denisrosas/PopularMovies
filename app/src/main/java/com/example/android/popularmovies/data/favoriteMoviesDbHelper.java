package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.popularmovies.data.favoriteMoviesContract.FavoriteMoviesEntry.*;

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
        final String CREATE_TABLE = "CREATE TABLE "  + TABLE_NAME + " (" +
                _ID  + " INTEGER PRIMARY KEY, " +
                COLUMN_MOVIE_ID + " INTEGER NOT NULL);";

        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
