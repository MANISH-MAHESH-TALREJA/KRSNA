package net.manish.navratri.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;

import net.manish.navratri.R;
import net.manish.navratri.activity.ActivityYoutubeVideo;
import net.manish.navratri.activity.VideoPlayer;
import net.manish.navratri.item.ItemMessage;
import net.manish.navratri.item.ItemVideos;
import net.manish.navratri.util.Methods;

import java.util.ArrayList;
import java.util.List;

public class AdapterVideo extends RecyclerView.Adapter {

    private Activity activity;
    private Animation myAnim;
    private int columnWidth;
    private List<ItemVideos> videoLists;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public AdapterVideo(Activity activity, List<ItemVideos> videoLists, int columnWidth) {
        this.activity = activity;
        this.videoLists = videoLists;
        this.columnWidth = columnWidth;
        myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.fitness_video_adapter, parent, false);
        return new ViewHolder(view);

    }

    public void filterList(ArrayList<ItemVideos> filterList) {
        videoLists = filterList;
        notifyDataSetChanged();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (holder instanceof ViewHolder) {

            final ViewHolder viewHolder = (ViewHolder) holder;

            viewHolder.imageView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, (int) (columnWidth / 2.2)));

            viewHolder.textView.setText(videoLists.get(position).getVideoTitle());

            Glide.with(activity).load(videoLists.get(position).getVideoImage())
                    .placeholder(R.drawable.bg_header2)
                    .into(viewHolder.imageView);

            viewHolder.constraintLayout.setOnClickListener(v -> click(position));

            viewHolder.imageViewPlay.setOnClickListener(v -> click(position));


            if (videoLists.get(position).getVideoType().equalsIgnoreCase("youtube"))
            {
                viewHolder.imageViewFav.setVisibility(View.INVISIBLE);
            }
            viewHolder.imageViewFav.setOnClickListener(v ->
            {
                Uri uri = Uri.parse("https://www.youtube.com/watch?v=" + videoLists.get(position).getVideoURL()); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                activity.startActivity(intent);
            });

            viewHolder.imageViewShare.setOnClickListener(v -> {
                viewHolder.imageViewShare.startAnimation(myAnim);
                String string = activity.getResources().getString(R.string.Let_me_recommend_you_this_application) + "\n\n" + "https://play.google.com/store/apps/details?id=" + activity.getApplication().getPackageName();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, string);
                activity.startActivity(Intent.createChooser(intent, activity.getResources().getString(R.string.choose_one)));
            });

        }

    }

    private void click(int position) {
        clickMe(videoLists.get(position).getVideoType(), videoLists.get(position).getVideoURL(), activity, videoLists.get(position).getVideoLayout());
    }

    private void clickMe(String videoType, String videoLink, Activity activity, String orientation)
    {
        if (videoType.equals("youtube")) {
            activity.startActivity(new Intent(activity, ActivityYoutubeVideo.class)
                    .putExtra("video", videoLink).putExtra("orientation", orientation));
        } else {
            activity.startActivity(new Intent(activity, VideoPlayer.class)
                    .putExtra("video", videoLink).putExtra("orientation", orientation));
        }
    }

    @Override
    public int getItemCount() {
        return videoLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (videoLists.size() == position) {
            return VIEW_TYPE_LOADING;
        }  else {
            return VIEW_TYPE_ITEM;
        }
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView textView;
        private ConstraintLayout constraintLayout;
        private ImageView imageView, imageViewPlay, imageViewFav, imageViewShare;

        ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_video_adapter);
            textView = itemView.findViewById(R.id.textView_video_adapter);
            imageViewFav = itemView.findViewById(R.id.imageView_fav_video_adapter);
            imageViewPlay = itemView.findViewById(R.id.imageView_play_video_adapter);
            imageViewShare = itemView.findViewById(R.id.imageView_share_video_adapter);
            constraintLayout = itemView.findViewById(R.id.con_video_adapter);

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("StaticFieldLeak")
        public static ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar_loading);
        }
    }


}
