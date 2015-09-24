package com.shaftapps.pglab.popularmovies.util;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.Toolbar;

/**
 * Created by Paulina on 2015-09-24.
 */
public class ToolbarUtils {

    public static void showTitleIfOpaque(Toolbar toolbar, String title){
        // Show title on toolbar when background is opaque.
        if ((toolbar.getBackground() instanceof ColorDrawable) &&
                Color.alpha(((ColorDrawable) toolbar.getBackground()).getColor()) == 255)
            toolbar.setTitle(title);
        else toolbar.setTitle("");
    }

}
