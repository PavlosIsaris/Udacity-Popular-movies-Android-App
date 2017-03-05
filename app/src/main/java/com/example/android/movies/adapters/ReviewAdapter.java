package com.example.android.movies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.movies.R;
import com.example.android.movies.models.Review;
import com.example.android.movies.models.Trailer;

import java.util.List;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {
    private List<Review> mReviewData;
    Context context;


    /**
     * Cache of the children views for a settings list item.
     */
    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView mReviewContentText;
        public final TextView mReviewAuthorText;

        public ReviewAdapterViewHolder(View view) {
            super(view);
            mReviewContentText = (TextView) view.findViewById(R.id.tv_review_content);
            mReviewAuthorText = (TextView) view.findViewById(R.id.tv_review_author);
        }

    }



    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        this.context = viewGroup.getContext();
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ReviewAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param reviewAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder reviewAdapterViewHolder, int position) {
        String reviewContent = mReviewData.get(position).getContent();
        String reviewAuthor = mReviewData.get(position).getAuthor();
        reviewAdapterViewHolder.mReviewContentText.setText(reviewContent);
        reviewAdapterViewHolder.mReviewAuthorText.setText(reviewAuthor);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our settings
     */
    @Override
    public int getItemCount() {
        if (null == mReviewData) return 0;
        return mReviewData.size();
    }

    /**
     * This method is used to set the weather settings on a MovieAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new MovieAdapter to display it.
     *
     * @param reviewsData
     */
    public void setReviewsData(List<Review> reviewsData) {
        mReviewData = reviewsData;
        notifyDataSetChanged();
    }
}
