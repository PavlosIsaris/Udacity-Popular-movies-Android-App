package com.example.android.movies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.android.movies.models.Movie;

/**
 * This Activity is responsible for the clicked movie's details screen
 */
public class DetailActivity extends AppCompatActivity {

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private TextView mMovieTitleTextView;

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
        mMovieTitleTextView.setText(movie.getTitle());
    }
}