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
package com.example.android.movies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.movies.adapters.MovieAdapter;
import com.example.android.movies.adapters.MovieAdapter.MovieAdapterOnClickHandler;
import com.example.android.movies.db.MovieContract;
import com.example.android.movies.db.MovieDBHelper;
import com.example.android.movies.models.Movie;
import com.example.android.movies.utilities.NetworkUtils;
import com.example.android.movies.utilities.TheMovieDBJsonUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<Movie>> {

    private RecyclerView mMoviesRecyclerView;
    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    private SQLiteDatabase mDb;

    private GridLayoutManager moviesGridLayoutManager;

    private String CURRENT_SCROLL_POSITION = "CURRENT_SCROLL_POSITION";

    private int currentScrollPosition = 0;

    private Context mContext;

    private String PREFERRED_MOVIE_SORT_NAME = "preferredMovieSortName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_movies_list);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mMoviesRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        moviesGridLayoutManager = new GridLayoutManager(this, calculateNoOfColumns(this), GridLayoutManager.VERTICAL, false);

        mMoviesRecyclerView.setLayoutManager(moviesGridLayoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mMoviesRecyclerView.setHasFixedSize(true);

        /*
         * The MovieAdapter is responsible for linking our weather data with the Views that
         * will end up displaying our weather data.
         */
        mMovieAdapter = new MovieAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mMoviesRecyclerView.setAdapter(mMovieAdapter);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /* Once all of our views are setup, we can load the weather data. */
        loadMoviesData(getPreferredMoviesSortTerm());

        // Create a DB helper (this will create the DB if run for the first time)
        MovieDBHelper dbHelper = new MovieDBHelper(this);

        // Keep a reference to the mDb until paused or killed. Get a writable database
        // because you will be adding restaurant customers
        mDb = dbHelper.getReadableDatabase();
    }

    private String getPreferredMoviesSortTerm() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                getString(R.string.preference_movies_sort_term), Context.MODE_PRIVATE);
        return sharedPref.getString(PREFERRED_MOVIE_SORT_NAME, "popular");
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

    // Save the scroll position
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println("onSaveInstanceState");
        outState.putInt(CURRENT_SCROLL_POSITION, moviesGridLayoutManager.findFirstVisibleItemPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        System.out.println("onRestoreInstanceState");
        if (savedInstanceState != null) {
            currentScrollPosition = savedInstanceState.getInt(CURRENT_SCROLL_POSITION, 0);
            mMoviesRecyclerView.smoothScrollToPosition(currentScrollPosition);
        }
    }

    private static final int MOVIES_LOADER_ID = 0;

    /**
     * This method will get the user's preferred location for weather, and then tell some
     * background method to get the weather data in the background.
     */
    private void loadMoviesData(String sortByTerm) {

        if(sortByTerm.equals("favorites")) {
            getFavoriteMoviesFromDB();
            return;
        }

        int loaderId = MOVIES_LOADER_ID;

        /*
         * From MainActivity, we have implemented the LoaderCallbacks interface with the type of
         * String array. (implements LoaderCallbacks<String[]>) The variable callback is passed
         * to the call to initLoader below. This means that whenever the loaderManager has
         * something to notify us of, it will do so through this callback.
         */
        LoaderManager.LoaderCallbacks<List<Movie>> callback = MainActivity.this;

        /*
         * The second parameter of the initLoader method below is a Bundle. Optionally, you can
         * pass a Bundle to initLoader that you can then access from within the onCreateLoader
         * callback. In our case, we don't actually use the Bundle, but it's here in case we wanted
         * to.
         */
        Bundle bundleForLoader = new Bundle();
        bundleForLoader.putString("sortByParam", sortByTerm);

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);
    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks.
     *
     * @param selectedMovie The movie id of the movie item that was selected
     */
    @Override
    public void onClick(Movie selectedMovie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("selectedMovie", selectedMovie);
        startActivity(intentToStartDetailActivity);
    }

    /**
     * This method will make the View for the weather data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showMoviesDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mMoviesRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the weather
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mMoviesRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(this) {

            List<Movie> moviesData = null;
            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                if (moviesData != null) {
                    deliverResult(moviesData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from OpenWeatherMap in the background.
             *
             * @return Weather data from OpenWeatherMap as an array of Strings.
             *         null if an error occurs
             */
            @Override
            public List<Movie> loadInBackground() {
                String sortByParameter = "popular";

                if(args.getString("sortByParam") != null)
                    sortByParameter = args.getString("sortByParam");
                try {
                    URL movieRequestUrl = NetworkUtils.buildMoviesUrl(sortByParameter);

                    try {
                        String jsonMovieResponse = NetworkUtils
                                .getResponseFromHttpUrl(movieRequestUrl);

                        ArrayList<Movie> simpleJsonMovieData = (ArrayList<Movie>) TheMovieDBJsonUtils
                                .getMovieObjectsFromJson(MainActivity.this, jsonMovieResponse);

                        return simpleJsonMovieData;

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(List<Movie> data) {
                moviesData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null) {
            showMoviesDataView();
            mMovieAdapter.setMoviesData(data);
            // Scroll back to previous position
            mMoviesRecyclerView.smoothScrollToPosition(currentScrollPosition);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.settings, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String sortByTerm = null;
        if (id == R.id.action_sort_by_popularity) {
            sortByTerm = "popular";
            saveToPreferences(sortByTerm);
            return getDataByTerm();
        } else if(id == R.id.action_sort_by_rating) {
            sortByTerm = "top_rated";
            saveToPreferences(sortByTerm);
            return getDataByTerm();
        } else if(id == R.id.action_favorites) {
            sortByTerm = "favorites";
            saveToPreferences(sortByTerm);
            getFavoriteMoviesFromDB();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveToPreferences(String movieSortPreference) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                getString(R.string.preference_movies_sort_term), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PREFERRED_MOVIE_SORT_NAME, movieSortPreference);
        editor.commit();
    }

    private boolean getDataByTerm() {
        String sortByTerm = getPreferredMoviesSortTerm();
        if (sortByTerm.equals("favourite")) {
            getFavoriteMoviesFromDB();
            return true;
        }
        else {
            invalidateData();
            Bundle bundleForLoader = new Bundle();
            bundleForLoader.putString("sortByParam", sortByTerm);
            getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, bundleForLoader, this);
        }
        return true;
    }

    /**
     * This method is used when we are resetting data, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing.
     */
    private void invalidateData() {
        mMovieAdapter.setMoviesData(null);
    }

    public void getFavoriteMoviesFromDB() {
        // Get all movie objects from the database and save in a cursor
        Cursor cursor = getAllMovies();
        ArrayList<Movie> favoriteMovies = new ArrayList<Movie>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            favoriteMovies.add(new Movie(
                    cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)),
                    cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH_URL)),
                    cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE)),
                    cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_SYNOPSIS)),
                    cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_USER_RATING)),
                    cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)))); //add the item
            cursor.moveToNext();
        }

        // Link the adapter to the RecyclerView
        mMovieAdapter.setMoviesData(favoriteMovies);
    }

    /**
     * Query the mDb and get all guests from the waitlist table
     *
     * @return Cursor containing the list of guests
     */
    private Cursor getAllMovies() {
        return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                MovieContract.MovieEntry.COLUMN_TIMESTAMP + " DESC");
    }
}