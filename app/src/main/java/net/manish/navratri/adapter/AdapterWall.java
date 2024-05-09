package net.manish.navratri.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/*import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;*/
import com.squareup.picasso.Picasso;
/*import com.startapp.sdk.ads.nativead.NativeAdDetails;*/
import net.manish.navratri.R;
import net.manish.navratri.activity.SingleWallpaper;
import net.manish.navratri.interfaces.InterAdListener;
import net.manish.navratri.item.ItemWallpaper;
import net.manish.navratri.util.Constant;
import net.manish.navratri.util.Methods;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class AdapterWall extends RecyclerView.Adapter {

    Context context;
    ArrayList<ItemWallpaper> arrayList, arrayListAdLess;
    int imageWidth;
    Methods methods;

    final int VIEW_ADS = -2;

    Boolean isAdLoaded = false;
    // List<NativeAd> mNativeAdsAdmob = new ArrayList<>();
    // List<NativeAdDetails> nativeAdsStartApp = new ArrayList<>();

    public AdapterWall(Context context, ArrayList<ItemWallpaper> arrayList, ArrayList<ItemWallpaper> arrayListAdLess, int columnWidth) {
        this.arrayList = arrayList;
        this.arrayListAdLess = arrayListAdLess;
        this.imageWidth = columnWidth;
        this.context = context;
        methods = new Methods(context/*, interAdListener*/);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.iv_wall);
        }
    }

    /*private static class ADViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rl_native_ad;

        private ADViewHolder(View view) {
            super(view);
            rl_native_ad = view.findViewById(R.id.rl_native_ad);
        }
    }*/

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_wallpaper, parent, false);
        return new MyViewHolder(itemView);
        /*if (viewType == VIEW_ADS) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ads, parent, false);
            return new ADViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_wallpaper, parent, false);
            return new MyViewHolder(itemView);
        }*/
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        ItemWallpaper objLatestBean = arrayList.get(position);

        ((MyViewHolder) holder).imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ((MyViewHolder) holder).imageView.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));

        Picasso.get()
                .load(objLatestBean.getImageSmall().replace(" ", "%20"))
                .placeholder(R.mipmap.app_icon)
                .into(((MyViewHolder) holder).imageView);

        ((MyViewHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.arrayList_wallpaper.clear();
                Constant.arrayList_wallpaper.addAll(arrayListAdLess);
                Intent intent = new Intent(context, SingleWallpaper.class);
                intent.putExtra("pos", getPos(position));
                intent.putExtra("layout", Constant.arrayList_wallpaper.get(position).getLayout());
                context.startActivity(intent);
                // methods.showInterAd(position, "");
            }
        });
        /*if (holder instanceof MyViewHolder) {

        } else {
            if (isAdLoaded) {
                try {
                    if (((ADViewHolder) holder).rl_native_ad.getChildCount() == 0) {
                        switch (Constant.nativeAdType) {
                            case Constant.AD_TYPE_ADMOB:
                            case Constant.AD_TYPE_FACEBOOK:
                                if (mNativeAdsAdmob.size() >= 1) {

                                    int i = new Random().nextInt(mNativeAdsAdmob.size() - 1);

                                    NativeAdView adView = (NativeAdView) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_admob, null);
                                    populateUnifiedNativeAdView(mNativeAdsAdmob.get(i), adView);
                                    ((ADViewHolder) holder).rl_native_ad.removeAllViews();
                                    ((ADViewHolder) holder).rl_native_ad.addView(adView);

                                    ((ADViewHolder) holder).rl_native_ad.setVisibility(View.VISIBLE);
                                }
                                break;
                            case Constant.AD_TYPE_STARTAPP:
                                int i = new Random().nextInt(nativeAdsStartApp.size() - 1);

                                RelativeLayout nativeAdView = (RelativeLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_startapp, null);
                                populateStartAppNativeAdView(nativeAdsStartApp.get(i), nativeAdView);

                                ((ADViewHolder) holder).rl_native_ad.removeAllViews();
                                ((ADViewHolder) holder).rl_native_ad.addView(nativeAdView);
                                ((ADViewHolder) holder).rl_native_ad.setVisibility(View.VISIBLE);
                                break;
                            case Constant.AD_TYPE_APPLOVIN:
                                MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader(Constant.nativeAdID, context);
                                nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                                    @Override
                                    public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                                        nativeAdView.setPadding(0, 0, 0, 10);
                                        nativeAdView.setBackgroundColor(Color.WHITE);
                                        ((ADViewHolder) holder).rl_native_ad.removeAllViews();
                                        ((ADViewHolder) holder).rl_native_ad.addView(nativeAdView);
                                        ((ADViewHolder) holder).rl_native_ad.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                                    }

                                    @Override
                                    public void onNativeAdClicked(final MaxAd ad) {
                                    }
                                });

                                nativeAdLoader.loadAd();
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }*/
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayList.get(position) != null) {
            return position;
        } else {
            return VIEW_ADS;
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public boolean isHeader(int position) {
        return arrayList.get(position) == null;
    }

    public int getPos(int position) {
        return arrayListAdLess.indexOf(arrayList.get(position));
    }

    public String getLayout(int position) {
        return arrayListAdLess.get(position).getLayout();
    }

    /*private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);
    }

    private void populateStartAppNativeAdView(NativeAdDetails nativeAdDetails, RelativeLayout nativeAdView) {
        ImageView icon = nativeAdView.findViewById(R.id.icon);
        TextView title = nativeAdView.findViewById(R.id.title);
        TextView description = nativeAdView.findViewById(R.id.description);
        Button button = nativeAdView.findViewById(R.id.button);

        icon.setImageBitmap(nativeAdDetails.getImageBitmap());
        title.setText(nativeAdDetails.getTitle());
        description.setText(nativeAdDetails.getDescription());
        button.setText(nativeAdDetails.isApp() ? "Install" : "Open");
    }

    public void addAds(NativeAd nativeAd) {
        mNativeAdsAdmob.add(nativeAd);
        isAdLoaded = true;
    }

    public void addNativeAds(ArrayList<NativeAdDetails> nativeAdDetails) {
        nativeAdsStartApp.addAll(nativeAdDetails);
        isAdLoaded = true;
    }

    public void setNativeAds(boolean isLoaded) {
        isAdLoaded = isLoaded;
    }*/

    /*public void destroyNativeAds() {
        try {
            for (int i = 0; i < mNativeAdsAdmob.size(); i++) {
                mNativeAdsAdmob.get(i).destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /*InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            Constant.arrayList_wallpaper.clear();
            Constant.arrayList_wallpaper.addAll(arrayListAdLess);
            Intent intent = new Intent(context, SingleWallpaper.class);
            intent.putExtra("pos", getPos(position));
            context.startActivity(intent);
        }
    };*/
}