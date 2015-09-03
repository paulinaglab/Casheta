package com.shaftapps.pglab.popularmovies;


import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by Paulina on 2015-08-30.
 */
public class DetailFragment extends Fragment {

    private MovieData movieData;
    private TextView titleTextView;
    private TextView originalTitleTextView;
    private TextView rateTextView;
    private TextView overviewTextView;
    private TextView releaseDate;
    private ImageView posterImageView;
    private ImageView photoImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Setting height to layout wrapper
        final ViewGroup wrapper = (ViewGroup) fragmentView.findViewById(R.id.detail_ratio_wrapper);
        ViewTreeObserver viewTreeObserver = wrapper.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    wrapper.setLayoutParams(new LinearLayout.LayoutParams(wrapper.getWidth(), wrapper.getWidth()));
//                    wrapper.invalidate();
                }

                ViewTreeObserver viewTreeObserver = wrapper.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this);
                } else {
                    viewTreeObserver.removeGlobalOnLayoutListener(this);
                }
            }
        });

        // Fields initialization
        titleTextView = (TextView) fragmentView.findViewById(R.id.detail_movie_title);
        originalTitleTextView = (TextView) fragmentView.findViewById(R.id.detail_movie_original_title);
        rateTextView = (TextView) fragmentView.findViewById(R.id.detail_rate_text_view);
        overviewTextView = (TextView) fragmentView.findViewById(R.id.detail_overview);
        releaseDate = (TextView) fragmentView.findViewById(R.id.detail_release_date);
        posterImageView = (ImageView) fragmentView.findViewById(R.id.detail_poster_image);
        photoImageView = (ImageView) fragmentView.findViewById(R.id.detail_photo_image);

        Bundle arguments = getArguments();
        if (arguments != null) {
            movieData = arguments.getParcelable(MovieData.EXTRA_KEY);

            Glide.with(getActivity())
                    .load(movieData.posterUrl)
                    .fitCenter()
                    .placeholder(R.color.grid_placeholder_bg)
                    .into(posterImageView);

            Glide.with(getActivity())
                    .load(movieData.photoUrl)
                    .fitCenter()
                    .into(photoImageView);

            titleTextView.setText(movieData.title);
            originalTitleTextView.setText(movieData.originalTitle);
            rateTextView.setText(
                    getString(R.string.details_rate_format, movieData.averageRate));
            overviewTextView.setText(movieData.overview);
            releaseDate.setText(
                    getString(R.string.details_release_date_label, movieData.releaseDate));
        }

        return fragmentView;
    }

}
