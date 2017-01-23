package com.example.android.movies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movies.models.Movie;
import com.example.android.movies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * This Activity is responsible for the clicked movie's details screen
 */
public class DetailActivity extends AppCompatActivity {

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private TextView mMovieTitleTextView;
    private ImageView mMovieImageView;
    private TextView mMovieReleaseYearTextView;
    private TextView mMovieUserRating;
    private TextView mMovieSynopsis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //get the data from the intent's extras
        Bundle data = getIntent().getExtras();
        //get the movie that was clicked
        Movie movie = (Movie) data.getParcelable("selectedMovie");
        //set the title to the appropriate textview.
        mMovieTitleTextView = (TextView) findViewById(R.id.tv_movie_title);
        mMovieImageView = (ImageView) findViewById(R.id.iv_movie_poster_thumb);
        mMovieReleaseYearTextView = (TextView) findViewById(R.id.tv_movie_release_year);
        mMovieUserRating = (TextView) findViewById(R.id.tv_movie_user_rating);
        mMovieSynopsis = (TextView) findViewById(R.id.tv_movie_synopsis);

        mMovieTitleTextView.setText(movie.getOriginalTitle());
        Picasso.with(getApplicationContext()).load(NetworkUtils.MOVIES_POSTER_BASE_URL + "w185/" + movie.getPosterPathUrl()).into(mMovieImageView);
        mMovieReleaseYearTextView.setText(movie.getReleaseDate());
        mMovieUserRating.setText(movie.getUserRating());
        mMovieSynopsis.setText(movie.getSynopsis());
    }
}