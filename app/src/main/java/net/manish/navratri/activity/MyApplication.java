package net.manish.navratri.activity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

/*import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;*/
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;

import net.manish.navratri.R;
import net.manish.navratri.util.Constant;
import net.manish.navratri.util.DBHelper;
import net.manish.navratri.util.Methods;
import net.manish.navratri.util.SharedPref;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class MyApplication extends Application {

    SharedPref sharedPref;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseAnalytics.getInstance(getApplicationContext());

        OneSignal.initWithContext(this);
        OneSignal.setAppId(getString(R.string.onesignal_app_id));

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                //.setDefaultFontPath("fonts/pop_reg.ttf")
                                //.setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        dbHelper.onCreate(dbHelper.getWritableDatabase());

        sharedPref = new SharedPref(this);
        String mode = sharedPref.getDarkMode();
        switch (mode) {
            case Constant.DARK_MODE_SYSTEM:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case Constant.DARK_MODE_OFF:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case Constant.DARK_MODE_ON:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }

        sharedPref.getAdDetails();
        // new Methods(getApplicationContext()).initializeAds();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}