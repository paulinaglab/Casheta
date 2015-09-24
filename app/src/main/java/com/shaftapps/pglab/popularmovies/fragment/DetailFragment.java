package com.shaftapps.pglab.popularmovies.fragment;


import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private ImageView backdropImageView;

    private ViewTreeObserver.OnGlobalLayoutListener ratioWrapperOnGlobalLayoutListener;
    private int generatedColor;


    //
    //  FRAGMENT LIFECYCLE METHODS
    //

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

        setHasOptionsMenu(true);

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

    @Override
    public void onDestroyView() {
        if (ratioWrapperOnGlobalLayoutListener != null)
            //TODO: remove it in a method
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ratioWrapper.getViewTreeObserver()
                        .removeOnGlobalLayoutListener(ratioWrapperOnGlobalLayoutListener);
            } else {
                ratioWrapper.getViewTreeObserver()
                        .removeGlobalOnLayoutListener(ratioWrapperOnGlobalLayoutListener);
            }
        super.onDestroyView();
    }

    //
    //  OPTION MENU METHODS
    //

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(getClass().getSimpleName(), "Fragment's onCreateOptionsMenu: " + menu);
        inflater.inflate(R.menu.detailfragment, menu);
        initFavoriteMenuItem(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            setFavoriteItemChecked(item, !item.isChecked());
            updateMovie(item.isChecked());
            Log.i(getClass().getSimpleName(), "Fav icon clicked! Now is set to " + item.isChecked());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initFavoriteMenuItem(Menu menu) {
        // Is this movie favorite?
        if (uri != null) {
            Cursor cursor = getActivity().getContentResolver().query(
                    uri,
                    new String[]{MovieContract.MovieEntry.COLUMN_FAVORITE},
                    null,
                    null,
                    null);
            int columnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE);
            cursor.moveToFirst();
            boolean favorite = cursor.getInt(columnIndex) == 1;
            cursor.close();

            // Show adequate menu item
            MenuItem item = menu.findItem(R.id.action_favorite);
            setFavoriteItemChecked(item, favorite);
        }
    }

    private void setFavoriteItemChecked(MenuItem item, boolean checked) {
        item.setChecked(checked);
        if (checked) {
            item.setIcon(R.drawable.ic_favorite_on);
        } else {
            item.setIcon(R.drawable.ic_favorite_off);
        }
    }

    private void updateMovie(boolean favorite) {
        if (uri != null) {
            if (favorite) {
                // Add movie to favorites
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, true);
                getActivity().getContentResolver().update(
                        MovieContract.MovieEntry.CONTENT_URI,
                        contentValues,
                        MovieContract.MovieEntry._ID + "=?",
                        new String[]{Long.toString(ContentUris.parseId(uri))});
            } else {
                // Remove movie from favorites
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, false);
                getActivity().getContentResolver().update(
                        MovieContract.MovieEntry.CONTENT_URI,
                        contentValues,
                        MovieContract.MovieEntry._ID + "=?",
                        new String[]{Long.toString(ContentUris.parseId(uri))});
                //TODO: undo snackbar
                //TODO: highest rated/most popular ?: flag remove on exit
            }
        }
    }


    //
    //  INITIALIZATION HELPER METHODS
    //

    private void initFields(View fragmentView) {
        ratioWrapper = (ViewGroup) fragmentView.findViewById(R.id.detail_ratio_wrapper);
        titlesWrapper = fragmentView.findViewById(R.id.detail_titles_wrapper);
        rateWrapper = fragmentView.findViewById(R.id.detail_rate_wrapper);
        notifyingScrollView = (NotifyingScrollView)
                fragmentView.findViewById(R.id.detail_notifying_scroll_view);
        titleTextView = (TextView) fragmentView.findViewById(R.id.detail_movie_title);
        originalTitleTextView = (TextView)
                fragmentView.findViewById(R.id.detail_movie_original_title);
        rateTextView = (TextView) fragmentView.findViewById(R.id.detail_rate_text_view);
        overviewTextView = (TextView) fragmentView.findViewById(R.id.detail_overview);
        releaseDate = (TextView) fragmentView.findViewById(R.id.detail_release_date);
        posterImageView = (ImageView) fragmentView.findViewById(R.id.detail_poster_image);
        backdropImageView = (ImageView) fragmentView.findViewById(R.id.detail_photo_image);
    }


    /**
     * This method fits height of top part layout (with title, poster and backdrop).
     */
    private void fitRatioWrapperHeight() {
        ViewTreeObserver ratioWrapperTreeObserver = ratioWrapper.getViewTreeObserver();
        ratioWrapperOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
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
                ratioWrapperOnGlobalLayoutListener = null;
            }
        };
        ratioWrapperTreeObserver.addOnGlobalLayoutListener(ratioWrapperOnGlobalLayoutListener);
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
                if (getActivity() == null)
                    return;

                generatedColor = palette.getDarkVibrantColor(
                        ContextCompat.getColor(getActivity(), R.color.details_rate_not_initialized_bg));

                titlesWrapper.setBackgroundColor(generatedColor);

                rateWrapper.setBackgroundColor(
                        ColorUtils.getColorWithTranslateBrightness(generatedColor, -10));
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
                .into(backdropImageView);
    }


    //
    //  LOADER CALLBACKS
    //

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
