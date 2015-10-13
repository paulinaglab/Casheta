package com.shaftapps.pglab.popularmovies;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Paulina on 2015-10-09.
 */
public class FetchingState {
    /**
     * Logical type describes movies fetching state.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FETCHED, NOT_FINISHED, FAILED})
    public @interface State {
    }

    public static final int FETCHED = 0;
    public static final int NOT_FINISHED = 1;
    public static final int FAILED = 2;


    @State
    public static int get(int code) {
        switch (code) {
            case FETCHED:
            case NOT_FINISHED:
            case FAILED:
                return code;
            default:
                throw new RuntimeException("FetchingState: Undefined code: " + code);
        }
    }
}
