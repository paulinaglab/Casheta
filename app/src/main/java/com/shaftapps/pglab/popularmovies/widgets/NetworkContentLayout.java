package com.shaftapps.pglab.popularmovies.widgets;

import android.content.Context;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Container for layers which represents state of content.
 * <p/>
 * It assumes specific order of children:
 * 1. Default content view. (Loaded state)
 * 2. Progress bar. (Loading in progress)
 * 3. Failed/Error view.
 * 4. Empty view. (Success, but nothing to show)
 * <p/>
 * Views 3 and 4 are optional. By default, normal content view will be shown.
 * <p/>
 * Created by Paulina on 2015-10-09.
 */
public class NetworkContentLayout extends RelativeLayout {

    private static final int CONTENT_VIEW_INDEX = 0;
    private static final int PROGRESS_BAR_INDEX = 1;
    private static final int FAILED_STATE_INDEX = 2;
    private static final int EMPTY_STATE_INDEX = 3;

    private View contentView;
    private View progressBar;
    private View failedStateView;
    private View emptyStateView;

    @ContentState
    private int contentState;


    public NetworkContentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NetworkContentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(CONTENT_VIEW_INDEX);
        progressBar = getChildAt(PROGRESS_BAR_INDEX);
        failedStateView = getChildAt(FAILED_STATE_INDEX);
        emptyStateView = getChildAt(EMPTY_STATE_INDEX);
    }

    /**
     * Switch state of the content.
     *
     * @param contentState content state to be applied.
     */
    public void setContentState(@ContentState int contentState) {
        if (this.contentState != contentState) {
            this.contentState = contentState;

            switch (contentState) {
                case LOADED:
                    setComponentViewVisibility(contentView, VISIBLE);
                    // other views
                    setComponentViewVisibility(progressBar, GONE);
                    setComponentViewVisibility(failedStateView, GONE);
                    setComponentViewVisibility(emptyStateView, GONE);
                    break;

                case PROGRESS:
                    setComponentViewVisibility(progressBar, VISIBLE);
                    // other views
                    if (contentView != null && contentView.getVisibility() == VISIBLE)
                        setComponentViewVisibility(contentView, INVISIBLE);
                    if (failedStateView != null && failedStateView.getVisibility() == VISIBLE)
                        setComponentViewVisibility(failedStateView, INVISIBLE);
                    if (emptyStateView != null && emptyStateView.getVisibility() == VISIBLE)
                        setComponentViewVisibility(emptyStateView, INVISIBLE);
                    break;

                case FAILED:
                    // other views
                    setComponentViewVisibility(progressBar, GONE);
                    setComponentViewVisibility(emptyStateView, GONE);
                    if (failedStateView != null) {
                        setComponentViewVisibility(failedStateView, VISIBLE);
                        setComponentViewVisibility(contentView, GONE);
                    } else {
                        // By default, if failed state is not specified, use normal content view.
                        setComponentViewVisibility(contentView, VISIBLE);
                    }
                    break;

                case EMPTY:
                    setComponentViewVisibility(progressBar, GONE);
                    setComponentViewVisibility(failedStateView, GONE);
                    if (emptyStateView != null) {
                        setComponentViewVisibility(emptyStateView, VISIBLE);
                        setComponentViewVisibility(contentView, GONE);
                    } else {
                        // By default, if empty state is not specified, use normal content view.
                        setComponentViewVisibility(contentView, VISIBLE);
                    }
                    break;
            }
        }
    }

    private void setComponentViewVisibility(View view, int visibility) {
        if (view != null && view.getVisibility() != visibility)
            view.setVisibility(visibility);
    }


    /**
     * Logical type describes movies fetching state.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LOADED, PROGRESS, FAILED, EMPTY})
    public @interface ContentState {
    }

    public static final int LOADED = 0;
    public static final int PROGRESS = 1;
    public static final int FAILED = 2;
    public static final int EMPTY = 3;
}
