package com.shaftapps.pglab.popularmovies.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.fragments.FavoritesFragment;
import com.shaftapps.pglab.popularmovies.fragments.HighestRatedFragment;
import com.shaftapps.pglab.popularmovies.fragments.MostPopularFragment;

/**
 * PagerAdapter for movie categories (sorting modes).
 *
 * Created by Paulina on 2015-10-08.
 */
public class MovieCategoryPagerAdapter extends FragmentPagerAdapter {

    public static final int MOST_POPULAR_INDEX = 0;
    public static final int HIGHEST_RATED_INDEX = 1;
    public static final int FAVORITES_INDEX = 2;

    private static final int CATEGORY_COUNT = 3;

    private Context context;


    public MovieCategoryPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case MOST_POPULAR_INDEX:
                return new MostPopularFragment();
            case HIGHEST_RATED_INDEX:
                return new HighestRatedFragment();
            case FAVORITES_INDEX:
                return new FavoritesFragment();
            default:
                throw new UnsupportedOperationException("Undefined Fragment index: " + position);
        }
    }

    @Override
    public int getCount() {
        return CATEGORY_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getStringArray(R.array.sorting_modes)[position];
    }
}
