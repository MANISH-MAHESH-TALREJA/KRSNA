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
        /*if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.fitness_video_adapter, parent, false);
            return new ViewHolder(view);
        } else  {
            View v = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new ProgressViewHolder(v);
        }*/
    }

    public void filterList(ArrayList<ItemVideos> filterList) {
        // below line is to add our filtered
        // list in our course array list.
        videoLists = filterList;
        // below line is to notify our adapter
        // as change in recycler view data.
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
                    .placeholder(R.drawable.place_holder)
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

        } /*else if (holder.getItemViewType() == VIEW_TYPE_Ad) {
            AdOption adOption = (AdOption) holder;
            if (adOption.conAdView.getChildCount() == 0) {
                if (videoLists.get(position).getNative_ad_type().equals("admob")) {

                    View view = activity.getLayoutInflater().inflate(R.layout.admob_ad, null, true);

                    TemplateView templateView = view.findViewById(R.id.my_template);
                    if (templateView.getParent() != null) {
                        ((ViewGroup) templateView.getParent()).removeView(templateView); // <- fix
                    }
                    adOption.conAdView.addView(templateView);
                    AdLoader adLoader = new AdLoader.Builder(activity, videoLists.get(position).getNative_ad_id())
                            .forNativeAd(nativeAd -> {
                                NativeTemplateStyle styles = new
                                        NativeTemplateStyle.Builder()
                                        .build();

                                templateView.setStyles(styles);
                                templateView.setNativeAd(nativeAd);

                            })
                            .build();

                    AdRequest adRequest;
                    if (Method.personalization_ad) {
                        adRequest = new AdRequest.Builder()
                                .build();
                    } else {
                        Bundle extras = new Bundle();
                        extras.putString("npa", "1");
                        adRequest = new AdRequest.Builder()
                                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                .build();
                    }
                    adLoader.loadAd(adRequest);
                } else {
                    LayoutInflater inflater = LayoutInflater.from(activity);
                    LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, adOption.conAdView, false);

                    NativeAd nativeAd = new NativeAd(activity, videoLists.get(position).getNative_ad_id());

                    // Add the AdOptionsView
                    LinearLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);

                    // Create native UI using the ad metadata.
                    MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
                    TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
                    MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
                    TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
                    TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
                    TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
                    Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

                    adOption.conAdView.addView(adView);

                    NativeAdListener nativeAdListener = new NativeAdListener() {
                        @Override
                        public void onMediaDownloaded(Ad ad) {
                            // Native ad finished downloading all assets
                            Log.e("status_data", "Native ad finished downloading all assets.");
                        }

                        @Override
                        public void onError(Ad ad, AdError adError) {
                            // Native ad failed to load
                            Log.e("status_data", "Native ad failed to load: " + adError.getErrorMessage());
                        }

                        @Override
                        public void onAdLoaded(Ad ad) {
                            // Native ad is loaded and ready to be displayed
                            Log.d("status_data", "Native ad is loaded and ready to be displayed!");
                            // Race condition, load() called again before last ad was displayed
                            if (nativeAd == null || nativeAd != ad) {
                                return;
                            }
                            // Inflate Native Ad into Container
                            Log.d("status_data", "on load" + " " + ad.toString());

                            NativeAdLayout nativeAdLayout = new NativeAdLayout(activity);
                            AdOptionsView adOptionsView = new AdOptionsView(activity, nativeAd, nativeAdLayout);
                            adChoicesContainer.removeAllViews();
                            adChoicesContainer.addView(adOptionsView, 0);

                            // Set the Text.
                            nativeAdTitle.setText(nativeAd.getAdvertiserName());
                            nativeAdBody.setText(nativeAd.getAdBodyText());
                            nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                            nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                            nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
                            sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

                            // Create a list of clickable views
                            List<View> clickableViews = new ArrayList<>();
                            clickableViews.add(nativeAdTitle);
                            clickableViews.add(nativeAdCallToAction);

                            // Register the Title and CTA button to listen for clicks.
                            nativeAd.registerViewForInteraction(
                                    adOption.conAdView,
                                    nativeAdMedia,
                                    nativeAdIcon,
                                    clickableViews);
                        }

                        @Override
                        public void onAdClicked(Ad ad) {
                            // Native ad clicked
                            Log.d("status_data", "Native ad clicked!");
                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {
                            // Native ad impression
                            Log.d("status_data", "Native ad impression logged!");
                        }
                    };

                    // Request an ad
                    nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
                }
            }
        }*/

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
