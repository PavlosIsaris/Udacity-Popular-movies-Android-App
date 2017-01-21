package com.example.android.movies.models;

/**
 * Class describing a movie object
 */

public class Movie {
    // id of the movie
    private int id;
    //title of the movie
    private String title;
    //poster path of the movie
    private String posterPathUrl;

    //getter method for movie poster file path
    public String getPosterPathUrl() {
        return posterPathUrl;
    }

    //getter method for id
    public int getId() {
        return id;
    }

    //getter method for title
    public String getTitle() {
        return title;
    }

    //default movie constructor
    public Movie(int id, String title, String posterPathUrl) {
        this.id = id;
        this.title = title;
        this.posterPathUrl = posterPathUrl;
    }
}
