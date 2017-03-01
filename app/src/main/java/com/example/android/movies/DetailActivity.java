package com.example.android.movies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movies.adapters.TrailerAdapter;
import com.example.android.movies.db.MovieContract;
import com.example.android.movies.db.MovieDbHelper;
import com.example.android.movies.loaders.AsyncTaskLoaderCompleteListener;
import com.example.android.movies.loaders.TrailerLoader;
import com.example.android.movies.models.Movie;
import com.example.android.movies.models.Trailer;
import com.example.android.movies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * This Activity is responsible for the clicked movie's details screen
 */
public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler, LoaderManager.LoaderCallbacks<List<Trailer>>, AsyncTaskLoaderCompleteListener<List<Trailer>>, View.OnClickListener {

    private TextView mMovieTitleTextView;
    private ImageView mMovieImageView;
    private TextView mMovieReleaseYearTextView;
    private TextView mMovieUserRating;
    private TextView mMovieSynopsis;
    private Movie mCurrentMovie;
    private Button mFavoriteMovieBtn;

    private SQLiteDatabase mDb;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    private RecyclerView mRecyclerView;
    private TrailerAdapter mTrailerAdapter;

    private static final int TRAILERS_LOADER_ID = 1;

    private boolean movieExistsInFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDB();
        setContentView(R.layout.activity_detail);
        //get the data from the intent's extras
        Bundle data = getIntent().getExtras();
        //get the movie that was clicked
        Movie movie = (Movie) data.getParcelable("selectedMovie");
        mCurrentMovie = movie;
        assert movie != null;

        mFavoriteMovieBtn = (Button) findViewById(R.id.bt_movie_favorite);
        mFavoriteMovieBtn.setOnClickListener(this);
        this.setUpMovieDetails(movie);
        this.setUpMovieTrailersUI();
    }

    private void initDB() {
        // Create a DB helper (this will create the DB if run for the first time)
        MovieDbHelper dbHelper = new MovieDbHelper(this);

        // Keep a reference to the mDb until paused or killed. Get a writable database
        // because you will be adding restaurant customers
        mDb = dbHelper.getWritableDatabase();
    }

    private void setUpMovieDetails(Movie movie) {
        mMovieTitleTextView = (TextView) findViewById(R.id.tv_movie_title);
        mMovieImageView = (ImageView) findViewById(R.id.iv_movie_poster_thumb);
        mMovieReleaseYearTextView = (TextView) findViewById(R.id.tv_movie_release_year);
        mMovieUserRating = (TextView) findViewById(R.id.tv_movie_user_rating);
        mMovieSynopsis = (TextView) findViewById(R.id.tv_movie_synopsis);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_trailers_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_trailers_loading_indicator);

        mMovieTitleTextView.setText(movie.getOriginalTitle());
        Picasso.with(getApplicationContext()).load(NetworkUtils.MOVIES_POSTER_BASE_URL + "w185/" + movie.getPosterPathUrl()).into(mMovieImageView);
        mMovieReleaseYearTextView.setText(movie.getReleaseDate());
        String userRatingFull = movie.getUserRating() + getString(R.string.divideByTenString);
        mMovieUserRating.setText(userRatingFull);
        mMovieSynopsis.setText(movie.getSynopsis());

        setAppropriateFavoriteMovieBtnText();
    }

    private void setAppropriateFavoriteMovieBtnText() {
        Cursor cursor = getMovieById(mCurrentMovie.getId());
        //if the movie does not exist in the DB, the cursor is empty
        if(cursor.getCount() == 0) {
            movieExistsInFavorites = false;
            mFavoriteMovieBtn.setText(R.string.favoriteMovieBtnText);
        } else {
            movieExistsInFavorites = true;
            mFavoriteMovieBtn.setText(R.string.remove_from_favorites);
        }
    }

    private void setUpMovieTrailersUI() {
         /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie_trailers);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * The MovieAdapter is responsible for linking our weather data with the Views that
         * will end up displaying our weather data.
         */
        mTrailerAdapter = new TrailerAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mTrailerAdapter);

        this.loadTrailers();
    }

    private void loadTrailers() {
        int loaderId = TRAILERS_LOADER_ID;
        LoaderManager.LoaderCallbacks<List<Trailer>> callback = DetailActivity.this;

        Bundle bundleForLoader = new Bundle();
        bundleForLoader.putInt("movieId", mCurrentMovie.getId());
        bundleForLoader.putString("path", "videos");
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);
    }

    @Override
    public void onClick(Trailer selectedTrailer) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + selectedTrailer.getKey())));
    }

    @Override
    public Loader<List<Trailer>> onCreateLoader(int id, final Bundle args) {
        return new TrailerLoader(this, this, args);
    }

    @Override
    public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null) {
            showTrailersDataView();
            mTrailerAdapter.setTrailersData(data);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Trailer>> loader) {

    }

    /**
     * This method will make the View for the weather data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showTrailersDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
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
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTaskComplete(List<Trailer> data) {

    }

    @Override
    public void onTaskInitialisation() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.bt_movie_favorite:
                String toastMessage;
                if(movieExistsInFavorites) {
                    removeMovieFromFavorites(mCurrentMovie.getId());
                    toastMessage = "Removed from favorites";
                    setAppropriateFavoriteMovieBtnText();
                } else {
                    saveMovieToFavorites();
                    toastMessage = "Marked as favorite";
                    setAppropriateFavoriteMovieBtnText();
                }
                Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
                break;
        }
    }

    private boolean saveMovieToFavorites() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mCurrentMovie.getId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, mCurrentMovie.getTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, mCurrentMovie.getOriginalTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, mCurrentMovie.getSynopsis());
        contentValues.put(MovieContract.MovieEntry.COLUMN_USER_RATING, mCurrentMovie.getUserRating());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mCurrentMovie.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH_URL, mCurrentMovie.getPosterPathUrl());

        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

        return uri != null;
    }

    /**
     * Removes the record with the specified id
     *
     * @param movieId the DB id to be removed (movie id)
     * @return True: if removed successfully, False: if failed
     */
    private void removeMovieFromFavorites(int movieId) {

        // Build appropriate uri with String row id appended
        String stringId = Integer.toString(movieId);
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();

        // COMPLETED (2) Delete a single row of data using a ContentResolver
        getContentResolver().delete(uri, null, null);
    }

    /**
     * Query the mDb and get all guests from the waitlist table
     *
     * @return Cursor containing the list of guests
     */
    private Cursor getMovieById(int movieId) {
        String stringId = Integer.toString(movieId);
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();

        return getContentResolver().query(uri,
                null,
                null,
                null,
                null);
    }
}