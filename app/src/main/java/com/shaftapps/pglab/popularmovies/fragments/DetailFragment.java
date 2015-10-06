package com.shaftapps.pglab.popularmovies.fragments;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.shaftapps.pglab.popularmovies.Keys;
import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.activities.ReviewsActivity;
import com.shaftapps.pglab.popularmovies.asynctasks.FetchMovieDetailsTask;
import com.shaftapps.pglab.popularmovies.utils.DisplayUtils;
import com.shaftapps.pglab.popularmovies.utils.TextLoader;
import com.shaftapps.pglab.popularmovies.utils.YouTubeUriBuilder;
import com.shaftapps.pglab.popularmovies.widgets.VideoItemDecoration;
import com.shaftapps.pglab.popularmovies.adapters.VideosCursorAdapter;
import com.shaftapps.pglab.popularmovies.asynctasks.FetchReviewsTask;
import com.shaftapps.pglab.popularmovies.asynctasks.FetchVideosTask;
import com.shaftapps.pglab.popularmovies.data.MovieContract;
import com.shaftapps.pglab.popularmovies.utils.ColorUtils;
import com.shaftapps.pglab.popularmovies.widgets.NotifyingScrollView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Fragment with details of specific movie.
 * <p/>
 * Created by Paulina on 2015-08-30.
 */
