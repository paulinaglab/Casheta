package com.shaftapps.pglab.popularmovies.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.shaftapps.pglab.popularmovies.MovieDBApiKeys;
import com.shaftapps.pglab.popularmovies.R;
import com.shaftapps.pglab.popularmovies.data.MovieContract;
import com.shaftapps.pglab.popularmovies.utils.YouTubeUriBuilder;

/**
 * Adapter class for videos.
 * <p/>
 * Created by Paulina on 2015-09-25.
 */
public class VideosCursorAdapter extends CursorAdapter<VideosCursorAdapter.VideoItemViewHolder> {

    protected Context context;
    protected OnItemClickListener onItemClickListener;

    public VideosCursorAdapter(Context context) {
        this.context = context;
    }

    @Override
    public VideoItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_list_item, parent, false);
        final VideoItemViewHolder holder = new VideoItemViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClicked(holder.getAdapterPosition());
            }
        });
        return holder;
    }

    /**
     * Method called when any item has been clicked.
     *
     * @param clickedPos clicked item position
     */
    public void itemClicked(int clickedPos) {
        if (onItemClickListener != null) {
            cursor.moveToPosition(clickedPos);

            Uri clickedUri = MovieContract.VideoEntry.buildUri(
                    cursor.getLong(cursor.getColumnIndex(MovieContract.VideoEntry._ID)));

            onItemClickListener.onItemClicked(clickedUri);
        }
    }

    @Override
    public void onBindViewHolder(VideoItemViewHolder holder, int position) {
        cursor.moveToPosition(position);
        // Load thumbnail if video site is YouTube
        int siteColumnIndex = cursor.getColumnIndex(MovieContract.VideoEntry.COLUMN_SITE);
        String site = cursor.getString(siteColumnIndex);
        if (site.equals(MovieDBApiKeys.VIDEO_SITE_YOUTUBE)) {
            //TODO: Load thumbnail
            int keyColumnIndex = cursor.getColumnIndex(MovieContract.VideoEntry.COLUMN_KEY);

            String thumbnailUrl = YouTubeUriBuilder
                    .getThumbnailUri(cursor.getString(keyColumnIndex))
                    .toString();

            Glide.with(context)
                    .load(thumbnailUrl)
                    .fitCenter()
                    .placeholder(R.color.video_thumbnail_placeholder)
                    .into(holder.thumbnail);
        }
    }

    public class VideoItemViewHolder extends RecyclerView.ViewHolder {

        ImageView thumbnail;

        public VideoItemViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail_view);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * Listener for item (video) clicked.
     */
    public interface OnItemClickListener {
        /**
         * Triggered when user click a video item.
         *
         * @param uri uri of video clicked by user
         */
        void onItemClicked(Uri uri);
    }
}
