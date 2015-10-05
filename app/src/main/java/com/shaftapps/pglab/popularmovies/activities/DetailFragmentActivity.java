package com.shaftapps.pglab.popularmovies.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.shaftapps.pglab.popularmovies.fragments.DetailFragment;
import com.shaftapps.pglab.popularmovies.utils.ColorUtils;

/**
 * Created by Paulina on 2015-10-02.
 */
public abstract class DetailFragmentActivity extends AppCompatActivity implements DetailFragment.OnActionBarParamsChangedListener {

    private Toolbar toolbar;
    private String toolbarTitle;

    @Override
    public void onParamsChanged(int ratioWrapperHeight, int color, int scrollPosition) {
        // Paint Toolbar background.
        // Alpha of the color depends on DetailFragment's scroll position - start as transparent
        // and ends as opaque.
        float changingDistance = ratioWrapperHeight - toolbar.getHeight();
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

    public void showTitleIfOpaque(){
        // Show title on toolbar when background is opaque.
        if ((toolbar.getBackground() instanceof ColorDrawable) &&
                Color.alpha(((ColorDrawable) toolbar.getBackground()).getColor()) == 255)
            toolbar.setTitle(toolbarTitle);
        else toolbar.setTitle("");
    }

}
