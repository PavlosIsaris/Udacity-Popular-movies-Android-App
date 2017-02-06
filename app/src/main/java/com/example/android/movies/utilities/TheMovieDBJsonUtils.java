/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.movies.utilities;

import android.content.Context;

import com.example.android.movies.models.Movie;
import com.example.android.movies.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions to handle TheMovieDB JSON data.
 */
public final class TheMovieDBJsonUtils {

    /**
     * This method parses JSON from a web response and returns an array of Movie instances.
     * <p/>
     *
     * @param moviesJsonStr JSON response from server
     *
     * @return Array of Strings describing weather data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static List<Movie> getMovieObjectsFromJson(Context context, String moviesJsonStr)
            throws JSONException {

        /* Weather information. Each day's settings info is an element of the "list" array */
        final String MOVIE_LIST = "results";

        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "title";
        final String MOVIE_POSTER_PATH = "poster_path";
        final String MOVIE_ORIGINAL_TITLE = "original_title";
        final String MOVIE_SYNOPSIS = "overview";
        final String MOVIE_USER_RATING = "vote_average";
        final String MOVIE_RELEASE_DATE = "release_date";

        /* String array to hold each day's weather String */
        List<Movie> parsedMovieData;

        JSONObject movieJson = new JSONObject(moviesJsonStr);

        JSONArray movieArray = movieJson.getJSONArray(MOVIE_LIST);

        parsedMovieData = new ArrayList<>();

        for (int i = 0; i < movieArray.length(); i++) {

            /* Get the JSON object representing the current movie */
            JSONObject movieJSONObj = movieArray.getJSONObject(i);

            int movieId = movieJSONObj.getInt(MOVIE_ID);
            String movieTitle = movieJSONObj.getString(MOVIE_TITLE);
            String moviePosterPath = movieJSONObj.getString(MOVIE_POSTER_PATH);
            String movieOriginalTitle = movieJSONObj.getString(MOVIE_ORIGINAL_TITLE);
            String movieSynopsis = movieJSONObj.getString(MOVIE_SYNOPSIS);
            String movieUserRating = movieJSONObj.getString(MOVIE_USER_RATING);
            String movieReleaseDate = movieJSONObj.getString(MOVIE_RELEASE_DATE);
            //add to the data list a new Movie instance
            parsedMovieData.add(i, new Movie(movieId, movieTitle, moviePosterPath, movieOriginalTitle, movieSynopsis, movieUserRating, movieReleaseDate));
        }

        return parsedMovieData;
    }

    public static List<Trailer> getTrailerObjectsFromJson(Context context, String moviesJsonStr)
            throws JSONException {

        /* Weather information. Each day's settings info is an element of the "list" array */
        final String MOVIE_LIST = "results";

        final String TRAILER_ID = "id";
        final String TRAILER_NAME = "name";
        final String TRAILER_KEY = "key";
        final String TRAILER_SITE = "site";

        /* String array to hold each day's weather String */
        List<Trailer> parsedTrailerData;

        JSONObject trailerJson = new JSONObject(moviesJsonStr);

        JSONArray trailerArray = trailerJson.getJSONArray(MOVIE_LIST);

        parsedTrailerData = new ArrayList<>();

        for (int i = 0; i < trailerArray.length(); i++) {

            /* Get the JSON object representing the current movie */
            JSONObject movieJSONObj = trailerArray.getJSONObject(i);

            String trailerId = movieJSONObj.getString(TRAILER_ID);
            String trailerName = movieJSONObj.getString(TRAILER_NAME);
            String trailerKey = movieJSONObj.getString(TRAILER_KEY);
            String trailerSite = movieJSONObj.getString(TRAILER_SITE);
            //add to the data list a new Movie instance
            parsedTrailerData.add(i, new Trailer(trailerId, trailerName, trailerKey, trailerSite));
        }

        return parsedTrailerData;
    }
}