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

import android.net.Uri;
import android.util.Log;

import com.example.android.movies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the movie DB servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIES_DB_URL =
            "https://api.themoviedb.org/3/movie/";
    public static final String MOVIES_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";

    private static final String BASE_URL = MOVIES_DB_URL;

    final static String API_KEY_PARAM = "api_key";
    final static String LANGUAGE_PARAM = "language";

    /**
     * Builds the URL used to talk to the movies server using a sort by term.
     *
     * @param subPath The term that will be queried for (popularity or rating).
     * @return The URL to use to query the movies server.
     */
    public static URL buildUrl(String subPath) {
        //read the api key from a config declared in build.gradle file
        String apiKey = BuildConfig.THE_MOVIE_DB_API_TOKEN;
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(subPath)
                .appendQueryParameter(LANGUAGE_PARAM, "en")
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}