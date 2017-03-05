package com.example.android.movies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.android.movies.R;
import com.example.android.movies.models.Trailer;

import java.util.List;


public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {
    private List<Trailer> mTrailerData;
    Context context;

    private TrailerAdapterOnClickHandler mClickHandler;

    /**
     * Creates a TrailerAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a settings list item.
     */
    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //        public final TextView mMovieTextView;
        public final Button mTrailerNameBtn;

        public TrailerAdapterViewHolder(View view) {
            super(view);
            mTrailerNameBtn = (Button) view.findViewById(R.id.bt_movie_trailer_name);
            mTrailerNameBtn.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Trailer selectedTrailer = mTrailerData.get(adapterPosition);
            mClickHandler.onClick(selectedTrailer);
        }
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer selectedTrailer);
    }

    @Override
    public TrailerAdapter.TrailerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        this.context = viewGroup.getContext();
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new TrailerAdapter.TrailerAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param trailerAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder trailerAdapterViewHolder, int position) {
        String trailerName = mTrailerData.get(position).getName();
        trailerAdapterViewHolder.mTrailerNameBtn.setText(trailerName);
        //Picasso.with(this.context).load(NetworkUtils.MOVIES_POSTER_BASE_URL + "w185/" + moviePoster).into(trailerAdapterViewHolder.mTrailerName);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our settings
     */
    @Override
    public int getItemCount() {
        if (null == mTrailerData) return 0;
        return mTrailerData.size();
    }

    /**
     * This method is used to set the weather settings on a MovieAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new MovieAdapter to display it.
     *
     * @param trailersData The new weather data to be displayed.
     */
    public void setTrailersData(List<Trailer> trailersData) {
        mTrailerData = trailersData;
        notifyDataSetChanged();
    }
}
