package net.manish.navratri.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import net.manish.navratri.R;
import net.manish.navratri.asyncTask.LoadAbout;
import net.manish.navratri.interfaces.AboutListener;
import net.manish.navratri.util.Constant;
import net.manish.navratri.util.DBHelper;
import net.manish.navratri.util.Methods;
import net.manish.navratri.util.SharedPref;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/*import com.appolica.flubber.Flubber;*/

import pl.droidsonroids.gif.GifImageView;
import yanzhikai.textpath.SyncTextPathView;

public class SplashActivity extends AppCompatActivity {

    Methods methods;
    DBHelper dbHelper;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        TextView text = findViewById(R.id.mainText);
        GifImageView image = findViewById(R.id.startImage);

        SyncTextPathView syncTextPathView3 = findViewById(R.id.fancy_text3);
        syncTextPathView3.startAnimation(0,1);
        SyncTextPathView syncTextPathView4 = findViewById(R.id.fancy_text4);
        syncTextPathView4.startAnimation(0,1);


        dbHelper = new DBHelper(this);
        sharedPref = new SharedPref(this);
        methods = new Methods(this);
        methods.setStatusColor(getWindow());

        new Handler().postDelayed(() ->
        {
            if (sharedPref.getIsFirst()) {
                loadAboutData();
            } else {
                openMainActivity();
            }
        }, 4000);
    }

    public void loadAboutData() {
        if (methods.isNetworkAvailable()) {
            LoadAbout loadAbout = new LoadAbout(new AboutListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String verifyStatus, String message) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            String version = "";
                            try {
                                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                version = String.valueOf(pInfo.versionCode);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            if(Constant.showUpdateDialog && !Constant.appVersion.equals(version)) {
                                methods.showUpdateAlert(Constant.appUpdateMsg);
                            } else {
                                dbHelper.addtoAbout();
                                openMainActivity();
                            }

                            sharedPref.setAdDetails(Constant.isBannerAd, Constant.isInterstitialAd, Constant.isNativeAd, Constant.bannerAdType,
                                    Constant.interstitialAdType, Constant.nativeAdType, Constant.bannerAdID, Constant.interstitialAdID, Constant.nativeAdID, Constant.startapp_id, Constant.interstitialAdShow, Constant.nativeAdShow);
                        } else {
                            errorDialog(getString(R.string.error_unauth_access), message);
                        }
                    } else {
                        errorDialog(getString(R.string.server_error), getString(R.string.server_no_conn));
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_ABOUT, 0, "", "", ""));
            loadAbout.execute();
        } else {
            errorDialog(getString(R.string.internet_not_connected), getString(R.string.error_connect_net_tryagain));
        }
    }

    private void errorDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this, R.style.ThemeDialog);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        if (title.equals(getString(R.string.internet_not_connected)) || title.equals(getString(R.string.server_error))) {
            alertDialog.setNegativeButton(getString(R.string.try_again), (dialog, which) -> loadAboutData());
        }

        alertDialog.setPositiveButton(getString(R.string.exit), (dialog, which) -> finish());
        alertDialog.show();
    }


    private void openMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}