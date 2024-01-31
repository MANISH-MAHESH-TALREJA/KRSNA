package net.manish.navratri.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/*import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.ads.mediation.facebook.FacebookAdapter;
import com.google.ads.mediation.facebook.FacebookExtras;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.NativeAd;
import com.startapp.sdk.ads.nativead.NativeAdPreferences;
import com.startapp.sdk.ads.nativead.StartAppNativeAd;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;*/
import net.manish.navratri.adapter.AdapterRingtone;
import net.manish.navratri.asyncTask.LoadRingtone;
import net.manish.navratri.R;
import net.manish.navratri.interfaces.RingtoneListener;
import net.manish.navratri.item.ItemRingtone;
import net.manish.navratri.util.Constant;
import net.manish.navratri.util.Methods;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentRingtone extends Fragment {

    private Methods methods;
    private RecyclerView recyclerView;
    private ArrayList<ItemRingtone> arrayList;
    private AdapterRingtone adapterRingtone;
    private CircularProgressBar progressBar;

    private TextView textView_empty;
    private AppCompatButton button_try;
    private LinearLayout ll_empty;
    private String errr_msg;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_ringtone, container, false);

        methods = new Methods(getActivity());

        arrayList = new ArrayList<>();

        ll_empty = rootView.findViewById(R.id.ll_empty);
        textView_empty = rootView.findViewById(R.id.textView_empty_msg);
        button_try = rootView.findViewById(R.id.button_empty_try);
        errr_msg = getString(R.string.no_ringtones_found);

        progressBar = rootView.findViewById(R.id.pb_ringtone);
        recyclerView = rootView.findViewById(R.id.rv_ringtone);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        button_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        loadData();

        setHasOptionsMenu(true);
        return rootView;
    }

    private void loadData() {
        if (methods.isNetworkAvailable()) {
            LoadRingtone loadRingtone = new LoadRingtone(new RingtoneListener() {
                @Override
                public void onStart() {
                    arrayList.clear();
                    ll_empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemRingtone> arrayListRingtone) {
                    if (getActivity() != null) {

                        // arrayListRingtone.addAll(arrayListRingtone);
                        /*arrayListRingtone.addAll(arrayListRingtone);
                        arrayListRingtone.addAll(arrayListRingtone);
                        arrayListRingtone.addAll(arrayListRingtone);
                        arrayListRingtone.addAll(arrayListRingtone);
                        arrayListRingtone.addAll(arrayListRingtone);*/

                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                arrayList.addAll(arrayListRingtone);
                                errr_msg = getString(R.string.no_ringtones_found);
                                setAdapter();
                            } else {
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            }
                        } else {
                            errr_msg = getString(R.string.server_no_conn);
                            setEmpty();
                        }
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_RINGTONE, 0, "", "", ""));

            loadRingtone.execute();
        } else {
            errr_msg = getString(R.string.net_not_conn);
            setEmpty();
        }
    }


    private void setAdapter() {
        adapterRingtone = new AdapterRingtone(getActivity(), arrayList);
        recyclerView.setAdapter(adapterRingtone);
        setEmpty();

        //loadNativeAds();
    }

    private void setEmpty() {
        progressBar.setVisibility(View.GONE);
        if (arrayList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText(errr_msg);
            recyclerView.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        if (adapterRingtone != null) {
            adapterRingtone.stopMediaPlayer();
        }
        super.onDestroy();
    }

    /*@SuppressLint("MissingPermission")
    private void loadNativeAds() {
        if (Constant.isNativeAd && arrayList.size() >= 10) {
            switch (Constant.nativeAdType) {
                case Constant.AD_TYPE_ADMOB:
                case Constant.AD_TYPE_FACEBOOK:
                    AdLoader.Builder builder = new AdLoader.Builder(getActivity(), Constant.nativeAdID);
                    AdLoader adLoader = builder.forNativeAd(
                            new NativeAd.OnNativeAdLoadedListener() {
                                @Override
                                public void onNativeAdLoaded(@NotNull NativeAd nativeAd) {
                                    // A native ad loaded successfully, check if the ad loader has finished loading
                                    // and if so, insert the ads into the list.
                                    try {
                                        adapterRingtone.addAds(nativeAd);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).build();

                    // Load the Native Express ad.
                    Bundle extras = new Bundle();
                    if (ConsentInformation.getInstance(getActivity()).getConsentStatus() != ConsentStatus.PERSONALIZED) {
                        extras.putString("npa", "1");
                    }
                    AdRequest adRequest = new AdRequest.Builder()
                            .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                            .addNetworkExtrasBundle(FacebookAdapter.class, new FacebookExtras().build())
                            .build();

                    adLoader.loadAds(adRequest, 5);
                    break;
                case Constant.AD_TYPE_STARTAPP:
                    final StartAppNativeAd nativeAd = new StartAppNativeAd(getActivity());

                    nativeAd.loadAd(new NativeAdPreferences()
                            .setAdsNumber(3)
                            .setAutoBitmapDownload(true)
                            .setPrimaryImageSize(2), new AdEventListener() {
                        @Override
                        public void onReceiveAd(Ad ad) {
                            try {
                                adapterRingtone.addNativeAds(nativeAd.getNativeAds());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailedToReceiveAd(Ad ad) {
//                            Log.e("aaa", "ad error");
                        }
                    });
                    break;
                case Constant.AD_TYPE_APPLOVIN:
                    adapterRingtone.setNativeAds(true);
                    break;
            }
        }
    }*/
}