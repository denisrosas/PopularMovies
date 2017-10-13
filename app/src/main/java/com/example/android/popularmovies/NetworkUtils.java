package com.example.android.popularmovies;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;


final class NetworkUtils {

    private static final String LANGUAGE = "en-US"; //can change to en-US, pt-BR
    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String SORT_BY_POPULARITY = "popular";
    private static final String SORT_BY_TOP_RATED = "top_rated";
    private static final String INCLUDE_ADULT = "false"; //set true to include adult movies

    private final static String INCLUDE_ADULT_PARAM = "include_adult";
    private final static String PAGE_PARAM = "page";
    private final static String API_KEY_PARAM = "api_key";
    private final static String LANGUAGE_PARAM = "language";


    public static URL buildUrl(String api_key) {

        URL url = null;

        Uri.Builder UriBuilder = Uri.parse(TMDB_BASE_URL).buildUpon();

        if(MainActivity.sortPopularity == true){
            UriBuilder.appendPath(SORT_BY_POPULARITY);
        } else {
            UriBuilder.appendPath(SORT_BY_TOP_RATED);
        }

        Uri builtUri = UriBuilder.appendQueryParameter(API_KEY_PARAM,api_key)
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE)
                .appendQueryParameter(INCLUDE_ADULT_PARAM, INCLUDE_ADULT)
                .appendQueryParameter(PAGE_PARAM, "1")
                .build();

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.i("denis buildUrl", "url: " + url.toString());

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            //InputStreamReader reader = new InputStreamReader(in);

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            //TODO - ver se esse demiliter eh necessario
            String jsonReturn = "";

            if(scanner.hasNext() == false)
                return null;

            while(scanner.hasNext()){
                jsonReturn = jsonReturn.concat(scanner.next());
            }
            return jsonReturn;

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return null;
    }

}
