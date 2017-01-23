package com.example.android.movies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class describing a movie object
 */

public class Movie implements Parcelable {
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

    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        posterPathUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.title);
        parcel.writeString(this.posterPathUrl);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
