package com.example.android.movies.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import com.example.android.movies.models.Trailer;
import com.example.android.movies.utilities.NetworkUtils;
import com.example.android.movies.utilities.TheMovieDBJsonUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class TrailerLoader extends AsyncTaskLoader<List<Trailer>> {

    private AsyncTaskLoaderCompleteListener<List<Trailer>> mAsyncTaskLoaderCompleteListener;
    List<Trailer> trailers = null;
    Bundle args;
    Context mContext;

    public TrailerLoader(Context context, AsyncTaskLoaderCompleteListener listener, Bundle args) {
        super(context);
        this.mContext = context;
        this.mAsyncTaskLoaderCompleteListener = listener;
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        mAsyncTaskLoaderCompleteListener.onTaskInitialisation();
        if (trailers != null) {
            deliverResult(trailers);
        } else {

            forceLoad();
        }
    }

    @Override
    public List<Trailer> loadInBackground() {
        String additionalURLPath = null;
        int movieId = 0;

        if(args.getString("path") != null)
            additionalURLPath = args.getString("path");
        if(args.getInt("movieId") != -1)
            movieId = args.getInt("movieId");
        try {
            URL movieRequestUrl = NetworkUtils.buildMovieDetailsUrl(additionalURLPath, movieId);

            try {
                String jsonTrailersResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                ArrayList<Trailer> simpleJsonTrailersData = (ArrayList<Trailer>) TheMovieDBJsonUtils
                        .getTrailerObjectsFromJson(mContext, jsonTrailersResponse);

                return simpleJsonTrailersData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
