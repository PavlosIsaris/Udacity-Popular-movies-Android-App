package com.example.android.movies.db;


import android.provider.BaseColumns;

public class MovieContract {

    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_USER_RATING = "user_rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH_URL = "poster_path_url";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
