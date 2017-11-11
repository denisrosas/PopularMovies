package com.example.android.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;


final class NetworkUtils {

    private static final String LANGUAGE = "en-US"; //can change to en-US, pt-BR
    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String SORT_BY_POPULARITY = "popular";
    private static final String SORT_BY_TOP_RATED = "top_rated";
    private static final String REVIEWS = "reviews";
    private static final String VIDEOS = "videos";
    private static final String INCLUDE_ADULT = "false"; //set true to include adult movies

    private final static String INCLUDE_ADULT_PARAM = "include_adult";
    private final static String PAGE_PARAM = "page";
    private final static String API_KEY_PARAM = "api_key";
    private final static String LANGUAGE_PARAM = "language";

    private final static String JSON_TOTAL_PAGES = "total_pages";
    private final static String JSON_REVIEW_AUTHOR = "author";
    private final static String JSON_REVIEW_CONTENT = "content";
    private final static String JSON_REVIEW_RESULTS = "results";
    private final static String JSON_VIDEOS_SITE = "site";
    private final static String JSON_VIDEOS_KEY = "key";
    private final static String JSON_VIDEOS_YOUTUBE = "YouTube";



    public static URL buildUrlMovieList(String api_key) {

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

        Log.i("denis buildUrlMovieList", "url: " + url.toString());

        return url;
    }

    public static URL buildUrlReviewList(String api_key, int movieID, int page) {

        URL url = null;
        //build an URI to get the reviews
        Uri.Builder UriBuilder = Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendPath(Integer.toString(movieID));

        Uri builtUri = UriBuilder
                .appendPath(REVIEWS)
                .appendQueryParameter(API_KEY_PARAM,api_key)
                .appendQueryParameter(PAGE_PARAM, Integer.toString(page))
                .build();

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static ArrayList<String[]> buildAndDownloadReviewsList(String api_key, int movieID) {

        URL url = null;
        JSONObject jsonObject, jsonObjectReviews = null;
        JSONArray jsonArray = null;
        int total_pages = 0;
        ArrayList<String[]> reviewsList = new ArrayList<>();

        //request page 1 because at first we don't know the page count. We'll know after download the first JSON
        url = buildUrlReviewList(api_key, movieID, 1);
        Log.i("denis buildUrl", "buildAndDownloadReviewsList() page 1 url: " + url.toString());

        //Download JSON of Reviews
        try {
            //download JSON from the built URI
            String jsonTMDBReviewList = getResponseFromHttpUrl(url);
            jsonObject = new JSONObject(jsonTMDBReviewList);

            //get number of pages from JSON. Its necessary to download the first page to know the total_pages
            total_pages = jsonObject.getInt(JSON_TOTAL_PAGES);

            //getting the Reviews array from JSON
            if(total_pages>0)
                jsonArray = jsonObject.getJSONArray(JSON_REVIEW_RESULTS);
            else {
                Log.i("denis ", "No Reviews Found for this movie!!!!");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("denis ", "jsonArray: "+jsonArray.toString());
        //for all reviews in the JSON get get the Author and Content and store in reviewsList var
        for(int index=0; index<jsonArray.length(); index++){
            try{
                reviewsList.add(parseJSONGetAuthorAndContent(jsonArray.getJSONObject(index)));
                Log.i("denis Reviews", "reviewsList posicao "+index+" armazenou: "+reviewsList.get(index)[0] +" e "+reviewsList.get(index)[1]);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        //if there is more than one page, download the reviews from the other pages
        if(total_pages>1){
            for(int current_page = 2; current_page<=total_pages; current_page++){
                url = buildUrlReviewList(api_key, movieID, current_page);
                Log.i("denis buildUrl", "buildAndDownloadReviewsList() page 2 url: " + url.toString());
                try {
                    //download JSON from the built URI
                    String jsonTMDBReviewList = getResponseFromHttpUrl(url);
                    jsonObject = new JSONObject(jsonTMDBReviewList);
                    jsonArray = jsonObject.getJSONArray(JSON_REVIEW_RESULTS);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //for all reviews in the JSON get get the Author and Content and store in reviewsList var
                for(int index=0; index<jsonArray.length(); index++){
                    try{
                        reviewsList.add(parseJSONGetAuthorAndContent(jsonArray.getJSONObject(index)));
                        Log.i("denis Reviews", "reviewsList pagina: "+current_page+" posicao: "+(index+5)+" armazenou: "+reviewsList.get(index+5)[0] +" e "+reviewsList.get(index+5)[1]);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

        return reviewsList;
    }

    public static String[] parseJSONGetAuthorAndContent(JSONObject jsonObject){
        String[] reviewInfo = {"",""};
        //jsonObjectReviews = jsonArray.getJSONObject(index);
        try {
            reviewInfo[MoviesInfoFromTMDB.REVIEW_AUTHOR_INDEX] = jsonObject.getString(JSON_REVIEW_AUTHOR);
            reviewInfo[MoviesInfoFromTMDB.REVIEW_CONTENT_INDEX] = jsonObject.getString(JSON_REVIEW_CONTENT);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reviewInfo;
    }

    public static URL buildUrlTrailerList(String api_key, int movieID) {

        URL url = null;
        //build an URI to get the reviews
        Uri.Builder UriBuilder = Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendPath(Integer.toString(movieID));

        Uri builtUri = UriBuilder
                .appendPath(VIDEOS)
                .appendQueryParameter(API_KEY_PARAM,api_key)
                .build();

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static ArrayList<String> buildAndDownloadTrailerList(String api_key, int movieID){

        URL url = null;
        JSONObject jsonObject, jsonObjectReviews = null;
        JSONArray jsonArray = null;
        ArrayList<String> trailerList = new ArrayList<>();

        //build the url to download the trailer list from TMDB
        url = buildUrlTrailerList(api_key, movieID);
        Log.i("denis buildUrl", "buildAndDownloadTrailerList() url: " + url.toString());

        //Download JSON of Trailers
        try {
            String jsonTMDBTrailerList = getResponseFromHttpUrl(url);
            jsonObject = new JSONObject(jsonTMDBTrailerList);

            //getting the Videos JSONArray from JSONObject
            jsonArray = jsonObject.getJSONArray(JSON_REVIEW_RESULTS);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("denis ", "jsonArray: "+jsonArray.toString());
        //for all videos in the JSON get get the Key and store in trailerList var
        for(int index=0; index<jsonArray.length(); index++){
            try{
                trailerList.add(parseJSONGetTrailerList(jsonArray.getJSONObject(index)));
                Log.i("denis Reviews", "trailerList posicao "+index+" armazenou: "+trailerList.get(index));
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        return trailerList;
    }

    private static String parseJSONGetTrailerList(JSONObject jsonObject) {
        String reviewInfo = new String();
        //jsonObjectReviews = jsonArray.getJSONObject(index);
        try {

            //if the video parsed is a Youtube Video
//            String string1 = new String(jsonObject.getString(JSON_VIDEOS_SITE));
//            Log.i("denis parse trailer","string1: "+ string1+" youtube: "+JSON_VIDEOS_YOUTUBE);
            if (jsonObject.getString(JSON_VIDEOS_SITE).equals(JSON_VIDEOS_YOUTUBE)){

                //get the video key
                //key is the youtube video ID https://www.youtube.com/watch?v=VIDEO_ID
                reviewInfo = jsonObject.getString(JSON_VIDEOS_KEY);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reviewInfo;
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

    public static boolean isNetworkConnected(Context activityContext) {
        ConnectivityManager cm = (ConnectivityManager) activityContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
