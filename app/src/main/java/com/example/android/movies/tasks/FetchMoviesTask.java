package com.example.android.movies.tasks;

import android.content.Context;
import android.os.AsyncTask;
import com.example.android.movies.models.Movie;
import com.example.android.movies.utilities.NetworkUtils;
import com.example.android.movies.utilities.TheMovieDBJsonUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

    private AsyncTaskCompleteListener<List<Movie>> listener;
    private Context mContext;

    public FetchMoviesTask(Context context, AsyncTaskCompleteListener<List<Movie>> listener) {
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
       listener.onTaskInitialisation();
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        String sortByParameter = params[0];
        URL movieRequestUrl = NetworkUtils.buildMoviesUrl(sortByParameter);

        try {
            String jsonMovieResponse = NetworkUtils
                    .getResponseFromHttpUrl(movieRequestUrl);

            ArrayList<Movie> simpleJsonMovieData = (ArrayList<Movie>) TheMovieDBJsonUtils
                    .getMovieObjectsFromJson(this.mContext, jsonMovieResponse);

            return simpleJsonMovieData;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Movie> movieObjects) {
        listener.onTaskComplete(movieObjects);
    }
}