public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, VideosCursorAdapter.OnItemClickListener, View.OnClickListener {

    private static final String GENERATED_COLOR_KEY = "generated_color";

    private static final int MOVIE_LOADER_ID = 1;
    private static final int REVIEW_LOADER_ID = 2;
    private static final int VIDEO_LOADER_ID = 3;

    private static final String[] MOVIE_PROJECTION = new String[]{
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_AVERAGE_RATE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_GENRE,
            MovieContract.MovieEntry.COLUMN_COUNTRY,
            MovieContract.MovieEntry.COLUMN_POSTER_URL,
            MovieContract.MovieEntry.COLUMN_BACKDROP_URL};

    private static final int RATE_COLOR_TRANSLATION = -10;

    private Uri movieUri;
    private Uri reviewsUri;

    private Cursor movieCursor;

    private OnActionBarParamsChangedListener onActionBarParamsChangedListener;

    private ViewGroup ratioWrapper;
    private View titlesWrapper;
    private View rateWrapper;
    private NotifyingScrollView notifyingScrollView;
    // Section: Rich Content (top part)
    private ImageView posterImageView;
    private ImageView backdropImageView;
    private TextView titleTextView;
    private TextView originalTitleTextView;
    private TextView rateTextView;
    // Section: Basic Info
    private TextView releaseDate;
    private TextView genreTextView;
    private TextView countryTextView;
    // Section: Overview
    private TextView overviewTextView;
    // Section: Reviews
    private TextView reviewSubheaderTextView;
    private View reviewEmptyStateView;
    private View reviewItemLayout;
    private TextView reviewAuthorNameTextView;
    private TextView reviewContentTextView;
    private Button reviewShowMoreButton;
    // Section: Videos
    private RecyclerView videoRecyclerView;
    private VideosCursorAdapter videosAdapter;

    private ViewTreeObserver.OnGlobalLayoutListener ratioWrapperOnGlobalLayoutListener;
    private int generatedColor = -1;
    private boolean tablet;
    private String placeholderText;


    //
    //  FRAGMENT LIFECYCLE METHODS
    //

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            movieUri = getArguments().getParcelable(Keys.SELECTED_MOVIE_URI);
            if (movieUri != null)
                reviewsUri = MovieContract.ReviewEntry.buildUriByMovieId(
                        ContentUris.parseId(movieUri));
        }

        if (savedInstanceState != null) {
            generatedColor = savedInstanceState.getInt(GENERATED_COLOR_KEY, -1);
        }

        View fragmentView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Fields initialization
        initFields(fragmentView);

        // Setting height to layout ratioWrapper
        fitRatioWrapperHeight();

        // Setting ScrollView listener
        initScrollViewListener();

        // RecyclerView with Videos initialization
        initVideoRecyclerView();

        setHasOptionsMenu(true);

        // Checking device is phone or tablet
        tablet = DisplayUtils.isSmallestWidth600dp(getActivity());

        placeholderText = getResources().getString(R.string.details_no_info_placeholder);

        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER_ID, null, this);
        getLoaderManager().initLoader(VIDEO_LOADER_ID, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onActionBarParamsChangedListener = (OnActionBarParamsChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnActionBarParamsChangedListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (movieUri != null) {
            long movieId = ContentUris.parseId(movieUri);
            FetchMovieDetailsTask fetchMovieDetailsTask =
                    new FetchMovieDetailsTask(getActivity(), movieId);
            fetchMovieDetailsTask.execute();

            FetchReviewsTask fetchReviewsTask =
                    new FetchReviewsTask(getActivity(), movieId);
            fetchReviewsTask.execute();

            FetchVideosTask fetchVideosTask =
                    new FetchVideosTask(getActivity(), movieId);
            fetchVideosTask.execute();
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Saving color once generated.
        // I want to keep it after rotating.
        if (generatedColor != -1)
            outState.putInt(GENERATED_COLOR_KEY, generatedColor);

        super.onSaveInstanceState(outState);
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
        switch (item.getItemId()) {
            case R.id.action_favorite:
                setFavoriteItemChecked(item, !item.isChecked());
                updateMovie(item.isChecked());
                Log.i(getClass().getSimpleName(), "Fav icon clicked! Now is set to " + item.isChecked());
                return true;
            case R.id.action_share:
                Intent intent = createShareMovieIntent();
                if (intent != null)
                    startActivity(intent);
                else
                    Toast.makeText(
                            getActivity(),
                            getResources().getString(R.string.sharing_toast_no_video),
                            Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initFavoriteMenuItem(Menu menu) {
        // Is this movie favorite?
        if (movieUri != null) {
            Cursor cursor = getActivity().getContentResolver().query(
                    movieUri,
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

    private Intent createShareMovieIntent() {
        Uri trailerUri = MovieContract.VideoEntry
                .buildUriForMovieTrailer(ContentUris.parseId(movieUri));
        Cursor cursor = getActivity().getContentResolver().query(
                trailerUri, null, null, null, null);
        if (cursor.moveToFirst()) {
            int keyColumnIndex = cursor.getColumnIndex(MovieContract.VideoEntry.COLUMN_KEY);
            Intent intent = ShareCompat.IntentBuilder
                    .from(getActivity())
                    .setText(getString(R.string.sharing_movie_text,
                            YouTubeUriBuilder.getUri(cursor.getString(keyColumnIndex)).toString()) +
                            getResources().getString(R.string.app_hashtag))
                    .setType("text/plain")
                    .createChooserIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            return intent;
        } else
            return null;
    }

    private void updateMovie(boolean favorite) {
        if (movieUri != null) {
            if (favorite) {
                // Add movie to favorites
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, true);
                getActivity().getContentResolver().update(
                        MovieContract.MovieEntry.CONTENT_URI,
                        contentValues,
                        MovieContract.MovieEntry._ID + "=?",
                        new String[]{Long.toString(ContentUris.parseId(movieUri))});
            } else {
                // Remove movie from favorites
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, false);
                getActivity().getContentResolver().update(
                        MovieContract.MovieEntry.CONTENT_URI,
                        contentValues,
                        MovieContract.MovieEntry._ID + "=?",
                        new String[]{Long.toString(ContentUris.parseId(movieUri))});
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

        // Section: Rich Content
        posterImageView = (ImageView) fragmentView.findViewById(R.id.detail_poster_image);
        backdropImageView = (ImageView) fragmentView.findViewById(R.id.detail_photo_image);
        titleTextView = (TextView) fragmentView.findViewById(R.id.detail_movie_title);
        originalTitleTextView = (TextView)
                fragmentView.findViewById(R.id.detail_movie_original_title);
        rateTextView = (TextView) fragmentView.findViewById(R.id.detail_rate_text_view);
        // Section: Overview
        overviewTextView = (TextView) fragmentView.findViewById(R.id.detail_overview);
        // Section: Basic Info
        releaseDate = (TextView) fragmentView.findViewById(R.id.detail_release_date_text_view);
        genreTextView = (TextView) fragmentView.findViewById(R.id.detail_genres_text_view);
        countryTextView = (TextView) fragmentView.findViewById(R.id.detail_countries_text_view);
        // Section: Reviews
        reviewSubheaderTextView = (TextView) fragmentView.findViewById(R.id.detail_review_subheader_text_view);
        reviewEmptyStateView = fragmentView.findViewById(R.id.detail_review_empty_state_view);
        reviewItemLayout = fragmentView.findViewById(R.id.detail_review_item_view);
        reviewAuthorNameTextView = (TextView) reviewItemLayout.findViewById(R.id.review_author_name_view);
        reviewContentTextView = (TextView) reviewItemLayout.findViewById(R.id.review_content_view);
        reviewShowMoreButton = (Button) fragmentView.findViewById(R.id.detail_reviews_show_more_button);
        // Section: Videos
        videoRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.detail_video_recycler_view);
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
                        getResources().getDimensionPixelSize(R.dimen.status_bar_height) +
                        getResources().getDimensionPixelSize(R.dimen.translucent_status_bar_padding);
                ratioWrapper.setLayoutParams(new LinearLayout.LayoutParams(
                        ratioWrapper.getWidth(),
                        Math.min(ratioWrapper.getWidth(), screenHeight)));
                notifyActionBarParamsChanged();

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
                notifyActionBarParamsChanged();
            }
        });
    }

    private void notifyActionBarParamsChanged() {
        if (onActionBarParamsChangedListener != null) {
            onActionBarParamsChangedListener.onParamsChanged(
                    ratioWrapper.getHeight(),
                    generatedColor != -1 ? generatedColor : ContextCompat.getColor(getActivity(), R.color.main_toolbar_bg),
                    notifyingScrollView.getScrollY());
        }
    }

    private void initVideoRecyclerView() {
        videosAdapter = new VideosCursorAdapter(getActivity());
        videosAdapter.setOnItemClickListener(this);
        videoRecyclerView.setFocusable(false);
        videoRecyclerView.setAdapter(videosAdapter);
        videoRecyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        int spaceBetweenPx = getResources().getDimensionPixelSize(R.dimen.details_video_item_space);
        videoRecyclerView.addItemDecoration(new VideoItemDecoration(spaceBetweenPx));
    }


    //
    //  LOADING DATA HELPER METHODS
    //

    private void loadMovieDetails(Cursor oldMovieCursor, Cursor newMovieCursor) {
        if (newMovieCursor != null && newMovieCursor.moveToFirst()) {

            // Notify ActionBar that title is loaded.
            if (onActionBarParamsChangedListener != null) {
                onActionBarParamsChangedListener.onTitleLoaded(newMovieCursor.getString(
                        newMovieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
            }

            loadSectionRichContent(oldMovieCursor, newMovieCursor);

            loadSectionOverview(newMovieCursor);

            loadSectionBasicInfo(newMovieCursor);
        }
    }

    private void loadSectionRichContent(@Nullable Cursor oldMovieCursor, @NonNull Cursor newMovieCursor) {
        // Setting text data
        TextLoader.loadText(titleTextView,
                newMovieCursor, MovieContract.MovieEntry.COLUMN_TITLE);
        TextLoader.loadText(originalTitleTextView,
                newMovieCursor, MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        rateTextView.setText(getString(R.string.details_rate_format, newMovieCursor.getDouble(
                newMovieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_AVERAGE_RATE))));

        // Loading images and color generation
        String newPosterUrl = newMovieCursor.getString(
                newMovieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_URL));
        String newBackdropUrl = newMovieCursor.getString(
                newMovieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_URL));

        // Applying new images only if they actually different.
        // Thanks of that I avoid unnecessary reloading.
        if (oldMovieCursor == null || !oldMovieCursor.moveToFirst()) {
            loadPosterAndColors(newPosterUrl);
            loadBackdrop(newBackdropUrl);
        } else {
            // Update poster only if poster_url has changed.
            String oldPosterUrl = oldMovieCursor.getString(
                    oldMovieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_URL));

            if (!newPosterUrl.equals(oldPosterUrl))
                loadPosterAndColors(newPosterUrl);

            // Update backdrop only if backdrop_url has changed.
            String oldBackdropUrl = oldMovieCursor.getString(
                    oldMovieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_URL));

            if (!newBackdropUrl.equals(oldBackdropUrl))
                loadBackdrop(newBackdropUrl);

        }

    }

    private void loadPosterAndColors(String url) {
        if (generatedColor != -1) {
            titlesWrapper.setBackgroundColor(generatedColor);
            rateWrapper.setBackgroundColor(
                    ColorUtils.getColorWithTranslateBrightness(generatedColor, RATE_COLOR_TRANSLATION));

            // Poster loading
            Glide.with(getActivity())
                    .load(url)
                    .fitCenter()
                    .placeholder(R.color.grid_placeholder_bg)
                    .into(posterImageView);
        } else {
            // Creating listener for palette
            final Palette.PaletteAsyncListener paletteAsyncListener = new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    if (getActivity() == null)
                        return;

                    generatedColor = palette.getDarkVibrantColor(
                            ContextCompat.getColor(getActivity(), R.color.details_rate_not_initialized_bg));

                    int duration = 175;
                    int titleStartDelay = duration / 3;

                    // Animating titles' wrapper background
                    ObjectAnimator titleBgAnimator = ObjectAnimator.ofInt(
                            titlesWrapper,
                            "backgroundColor",
                            ContextCompat.getColor(getActivity(), R.color.details_title_not_initialized_bg),
                            generatedColor);
                    titleBgAnimator.setEvaluator(new ArgbEvaluator());
                    titleBgAnimator.setDuration(duration - titleStartDelay);
                    titleBgAnimator.setStartDelay(titleStartDelay);
                    titleBgAnimator.setInterpolator(new AccelerateInterpolator());
                    titleBgAnimator.start();

                    // Animating rate's wrapper background
                    ObjectAnimator rateBgAnimator = ObjectAnimator.ofInt(
                            rateWrapper,
                            "backgroundColor",
                            ContextCompat.getColor(getActivity(), R.color.details_rate_not_initialized_bg),
                            ColorUtils.getColorWithTranslateBrightness(generatedColor, RATE_COLOR_TRANSLATION));
                    rateBgAnimator.setEvaluator(new ArgbEvaluator());
                    rateBgAnimator.setDuration(duration);
                    rateBgAnimator.setInterpolator(new AccelerateInterpolator());
                    rateBgAnimator.start();

                    notifyActionBarParamsChanged();
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
                    .load(url)
                    .fitCenter()
                    .placeholder(R.color.grid_placeholder_bg)
                    .into(posterGlideDrawable);
        }
    }

    private void loadBackdrop(String url) {
        // Background photo (backdrop) loading
        Glide.with(getActivity())
                .load(url)
                .fitCenter()
                .into(backdropImageView);
    }

    private void loadSectionOverview(@NonNull Cursor movieCursor) {
        TextLoader.loadText(overviewTextView,
                movieCursor, MovieContract.MovieEntry.COLUMN_OVERVIEW);
    }

    private void loadSectionBasicInfo(@NonNull Cursor movieCursor) {
        TextLoader.loadDate(releaseDate,
                movieCursor, MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        TextLoader.loadText(genreTextView,
                movieCursor, MovieContract.MovieEntry.COLUMN_GENRE, placeholderText);
        TextLoader.loadText(countryTextView,
                movieCursor, MovieContract.MovieEntry.COLUMN_COUNTRY, placeholderText);
    }

    private void loadSectionReviews(@Nullable Cursor reviewsCursor) {
        if (reviewsCursor != null && reviewsCursor.moveToFirst()) {
            reviewSubheaderTextView.setText(getString(R.string.details_reviews_label,
                    reviewsCursor.getCount()));

            TextLoader.loadText(reviewAuthorNameTextView,
                    reviewsCursor, MovieContract.ReviewEntry.COLUMN_AUTHOR);
            TextLoader.loadText(reviewContentTextView,
                    reviewsCursor, MovieContract.ReviewEntry.COLUMN_CONTENT);

            reviewEmptyStateView.setVisibility(View.GONE);
            reviewItemLayout.setVisibility(View.VISIBLE);

            // Button 'show more' should be visible only if there are more then one (shown) review.
            if (reviewsCursor.moveToNext())
                reviewShowMoreButton.setVisibility(View.VISIBLE);
            else
                reviewShowMoreButton.setVisibility(View.GONE);

            // Setting Listener which would open full reviews list in dialog (tablets) or
            // activity (phones).
            reviewItemLayout.setOnClickListener(this);
            reviewShowMoreButton.setOnClickListener(this);
        } else {
            //There is no reviews.
            reviewSubheaderTextView.setText(getString(R.string.details_reviews_label, 0));
            reviewEmptyStateView.setVisibility(View.VISIBLE);
            reviewItemLayout.setVisibility(View.GONE);
            reviewShowMoreButton.setVisibility(View.GONE);
        }
    }

    private void loadSectionVideos(@Nullable Cursor videosCursor) {
        videosAdapter.swapCursor(videosCursor);
    }

    public Uri getMovieUri() {
        return movieUri;
    }

    public void reloadMovie() {
        notifyingScrollView.smoothScrollTo(0, 0);
    }


    //
    //  LOADER CALLBACKS
    //

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (movieUri != null) {
            switch (id) {
                case MOVIE_LOADER_ID:
                    return new CursorLoader(
                            getActivity(),
                            movieUri,
                            MOVIE_PROJECTION, null, null, null);
                case REVIEW_LOADER_ID:
                    return new CursorLoader(
                            getActivity(),
                            reviewsUri,
                            null, null, null, null);
                case VIDEO_LOADER_ID:
                    return new CursorLoader(
                            getActivity(),
                            MovieContract.VideoEntry.buildUriByMovieId(ContentUris.parseId(movieUri)),
                            null, null, null, null);
                default:
                    throw new UnsupportedOperationException("Unknown loader id: " + id);
            }
        } else
            return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case MOVIE_LOADER_ID:
                loadMovieDetails(movieCursor, data);
                movieCursor = data;
                break;
            case REVIEW_LOADER_ID:
                loadSectionReviews(data);
                break;
            case VIDEO_LOADER_ID:
                loadSectionVideos(data);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case MOVIE_LOADER_ID:
                movieCursor = null;
                break;
            case REVIEW_LOADER_ID:
                // do nothing
                break;
            case VIDEO_LOADER_ID:
                loadSectionVideos(null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }
    }

    @Override
    public void onItemClicked(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {
            int keyColumnIndex = cursor.getColumnIndex(MovieContract.VideoEntry.COLUMN_KEY);
            Uri videoUrl = YouTubeUriBuilder.getUri(cursor.getString(keyColumnIndex));
            cursor.close();

            Intent intent = new Intent(Intent.ACTION_VIEW, videoUrl);
            startActivity(intent);
        } else
            cursor.close();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == reviewShowMoreButton.getId() || v.getId() == reviewItemLayout.getId()) {
            // Show dialog for tablets and open activity for phones.
            if (tablet) {
                ReviewsDialogFragment fragment = new ReviewsDialogFragment();
                Bundle arguments = new Bundle();
                arguments.putParcelable(Keys.REVIEWS_OF_MOVIE_URI, reviewsUri);
                fragment.setArguments(arguments);
                fragment.show(getFragmentManager(), ReviewsDialogFragment.TAG);
            } else {
                // Opening new activity with uri of selected movie.
                Intent intent = new Intent(getActivity(), ReviewsActivity.class)
                        .setData(reviewsUri);
                startActivity(intent);
            }
        }
    }


    /**
     * DetailFragment's scroll, changing distance and color listener.
     */
    public interface OnActionBarParamsChangedListener {
        /**
         * Called when scroll position, changing distance or end color is changed.
         *
         * @param ratioWrapperHeight top part layout (ratio wrapper) height
         * @param color              color generated based on poster image
         * @param scrollPosition     current scroll position
         */
        void onParamsChanged(int ratioWrapperHeight, int color, int scrollPosition);

        /**
         * Called when a movie title is loaded.
         *
         * @param title title of a movie
         */
        void onTitleLoaded(String title);
    }

}
