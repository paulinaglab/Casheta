package com.shaftapps.pglab.popularmovies.fragment;


import android.app.Activity;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.shaftapps.pglab.popularmovies.Keys;
import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.data.MovieContract;
import com.shaftapps.pglab.popularmovies.util.ColorUtils;
import com.shaftapps.pglab.popularmovies.widget.NotifyingScrollView;

/**
 * Fragment with details of specific movie.
 * <p/>
 * Created by Paulina on 2015-08-30.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int MOVIE_LOADER_ID = 1;

    private static final String[] MOVIE_PROJECTION = new String[]{
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_AVERAGE_RATE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER_URL,
            MovieContract.MovieEntry.COLUMN_BACKDROP_URL};

    private Uri uri;

    private Cursor movieCursor;

    private OnScrollChangedListener onScrollChangedListener;

    private ViewGroup ratioWrapper;
    private View titlesWrapper;
    private View rateWrapper;
    private NotifyingScrollView notifyingScrollView;
    private TextView titleTextView;
    private TextView originalTitleTextView;
    private TextView rateTextView;
    private TextView overviewTextView;
    private TextView releaseDate;
    private ImageView posterImageView;
    private ImageView photoImageView;

    private int generatedColor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null)
            uri = getArguments().getParcelable(Keys.SELECTED_MOVIE_URI);

        View fragmentView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Fields initialization
        initFields(fragmentView);

        // Setting height to layout ratioWrapper
        fitRatioWrapperHeight();

        // Setting data from arguments
        insertDataIntoUI();

        // Setting ScrollView listener
        initScrollViewListener();

        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onScrollChangedListener = (OnScrollChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnScrollChangedListener");
        }
    }

    private void initFields(View fragmentView) {
        ratioWrapper = (ViewGroup) fragmentView.findViewById(R.id.detail_ratio_wrapper);
        titlesWrapper = fragmentView.findViewById(R.id.detail_titles_wrapper);
        rateWrapper = fragmentView.findViewById(R.id.detail_rate_wrapper);
        notifyingScrollView = (NotifyingScrollView)
                fragmentView.findViewById(R.id.detail_notifying_scroll_view);
        titleTextView = (TextView) fragmentView.findViewById(R.id.detail_movie_title);
        originalTitleTextView = (TextView) fragmentView.findViewById(R.id.detail_movie_original_title);
        rateTextView = (TextView) fragmentView.findViewById(R.id.detail_rate_text_view);
        overviewTextView = (TextView) fragmentView.findViewById(R.id.detail_overview);
        releaseDate = (TextView) fragmentView.findViewById(R.id.detail_release_date);
        posterImageView = (ImageView) fragmentView.findViewById(R.id.detail_poster_image);
        photoImageView = (ImageView) fragmentView.findViewById(R.id.detail_photo_image);
    }


    /**
     * This method fits height of top part layout (with title, poster and backdrop).
     */
    private void fitRatioWrapperHeight() {
        ViewTreeObserver viewTreeObserver = ratioWrapper.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int screenHeight = getResources().getDisplayMetrics().heightPixels -
                        getResources().getDimensionPixelSize(R.dimen.status_bar_height);
                ratioWrapper.setLayoutParams(new LinearLayout.LayoutParams(
                        ratioWrapper.getWidth(),
                        Math.min(ratioWrapper.getWidth(), screenHeight)));

                ViewTreeObserver viewTreeObserver = ratioWrapper.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this);
                } else {
                    viewTreeObserver.removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    private void initScrollViewListener() {
        notifyingScrollView.setOnScrollChangedListener(new NotifyingScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                if (onScrollChangedListener != null) {
                    onScrollChangedListener.onScrollChanged(ratioWrapper.getHeight(), generatedColor, t);
                }
            }
        });
    }

    private void insertDataIntoUI() {
        if (movieCursor != null && movieCursor.moveToFirst()) {

            // Setting text data
            titleTextView.setText(
                    movieCursor.getString(movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));

            originalTitleTextView.setText(
                    movieCursor.getString(movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE)));

            rateTextView.setText(getString(R.string.details_rate_format,
                    movieCursor.getDouble(movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_AVERAGE_RATE))));

            overviewTextView.setText(
                    movieCursor.getString(movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));

            releaseDate.setText(getString(R.string.details_release_date_label,
                    movieCursor.getString(movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE))));

            // Loading images and color generation
            loadImagesAndColors();
        }
    }

    private void loadImagesAndColors() {
        // Creating listener for palette
        final Palette.PaletteAsyncListener paletteAsyncListener = new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                generatedColor = palette.getDarkVibrantColor(
                        ContextCompat.getColor(getActivity(), R.color.details_rate_not_initialized_bg));
                titlesWrapper.setBackgroundColor(generatedColor);

                rateWrapper.setBackgroundColor(
                        ColorUtils.getColorWithTranslateBrightness(generatedColor, -20));
            }
        };

        // Creating Glide's object, which allows starting Palette generation when the image is loaded
        GlideDrawableImageViewTarget posterGlideDrawable = new GlideDrawableImageViewTarget(posterImageView) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                super.onResourceReady(resource, animation);
                Palette.from(((GlideBitmapDrawable) resource).getBitmap()).generate(paletteAsyncListener);
            }
        };

        // Poster loading
        Glide.with(getActivity())
                .load(movieCursor.getString(movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_URL)))
                .fitCenter()
                .placeholder(R.color.grid_placeholder_bg)
                .into(posterGlideDrawable);

        // Background photo (backdrop) loading
        Glide.with(getActivity())
                .load(movieCursor.getString(movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_URL)))
                .fitCenter()
                .into(photoImageView);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (uri != null)
            return new CursorLoader(
                    getActivity(),
                    uri,
                    MOVIE_PROJECTION,
                    null,
                    null,
                    null);
        else
            return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        movieCursor = data;
        insertDataIntoUI();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * DetailFragment's scroll listener.
     */
    public interface OnScrollChangedListener {
        /**
         * Called when scroll position is changed.
         *
         * @param ratioWrapperHeight top part layout (ratio wrapper) height
         * @param color              color generated based on poster image
         * @param scrollPosition     current scroll position
         */
        void onScrollChanged(int ratioWrapperHeight, int color, int scrollPosition);
    }

}
