package com.shaftapps.pglab.popularmovies;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
            movieData = (MovieData) arguments.getSerializable(MovieData.EXTRA_KEY);

            Glide.with(getActivity())
                    .load(movieData.posterUrl)
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
