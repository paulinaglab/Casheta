package com.shaftapps.pglab.popularmovies.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.shaftapps.pglab.popularmovies.Keys;
import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.adapters.ReviewsCursorAdapter;
import com.shaftapps.pglab.popularmovies.widgets.ReviewItemSeparator;

/**
 * Created by Paulina on 2015-09-29.
 */
public class ReviewsDialogFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = ReviewsDialogFragment.class.getName();

    private static final int REVIEWS_LOADER_ID = 1;

    private Uri reviewsUri;

    private ReviewsCursorAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, getTheme());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null)
            reviewsUri = getArguments().getParcelable(Keys.REVIEWS_OF_MOVIE_URI);

        View fragmentView = inflater.inflate(R.layout.fragment_reviews, container, false);

        // Recycler initialization
        RecyclerView recyclerView = (RecyclerView) fragmentView.findViewById(R.id.reviews_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new ReviewsCursorAdapter(getActivity(), savedInstanceState);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(
                new ReviewItemSeparator(getResources().getColor(R.color.separator),
                        getResources().getDimensionPixelSize(R.dimen.reviews_item_separator_width),
                        getResources().getDimensionPixelSize(R.dimen.reviews_item_separator_indent)));

        RecyclerView.ItemAnimator animator = new DefaultItemAnimator();
        animator.setSupportsChangeAnimations(false);

        recyclerView.setItemAnimator(animator);

        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(REVIEWS_LOADER_ID, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (adapter != null)
            adapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    //
    //  LOADER CALLBACKS
    //

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), reviewsUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
