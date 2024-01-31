package net.manish.navratri.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import net.manish.navratri.R;
import net.manish.navratri.asyncTask.LoadAbout;
import net.manish.navratri.fragments.EkadashiFragment;
import net.manish.navratri.fragments.FragmentDashBoard;
import net.manish.navratri.fragments.FragmentHome;
import net.manish.navratri.fragments.FragmentMessage;
import net.manish.navratri.fragments.FragmentRingtone;
import net.manish.navratri.fragments.FragmentWallpaper;
import net.manish.navratri.interfaces.AboutListener;
import net.manish.navratri.interfaces.AdConsentListener;
/*import net.manish.navratri.util.AdConsent;
import net.manish.navratri.util.AdManagerInterAdmob;
import net.manish.navratri.util.AdManagerInterApplovin;
import net.manish.navratri.util.AdManagerInterStartApp;*/
import net.manish.navratri.util.Constant;
import net.manish.navratri.util.DBHelper;
import net.manish.navratri.util.Methods;
import net.manish.navratri.util.SharedPref;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Methods methods;
    DrawerLayout drawer;
    FragmentManager fm;
    ActionBarDrawerToggle toggle;
    DBHelper dbHelper;
    LoadAbout loadAbout;
    //AdConsent adConsent;
    LinearLayout ll_adView;
    Boolean isTime = false;
    NavigationView navigationView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();

        dbHelper = new DBHelper(this);

        ll_adView = findViewById(R.id.ll_adView);
        methods = new Methods(this);
        methods.setStatusColor(getWindow());
        methods.forceRTLIfSupported(getWindow());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getString(R.string.app_name));

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Constant.DEVICE_ID = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);

        /*adConsent = new AdConsent(this, new AdConsentListener() {
            @Override
            public void onConsentUpdate() {
                methods.showBannerAd(ll_adView);
            }
        });*/

        FragmentDashBoard fragmentDashBoard = new FragmentDashBoard();
        loadFrag(fragmentDashBoard, getString(R.string.app_name), fm);

        if (methods.isNetworkAvailable()) {
            loadAboutData();
        } else {
            /*adConsent.checkForConsent();*/
            dbHelper.getAbout();
            /*methods.showBannerAd(ll_adView);*/
            methods.showToast(getString(R.string.net_not_conn));
        }

        setNavMenu();

        if(!methods.getPerNotificationStatus()) {
            methods.permissionDialog();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            FragmentHome fragmentHome = new FragmentHome();
            loadFrag(fragmentHome, getString(R.string.home), fm);
        } else if (id == R.id.nav_wallpapers) {
            FragmentWallpaper fragmentWallpaper = new FragmentWallpaper();
            loadFrag(fragmentWallpaper, "KRSNA IMAGES", fm);
        } else if (id == R.id.nav_ringtones) {
            FragmentRingtone fragmentRingtone = new FragmentRingtone();
            loadFrag(fragmentRingtone, "Kirtan", fm);
        } else if (id == R.id.nav_messages) {
            FragmentMessage fragmentMessage = new FragmentMessage();
            loadFrag(fragmentMessage, getString(R.string.message), fm);
        } else if (id == R.id.nav_quiz) {
            showQuiz();
        } else if (id == R.id.nav_rate) {
            final String appName = getPackageName();//your application package name i.e play store application url
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id="
                                + appName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id="
                                + appName)));
            }
        } else if (id == R.id.nav_shareapp) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + " - " + "http://play.google.com/store/apps/details?id=" + getPackageName());
            startActivity(share);

        } else if (id == R.id.nav_settings) {
            this.finishAffinity();
        }
        else if (id == R.id.nav_about_us) {
            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_privacy) {
            openPrivacyDialog();
        }
        else if (id == R.id.nav_more) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps))));
        }
        else if (id == R.id.nav_ekadashi) {
            EkadashiFragment ekadashiFragment = new EkadashiFragment();
            loadFrag(ekadashiFragment, "Ekadashi Calendar 2024", fm);
        }
        else
        {
            FragmentHome fragmentHome = new FragmentHome();
            loadFrag(fragmentHome, getString(R.string.home), fm);
        }
        navigationView.setCheckedItem(item.getItemId());
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openPrivacyDialog() {
        Dialog dialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new Dialog(MainActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            dialog = new Dialog(MainActivity.this);
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_privacy);

        WebView webview = dialog.findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        String mimeType = "text/html;charset=UTF-8";
        String encoding = "utf-8";

        if (Constant.itemAbout != null) {
            String text = "";
            if (methods.isDarkMode()) {
                text = "<html><head>"
                        + "<style> body{color:#fff !important;text-align:left}"
                        + "</style></head>"
                        + "<body>"
                        + Constant.itemAbout.getPrivacy()
                        + "</body></html>";
            } else {
                text = "<html><head>"
                        + "<style> body{color:#000 !important;text-align:left}"
                        + "</style></head>"
                        + "<body>"
                        + Constant.itemAbout.getPrivacy()
                        + "</body></html>";
            }

            webview.setBackgroundColor(Color.TRANSPARENT);
            webview.loadDataWithBaseURL("blarg://ignored", text, mimeType, encoding, "");
        }

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void setNavMenu() {
        if (!Constant.isQuizEnabled) {
            navigationView.getMenu().findItem(R.id.nav_quiz).setVisible(false);
        }
        if (!Constant.isWallpaperEnabled) {
            navigationView.getMenu().findItem(R.id.nav_wallpapers).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_home).setVisible(false);
        }
        if (!Constant.isRingToneEnabled) {
            navigationView.getMenu().findItem(R.id.nav_ringtones).setVisible(false);
        }
        if (!Constant.isMessageEnabled) {
            navigationView.getMenu().findItem(R.id.nav_messages).setVisible(false);
        }
    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {

        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        FragmentTransaction ft = fm.beginTransaction();
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        if (name.equals(getString(R.string.christmas)) || name.equals(getString(R.string.app_name))) {
            ft.replace(R.id.frame_nav, f1, name);
        } else {
            ft.hide(fm.getFragments().get(fm.getBackStackEntryCount()));
            ft.add(R.id.frame_nav, f1, name);
            ft.addToBackStack(name);
        }

        ft.commit();
        getSupportActionBar().setTitle(name);
    }

    public void loadAboutData() {
        loadAbout = new LoadAbout(new AboutListener() {
            @Override
            public void onStart()
            {

            }

            @Override
            public void onEnd(String success, String verifyStatus, String message) {
                if (success.equals("1")) {
                    String version = "";
                    try {
                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        version = String.valueOf(pInfo.versionCode);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (Constant.showUpdateDialog && !Constant.appVersion.equals(version)) {
                        methods.showUpdateAlert(Constant.appUpdateMsg);
                    } else {
                        // adConsent.checkForConsent();
                        dbHelper.addtoAbout();
                        // new AdManagerInterAdmob(MainActivity.this).createAd();
                    }

                    // methods.initializeAds();

                    /*new SharedPref(MainActivity.this).setAdDetails(Constant.isBannerAd, Constant.isInterstitialAd, Constant.isNativeAd, Constant.bannerAdType,
                            Constant.interstitialAdType, Constant.nativeAdType, Constant.bannerAdID, Constant.interstitialAdID, Constant.nativeAdID, Constant.startapp_id, Constant.interstitialAdShow, Constant.nativeAdShow);

                    if (Constant.isInterstitialAd) {
                        switch (Constant.interstitialAdType) {
                            case Constant.AD_TYPE_ADMOB:
                            case Constant.AD_TYPE_FACEBOOK:
                                AdManagerInterAdmob adManagerInterAdmob = new AdManagerInterAdmob(getApplicationContext());
                                adManagerInterAdmob.createAd();
                                break;
                            case Constant.AD_TYPE_STARTAPP:
                                AdManagerInterStartApp adManagerInterStartApp = new AdManagerInterStartApp(getApplicationContext());
                                adManagerInterStartApp.createAd();
                                break;
                            case Constant.AD_TYPE_APPLOVIN:
                                AdManagerInterApplovin adManagerInterApplovin = new AdManagerInterApplovin(MainActivity.this);
                                adManagerInterApplovin.createAd();
                                break;
                        }
                    }*/
                }
            }
        }, methods.getAPIRequest(Constant.METHOD_ABOUT, 0, "", "", ""));
        loadAbout.execute();
    }

    private void showQuiz() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle(getString(R.string.quiz));
        dialog.setContentView(R.layout.layout_quiz);

        final AppCompatSpinner spinner = dialog.findViewById(R.id.spinner_no_que);
        final AppCompatSpinner spinner_time = dialog.findViewById(R.id.spinner_time);
        Button button = dialog.findViewById(R.id.button_goquiz);
        final LinearLayout ll_time = dialog.findViewById(R.id.ll_time);
        final AppCompatCheckBox checkBox = dialog.findViewById(R.id.checkBox_timer);
        final LinearLayout ll_checkBox = dialog.findViewById(R.id.ll_checkbox);

        ll_checkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setChecked(!checkBox.isChecked());
            }
        });

        String[] time = getResources().getStringArray(R.array.time);
        String[] ques = getResources().getStringArray(R.array.array_no_que);

        CustomAdapter customAdapter = new CustomAdapter(dialog.getContext(), time);
        CustomAdapter customAdapter_ques = new CustomAdapter(dialog.getContext(), ques);
        spinner_time.setAdapter(customAdapter);
        spinner.setAdapter(customAdapter_ques);

        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isTime = isChecked;
                if (isChecked) {
                    ll_time.setVisibility(View.VISIBLE);
                } else {
                    ll_time.setVisibility(View.GONE);
                }
            }
        });

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (methods.isNetworkAvailable()) {
                    Intent intentabout = new Intent(getApplicationContext(), QuizActivity.class);
                    intentabout.putExtra("que", Integer.parseInt(spinner.getSelectedItem().toString()));
                    intentabout.putExtra("istime", checkBox.isChecked());
                    intentabout.putExtra("time", (Integer.parseInt(spinner_time.getSelectedItem().toString()) * Integer.parseInt(spinner.getSelectedItem().toString())));
                    startActivity(intentabout);
                    dialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.net_not_conn), Toast.LENGTH_SHORT).show();
                }
            }
        });


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.slide_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void exitDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this, R.style.ThemeDialog);
        alert.setTitle(R.string.app_name);
        alert.setIcon(R.mipmap.app_icon);
        alert.setMessage(getString(R.string.sure_quit));

        alert.setPositiveButton(getString(R.string.exit),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                });

        alert.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

        alert.setNeutralButton(getString(R.string.rate_app),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String appName = getPackageName();//your application package name i.e play store application url
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id="
                                            + appName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id="
                                            + appName)));
                        }

                    }
                });
        alert.show();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fm.getBackStackEntryCount() != 0) {
            String title = fm.getFragments().get(fm.getBackStackEntryCount() - 1).getTag();
            if (fm.getFragments().get(fm.getBackStackEntryCount()).getTag().equals(getString(R.string.home))) {
                navigationView.getMenu().findItem(R.id.nav_home).setChecked(false);
            } else if (fm.getFragments().get(fm.getBackStackEntryCount()).getTag().equals(getString(R.string.wallpaper))) {
                navigationView.getMenu().findItem(R.id.nav_wallpapers).setChecked(false);
            } else if (fm.getFragments().get(fm.getBackStackEntryCount()).getTag().equals(getString(R.string.ringtone))) {
                navigationView.getMenu().findItem(R.id.nav_ringtones).setChecked(false);
            } else if (fm.getFragments().get(fm.getBackStackEntryCount()).getTag().equals(getString(R.string.message))) {
                navigationView.getMenu().findItem(R.id.nav_messages).setChecked(false);
            }
            getSupportActionBar().setTitle(title);

            super.onBackPressed();
        } else {
            exitDialog();
        }
    }

    public class CustomAdapter extends BaseAdapter {
        Context context;
        String[] countryNames;
        LayoutInflater inflter;

        CustomAdapter(Context applicationContext, String[] countryNames) {
            this.context = applicationContext;
            this.countryNames = countryNames;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return countryNames.length;
        }

        @Override
        public Object getItem(int i) {
            return countryNames[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.layout_spinner, null);
            TextView textView = view.findViewById(R.id.textView_spnr);
            textView.setText(countryNames[i]);
            return view;
        }
    }
}