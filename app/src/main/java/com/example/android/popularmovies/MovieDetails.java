package com.example.android.popularmovies;

class MovieDetails {

    private final int voteCount;
    private final int id;
    private final Double voteAvarage;
    private final String title;
    private final String posterPath;
    private final String overview;
    private final String releaseDate;
    private final String[] trailersList;


    MovieDetails(int voteCount, int id, Double voteAvarage,
                 String title, String posterPath,
                 String overview, String releaseDate, String[] trailersList){

        this.voteCount = voteCount;
        this.id = id;
        this.voteAvarage = voteAvarage;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.trailersList = trailersList;
    }

    int getVoteCount() {
        return voteCount;
    }

    public int getId() {
        return id;
    }

    Double getVoteAvarage() {
        return voteAvarage;
    }

    String getTitle() {
        return title;
    }

    String getPosterPath(){
        return posterPath;
    }

    String getOverview() {
        return overview;
    }

    String getReleaseDate() {
        return releaseDate;
    }

    public String[] getTrailersList() {
        return trailersList;
    }


}
