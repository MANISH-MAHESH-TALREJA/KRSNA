package net.manish.navratri.util;

import android.net.Uri;

import net.manish.navratri.BuildConfig;
import net.manish.navratri.item.ItemAbout;
import net.manish.navratri.item.ItemMessage;
import net.manish.navratri.item.ItemWallpaper;

import java.io.Serializable;
import java.util.ArrayList;

public class Constant implements Serializable {

    private static final long serialVersionUID = 1L;

    //server url
    public static String SERVER_URL = BuildConfig.SERVER_URL + "api.php";

    public static final String SERVER_IMAGE = BuildConfig.SERVER_URL + "images/";

    public static final String METHOD_ABOUT = "get_app_details";
    public static final String METHOD_WALLPAPER = "get_wallpaper";
    public static final String METHOD_MOST_VIEWED_WALLPAPER = "get_wallpaper_view";
    public static final String METHOD_RINGTONE = "get_ringtone";
    public static final String METHOD_VIDEOS = "get_videos";
    public static final String METHOD_QUIZ = "get_quiz";
    public static final String METHOD_QUOTES = "get_sms";

    public static final String METHOD_SINGLE_WALL = "get_single_wallpaper";
    public static final String METHOD_SINGLE_RINGTONE = "get_single_ringtone";
    public static final String METHOD_SINGLE_QUIZ = "get_single_quiz";
    public static final String METHOD_SINGLE_QUOTES = "get_single_sms";

    public static final String TAG_ROOT = "CHRISTMAS_APP";
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_MSG = "msg";

    public static final String TAG_WALL_NAME = "wall_name";
    public static final String TAG_WALL_IMAGE_BIG= "image_b";
    public static final String TAG_WALL_LAYOUT= "wall_layout";

    public static final String LATESTMSG_ID = "id";
    public static final String LATESTMSG_URL = "sms";
    public static final String LATESTMSG_NUM = "num";

    public static final String LATEST_VIDEO_ID = "id";
    public static final String LATEST_VIDEO_URL = "video_url";
    public static final String LATEST_VIDEO_TYPE = "video_type";
    public static final String LATEST_VIDEO_LAYOUT = "video_orientation";
    public static final String LATEST_VIDEO_TITLE = "video_title";
    public static final String LATEST_VIDEO_IMAGE = "video_image";

    public static final String QUIZ_ID = "id";
    public static final String QUIZ_QUES = "quiz_title";
    public static final String QUIZ_A = "option1";
    public static final String QUIZ_B = "option2";
    public static final String QUIZ_C = "option3";
    public static final String QUIZ_D = "option4";
    public static final String QUIZ_ANS = "quiz_ans";

    public static final String TAG_ID = "id";
    public static final String TAG_RING_NAME = "ringtone_name";
    public static final String TAG_RING_URL = "ringtone_link";
    public static final String TAG_RING_DOWNLOAD = "download";
    public static final String TAG_TAGS = "tags";

    public static final String DARK_MODE_ON = "on";
    public static final String DARK_MODE_OFF = "off";
    public static final String DARK_MODE_SYSTEM = "system";

    // Number of columns of Grid View
    public static final int NUM_OF_COLUMNS = 3;
    public static String DEVICE_ID;

    public static Uri uri_set;
    public static ArrayList<ItemWallpaper> arrayList_wallpaper = new ArrayList<>();
    public static ArrayList<ItemMessage> arrayList_message = new ArrayList<>();

    public static ItemAbout itemAbout;

    public static Boolean showUpdateDialog = false, appUpdateCancel = false;
    public static String packageName = "",
            appVersion= "", appUpdateMsg = "", appUpdateURL = "";


    public static boolean isQuizEnabled = true;
    public static boolean isWallpaperEnabled = true;
    public static boolean isRingToneEnabled = true;
    public static boolean isMessageEnabled = true;
}