package com.shaftapps.pglab.popularmovies.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
 * Created by Paulina on 2015-10-02.
 */
public abstract class DetailFragmentActivity extends AppCompatActivity implements DetailFragment.DetailFragmentListener {

    private Toolbar toolbar;
    private String toolbarTitle;
    private Snackbar snackbar;


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

    protected void bindToolbarWithDetailFragment(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    protected Toolbar getDetailFragmentToolbar() {
        return toolbar;
    }

    public void showTitleIfOpaque() {
        // Show title on toolbar when background is opaque.
        if ((toolbar.getBackground() instanceof ColorDrawable) &&
                Color.alpha(((ColorDrawable) toolbar.getBackground()).getColor()) == 255)
            toolbar.setTitle(toolbarTitle);
        else toolbar.setTitle("");
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

            snackbar.setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    super.onDismissed(snackbar, event);
                    DetailFragmentActivity.this.snackbar = null;
                }

                @Override
                public void onShown(Snackbar snackbar) {
                    super.onShown(snackbar);
                }
            });
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.snackbar_bg));
            snackbar.show();
        }
    }

    protected abstract CoordinatorLayout getCoordinatorLayout();

    protected Snackbar getSnackbar() {
        return snackbar;
    }

    protected void setSnackbar(Snackbar snackbar) {
        this.snackbar = snackbar;
    }
}
