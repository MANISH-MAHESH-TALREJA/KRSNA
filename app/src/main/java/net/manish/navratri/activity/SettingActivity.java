package net.manish.navratri.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.onesignal.OneSignal;

import net.manish.navratri.R;
import net.manish.navratri.util.Constant;
import net.manish.navratri.util.Methods;
import net.manish.navratri.util.SharedPref;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SettingActivity extends AppCompatActivity {

    Toolbar toolbar;
    SharedPref sharedPref;
    Methods methods;
    LinearLayout ll_theme, ll_noti_permission;
    SwitchCompat switch_consent, switch_noti;
    Boolean isNoti = true, isLoaded = false;
    TextView textView_moreapp, textView_privacy, textView_about, tv_theme;
    ImageView iv_theme;
    String them_mode = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sharedPref = new SharedPref(this);
        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());

        isNoti = sharedPref.getIsNotification();
        findViewById(R.id.tv_noti_permission).setVisibility(methods.getPerNotificationStatus() ? View.GONE : View.VISIBLE);
        them_mode = methods.getDarkMode();

        toolbar = this.findViewById(R.id.toolbar_setting);
        toolbar.setTitle(getString(R.string.settings));
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ll_theme = findViewById(R.id.ll_theme);
        ll_noti_permission = findViewById(R.id.ll_noti_per);
        switch_noti = findViewById(R.id.switch_noti);
        switch_consent = findViewById(R.id.switch_consent);
        textView_moreapp = findViewById(R.id.textView_moreapp);
        textView_about = findViewById(R.id.textView_about);
        textView_privacy = findViewById(R.id.textView_privacy);
        tv_theme = findViewById(R.id.tv_theme);
        iv_theme = findViewById(R.id.iv_theme);

        switch_noti.setChecked(isNoti);

        if (methods.isDarkMode()) {
            iv_theme.setImageResource(R.mipmap.mode_dark);
        } else {
            iv_theme.setImageResource(R.mipmap.mode_icon);
        }

        String mode = methods.getDarkMode();
        switch (mode) {
            case Constant.DARK_MODE_SYSTEM:
                tv_theme.setText(getString(R.string.system_default));
                break;
            case Constant.DARK_MODE_OFF:
                tv_theme.setText(getString(R.string.light));
                break;
            case Constant.DARK_MODE_ON:
                tv_theme.setText(getString(R.string.dark));
                break;
        }

        ll_noti_permission.setOnClickListener(v ->
        {
            if(!methods.getPerNotificationStatus()) {
                methods.checkPerNotification();
            }
        });

        switch_noti.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            OneSignal.getUser().getPushSubscription().optOut();
            //OneSignal.disablePush(!isChecked);
            sharedPref.setIsNotification(isChecked);
        });

        switch_consent.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
        });

        textView_moreapp.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps)))));

        textView_about.setOnClickListener(v ->
        {
            Intent intent = new Intent(SettingActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        textView_privacy.setOnClickListener(v -> openPrivacyDialog());


        ll_theme.setOnClickListener(v -> openThemeDialog());

        isLoaded = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home)
        {
            onBackPressed();
        } else
        {
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void openThemeDialog() {
        final Dialog dialog = new Dialog(SettingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_theme);
        if (getString(R.string.isRTL).equals("true")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup_them);
        MaterialButton btn_ok = dialog.findViewById(R.id.textView_ok_them);
        MaterialButton btn_cancel = dialog.findViewById(R.id.textView_cancel_them);

        String mode = methods.getDarkMode();
        assert mode != null;
        switch (mode) {
            case Constant.DARK_MODE_SYSTEM:
                radioGroup.check(radioGroup.getChildAt(0).getId());
                break;
            case Constant.DARK_MODE_OFF:
                radioGroup.check(radioGroup.getChildAt(1).getId());
                break;
            case Constant.DARK_MODE_ON:
                radioGroup.check(radioGroup.getChildAt(2).getId());
                break;
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                MaterialRadioButton rb = group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    if (checkedId == R.id.radioButton_system_them) {
                        them_mode = Constant.DARK_MODE_SYSTEM;
                    } else if (checkedId == R.id.radioButton_light_them) {
                        them_mode = Constant.DARK_MODE_OFF;
                    } else if (checkedId == R.id.radioButton_dark_them) {
                        them_mode = Constant.DARK_MODE_ON;
                    }
                }
            }
        });

        btn_ok.setOnClickListener(v ->
        {
            sharedPref.setDarkMode(them_mode);
            switch (them_mode) {
                case Constant.DARK_MODE_SYSTEM:
                    tv_theme.setText(getResources().getString(R.string.system_default));
                    break;
                case Constant.DARK_MODE_OFF:
                    tv_theme.setText(getResources().getString(R.string.light));
                    break;
                case Constant.DARK_MODE_ON:
                    tv_theme.setText(getResources().getString(R.string.dark));
                    break;
                default:
                    break;
            }
            dialog.dismiss();

            String mode1 = sharedPref.getDarkMode();
            switch (mode1) {
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

        });

        btn_cancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }

    public void openPrivacyDialog() {
        Dialog dialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new Dialog(SettingActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            dialog = new Dialog(SettingActivity.this);
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_privacy);

        WebView webview = dialog.findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        String mimeType = "text/html;charset=UTF-8";
        String encoding = "utf-8";

        if (Constant.itemAbout != null) {
            String text;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(permissions.length > 0 && permissions[0].equalsIgnoreCase("android.permission.post_notifications")) {
            if(grantResults.length >0 && grantResults[0] != -1) {
                findViewById(R.id.tv_noti_permission).setVisibility(View.GONE);
            }
        }
    }
}