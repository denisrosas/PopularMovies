package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popularmovies.data.favoriteMoviesContract.FavoriteMoviesEntry;

import static com.example.android.popularmovies.data.favoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID;


public class favoriteMoviesCP extends ContentProvider{


    favoriteMoviesDbHelper DbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    public static final int FAV_MOVIES = 100;
    public static final int FAV_MOVIES_WITH_ID = 101;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DbHelper = new favoriteMoviesDbHelper(context);
        return true;
    }

    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //this case is for query, insert
        uriMatcher.addURI(favoriteMoviesContract.AUTHORITY, favoriteMoviesContract.FAVORITE_MOVIES_PATH, FAV_MOVIES);

        //this case is for delete only
        uriMatcher.addURI(favoriteMoviesContract.AUTHORITY, favoriteMoviesContract.FAVORITE_MOVIES_PATH+"/#", FAV_MOVIES_WITH_ID);

        return uriMatcher;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = DbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor cursor;

        switch (match){
            case FAV_MOVIES:

                String[] columns = {COLUMN_MOVIE_ID};
                //this content provider returns all movies in favoritemovies table
                cursor = database.query(favoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME,
                        columns, //get only movie_id column
                        null,
                        null,
                        null,
                        null,
                        sortOrder);
                break;

            case FAV_MOVIES_WITH_ID:
                String movie_id = uri.getPathSegments().get(1);
                String where_clause = COLUMN_MOVIE_ID+ "= '"+movie_id+"'"; //we want only movie_id

                cursor = database.query(favoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME,
                        null, //get all columns
                        where_clause, //where clause
                        null,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI: "+uri.toString());
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        SQLiteDatabase database = DbHelper.getWritableDatabase();

        Log.i("denis","favoriteMovies CP - insert - uri: "+uri.toString());

        //if matches the URI of our content provider, will return 100
        int match = sUriMatcher.match(uri);
        Uri returnUri = null; // URI to be returned


        switch (match){
            case FAV_MOVIES:
                long id = database.insert(favoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME,
                        null, contentValues);

                if(id >  0) {
                    //buiding the URI to access this new favorite movie
                    returnUri = ContentUris.withAppendedId(favoriteMoviesContract.FavoriteMoviesEntry.CONTENT_URI, id);

                    //alert the main activity of the changes
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                else
                    throw new android.database.SQLException("Failed to insert new row " + uri.toString());

                break;
            default:
                throw new android.database.SQLException("Unknown URI " + uri.toString());
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase database = DbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted tasks
        int moviesDeleted = 0; // starts as 0
        String where_movieid_equals = COLUMN_MOVIE_ID+"=?";

        switch (match){
            case FAV_MOVIES_WITH_ID:
                String[] movie_ids = new String[1];

                //URI: content://<authority> /path /#
                //index 0 is the path, index 1 is the ID and so on...
                movie_ids[0] = uri.getPathSegments().get(1);

                //Delete the selected movie from the table. User unfavorited it
                moviesDeleted = database.delete(FavoriteMoviesEntry.TABLE_NAME, where_movieid_equals, movie_ids);
                break;

            default:
                throw new UnsupportedOperationException("unknown URI: "+uri.toString());
        }
        if (moviesDeleted>0){
            //alert the contentResolver that a change has been made. Other apps might be accessing this data
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return moviesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("Unknown URI: "+uri.toString());
        //update is not necessary. Each line of the table have a copy of the movie from
        //tmdb site. as its a copy of the data, we don't need to update any fields
        //we just insert the movies marked as favorite and delete the unmarked
    }
}
