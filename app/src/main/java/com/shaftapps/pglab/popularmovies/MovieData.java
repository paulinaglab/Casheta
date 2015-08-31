package com.shaftapps.pglab.popularmovies;

import java.io.Serializable;

/**
 * Created by Paulina on 2015-08-28.
 */
public class MovieData implements Serializable {

    public static final String EXTRA_KEY = MovieData.class.getName();

    public String title;
    public String originalTitle;
    public String posterUrl;
    public String photoUrl;
    public double averageRate;
    public String overview;
    public String releaseDate;

}
