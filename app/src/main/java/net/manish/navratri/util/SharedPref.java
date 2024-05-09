package net.manish.navratri.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static String TAG_NIGHT_MODE = "nightmode", TAG_AD_IS_BANNER = "isbanner", TAG_AD_IS_INTER = "isinter",
    TAG_AD_IS_NATIVE = "isnative", TAG_AD_ID_BANNER = "id_banner", TAG_AD_ID_INTER = "id_inter", TAG_AD_ID_NATIVE = "id_native",
    TAG_AD_NATIVE_POS = "native_pos", TAG_AD_INTER_POS = "inter_pos", TAG_AD_TYPE_BANNER = "type_banner", TAG_AD_TYPE_INTER = "type_inter",
    TAG_AD_TYPE_NATIVE = "type_native", TAG_STARTAPP_ID = "startapp_id";

    public SharedPref(Context context) {
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public Boolean getIsNotification() {
        return sharedPreferences.getBoolean("noti", true);
    }

    public void setIsNotification(Boolean isNotification) {
        editor.putBoolean("noti", isNotification);
        editor.apply();
    }

    public void setIsFirst(Boolean flag) {
        editor.putBoolean("firstopen", flag);
        editor.apply();
    }

    public Boolean getIsFirst() {
        return sharedPreferences.getBoolean("firstopen", true);
    }



    public String getDarkMode() {
        return sharedPreferences.getString(TAG_NIGHT_MODE, Constant.DARK_MODE_SYSTEM);
    }

    public void setDarkMode(String nightMode) {
        editor.putString(TAG_NIGHT_MODE, nightMode);
        editor.apply();
    }

    public void setAdDetails(boolean isBanner, boolean isInter, boolean isNative, String typeBanner, String typeInter, String typeNative,
                             String idBanner, String idInter, String idNative, String startapp_id, int interPos, int nativePos) {
        editor.putBoolean(TAG_AD_IS_BANNER, isBanner);
        editor.putBoolean(TAG_AD_IS_INTER, isInter);
        editor.putBoolean(TAG_AD_IS_NATIVE, isNative);
        editor.putString(TAG_AD_TYPE_BANNER, Methods.encrypt(typeBanner));
        editor.putString(TAG_AD_TYPE_INTER, Methods.encrypt(typeInter));
        editor.putString(TAG_AD_TYPE_NATIVE, Methods.encrypt(typeNative));
        editor.putString(TAG_AD_ID_BANNER, Methods.encrypt(idBanner));
        editor.putString(TAG_AD_ID_INTER, Methods.encrypt(idInter));
        editor.putString(TAG_AD_ID_NATIVE, Methods.encrypt(idNative));
        editor.putString(TAG_STARTAPP_ID, Methods.encrypt(startapp_id));
        editor.putInt(TAG_AD_INTER_POS, interPos);
        editor.putInt(TAG_AD_NATIVE_POS, nativePos);
        editor.apply();
    }
}