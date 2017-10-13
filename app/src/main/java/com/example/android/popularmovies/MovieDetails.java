package com.example.android.popularmovies;

class MovieDetails {

    private final int voteCount;
    private final int id;
    private final Double voteAvarage;
    private final String title;
    private final String posterPath;
    private final String overview;
    private final String releaseDate;


    MovieDetails(int voteCount, int id, Double voteAvarage,
                 String title, String posterPath,
                 String overview, String releaseDate){

        this.voteCount = voteCount;
        this.id = id;
        this.voteAvarage = voteAvarage;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public int getId() {
        return id;
    }

    public Double getVoteAvarage() {
        return voteAvarage;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath(){
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }


}
