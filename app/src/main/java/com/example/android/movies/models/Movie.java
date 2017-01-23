package com.example.android.movies.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.movies.utilities.DateUtils;

/**
 * Class describing a movie object
 */

public class Movie implements Parcelable {
    // id of the movie
    private int id;
    //title of the movie
    private String title;
    //original title of the movie
    private String originalTitle;
    //synopsis of the movie
    private String synopsis;
    //user rating
    private String userRating;
    //release date
    private String releaseDate;
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

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    //default movie constructor
    public Movie(int id, String title, String posterPathUrl, String originalTitle, String synopsis, String userRating, String releaseDate) {
        DateUtils dateUtils = new DateUtils();
        this.id = id;
        this.title = title;
        this.posterPathUrl = posterPathUrl;
        this.originalTitle = originalTitle;
        this.synopsis = synopsis;
        this.userRating = userRating;
        this.releaseDate = dateUtils.getYearFromStringDate(releaseDate);
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        posterPathUrl = in.readString();
        originalTitle = in.readString();
        synopsis = in.readString();
        userRating = in.readString();
        releaseDate = in.readString();
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
        parcel.writeString(this.originalTitle);
        parcel.writeString(this.synopsis);
        parcel.writeString(this.userRating);
        parcel.writeString(this.releaseDate);
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
