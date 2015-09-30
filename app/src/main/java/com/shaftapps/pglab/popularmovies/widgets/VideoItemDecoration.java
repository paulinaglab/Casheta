package com.shaftapps.pglab.popularmovies.widgets;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Paulina on 2015-09-27.
 */
public class VideoItemDecoration extends RecyclerView.ItemDecoration {

    private int halfSpace;

    public VideoItemDecoration(int spacePixelSize) {
        this.halfSpace = spacePixelSize / 2;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int pos = parent.getChildViewHolder(view).getAdapterPosition();

        if (pos > 0)
            outRect.left = halfSpace;

        if (pos < parent.getAdapter().getItemCount() - 1)
            outRect.right = halfSpace;
    }
}
