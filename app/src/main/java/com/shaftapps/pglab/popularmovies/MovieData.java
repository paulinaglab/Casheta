package com.shaftapps.pglab.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class reflecting movie object got from themoviedb.org.
 *
 * Created by Paulina on 2015-08-28.
 */
public class MovieData implements Parcelable {

    public long apiId;
    public String title;
    public String originalTitle;
    public String posterUrl;
    public String photoUrl;
    public double averageRate;
    public String overview;
    public String releaseDate;


    public MovieData() {
    }

    protected MovieData(Parcel in) {
        apiId = in.readLong();
        title = in.readString();
        originalTitle = in.readString();
        posterUrl = in.readString();
        photoUrl = in.readString();
        averageRate = in.readDouble();
        overview = in.readString();
        releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(apiId);
        dest.writeString(title);
        dest.writeString(originalTitle);
        dest.writeString(posterUrl);
        dest.writeString(photoUrl);
        dest.writeDouble(averageRate);
        dest.writeString(overview);
        dest.writeString(releaseDate);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MovieData> CREATOR = new Parcelable.Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };
}
