package com.shaftapps.pglab.popularmovies.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.fragments.DetailFragment;
import com.shaftapps.pglab.popularmovies.utils.ColorUtils;

/**
 * Base class for Activities including DetailFragment.
 * <p/>
 * Created by Paulina on 2015-10-02.
 */
public abstract class DetailFragmentActivity extends AppCompatActivity implements DetailFragment.DetailFragmentListener {

    private Toolbar toolbar;
    private String toolbarTitle;
    private Snackbar snackbar;


    /**
     * Method to attach arbitrary Toolbar which will be updating by DetailFragment.
     *
     * @param toolbar arbitrary Toolbar.
     */
    protected void bindToolbarWithDetailFragment(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    /**
     * @return Toolbar bind with DetailFragment.
     */
    protected Toolbar getDetailFragmentToolbar() {
        return toolbar;
    }

    /**
     * This method update Toolbar title showing it only if attached Toolbar is opaque (and not
     * showing if the Toolbar is translucent/transparent).
     */
    public void showTitleIfOpaque() {
        // Show title on toolbar when background is opaque.
        if ((toolbar.getBackground() instanceof ColorDrawable) &&
                Color.alpha(((ColorDrawable) toolbar.getBackground()).getColor()) == 255)
            toolbar.setTitle(toolbarTitle);
        else toolbar.setTitle("");
    }

    @Nullable
    protected Snackbar getSnackbar() {
        return snackbar;
    }

    protected void setSnackbar(Snackbar snackbar) {
        this.snackbar = snackbar;
    }

    /**
     * @return layout on which Snackbars will be shown.
     */
    protected abstract CoordinatorLayout getCoordinatorLayout();


    //
    //  INTERFACE METHODS:
    //  DetailFragmentListener
    //

    @Override
    public void onActionBarParamsChanged(int wrapperHeight, int color, int scrollPosition) {
        // Paint Toolbar background.
        // Alpha of the color depends on DetailFragment's scroll position - start as transparent
        // and ends as opaque.
        float changingDistance = wrapperHeight - toolbar.getHeight();
        int currentColor = ColorUtils.getColorWithProportionalAlpha(
                color, changingDistance, scrollPosition);
        toolbar.setBackgroundColor(currentColor);

        showTitleIfOpaque();
    }

    @Override
    public void onTitleLoaded(String title) {
        this.toolbarTitle = title;
        showTitleIfOpaque();
    }

    @Override
    public void showFetchingFailedSnackbar() {
        if (snackbar == null) {
            snackbar = Snackbar.make(
                    getCoordinatorLayout(),
                    getResources().getString(R.string.snackbar_detail_fetching_failed_text),
                    Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getResources().getString(R.string.snackbar_detail_fetching_failed_action),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DetailFragment detailFragment = (DetailFragment)
                                    getSupportFragmentManager().findFragmentById(R.id.movie_detail_container);
                            detailFragment.retryFailedFetching();
                            DetailFragmentActivity.this.snackbar = null;
                        }
                    });
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.snackbar_bg));
            snackbar.show();
        }
    }
}
