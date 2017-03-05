package com.example.android.movies.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.movies.models.Review;
import com.example.android.movies.models.Trailer;
import com.example.android.movies.utilities.NetworkUtils;
import com.example.android.movies.utilities.TheMovieDBJsonUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ReviewLoader extends AsyncTaskLoader<List<Review>> {

    private AsyncTaskLoaderCompleteListener<List<Review>> mAsyncTaskLoaderCompleteListener;
    List<Review> reviews = null;
    Bundle args;
    Context mContext;

    public ReviewLoader(Context context, AsyncTaskLoaderCompleteListener listener, Bundle args) {
        super(context);
        this.mContext = context;
        this.mAsyncTaskLoaderCompleteListener = listener;
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        mAsyncTaskLoaderCompleteListener.onTaskInitialisation();
        if (reviews != null) {
            deliverResult(reviews);
        } else {

            forceLoad();
        }
    }

    @Override
    public List<Review> loadInBackground() {
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

                ArrayList<Review> simpleJsonReviewsData = (ArrayList<Review>) TheMovieDBJsonUtils
                        .getReviewObjectsFromJson(mContext, jsonTrailersResponse);

                return simpleJsonReviewsData;

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
