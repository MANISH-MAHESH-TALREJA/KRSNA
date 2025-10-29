package net.manish.navratri.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.manish.navratri.BuildConfig;
import net.manish.navratri.R;
import net.manish.navratri.activity.SetAsWallpaperActivity;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.Q;

public class Methods {

    private Context context;
    private static final String ALGORITHM = "Blowfish";
    private static final String MODE = "Blowfish/CBC/PKCS5Padding";
    private static final String TAG = "Methods"; // Added TAG for logging

    public Methods(Context context) {
        this.context = context;
    }

    // --- Utility Methods ---

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public int getScreenWidth() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return 0;
        Display display = wm.getDefaultDisplay();
        final Point point = new Point();
        display.getSize(point);
        return point.x;
    }

    public void forceRTLIfSupported(Window window) {
        if (context.getResources().getString(R.string.isRTL).equals("true") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    public void changeStatusBarColor(Window window) {
    }

    public void setStatusColor(Window window) {
        changeStatusBarColor(window); // Delegate to existing method
    }

    public void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public boolean isDarkMode() {
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    public String getDarkMode() {
        // Assuming SharedPref is correctly implemented elsewhere
        SharedPref sharedPref = new SharedPref(context);
        return sharedPref.getDarkMode();
    }

    // --- Encryption/Decryption ---

    public static String encrypt(String value) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(BuildConfig.ENC_KEY.getBytes(), ALGORITHM);
        try {
            Cipher cipher = Cipher.getInstance(MODE);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(BuildConfig.IV.getBytes()));
            byte[] values = cipher.doFinal(value.getBytes());
            return Base64.encodeToString(values, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, "Encryption failed: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    public static String decrypt(String value) {
        try {
            byte[] values = Base64.decode(value, Base64.DEFAULT);
            SecretKeySpec secretKeySpec = new SecretKeySpec(BuildConfig.ENC_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(MODE);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(BuildConfig.IV.getBytes()));
            return new String(cipher.doFinal(values));
        } catch (Exception e) {
            Log.e(TAG, "Decryption failed: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    // --- Media Download and Save ---

    public void saveImage(String img_url, String option, String postName) {
        new LoadShare(option, postName, FilenameUtils.getName(img_url)).execute(img_url);
    }

    public void saveRingtone(String ringtone_url, String option, String ringtoneName) {
        new LoadRingtone(ringtone_url, option, ringtoneName).execute(ringtone_url);
    }

    /**
     * Handles image download for Sharing, Wallpaper, or Saving to public storage.
     */
    public class LoadShare extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String option, filePath, postName, fileName;
        File file;

        LoadShare(String option, String postName, String fileName) {
            this.option = option;
            this.postName = postName;
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(context, android.app.AlertDialog.THEME_HOLO_LIGHT);
            String message = option.equals(context.getString(R.string.download)) ?
                    context.getResources().getString(R.string.downloading) :
                    context.getResources().getString(R.string.please_wait);
            pDialog.setMessage(message);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String urlString = strings[0];

            // 1. Determine File Path
            if (option.equals(context.getString(R.string.download))) {
                // For public download, use MediaStore (handled in saveImage) or legacy path (<Q)
                filePath = getDownloadImagePath(fileName);
            } else {
                // For temporary files (Share/Set Wallpaper), use cache
                filePath = context.getExternalCacheDir().getAbsoluteFile().getAbsolutePath() + File.separator + fileName;
            }

            file = new File(filePath);

            // Check if file already exists in the cache (for share/wallpaper)
            if (!option.equals(context.getString(R.string.download)) && file.exists()) {
                return "1"; // File exists in cache, proceed to share/set
            }

            // 2. Download Logic
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection;

                if (urlString.startsWith("https://")) {
                    urlConnection = (HttpsURLConnection) url.openConnection();
                } else {
                    urlConnection = (HttpURLConnection) url.openConnection();
                }

                urlConnection.setRequestProperty("Accept", "*/*");
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + urlConnection.getResponseCode() + " for " + urlString);
                    return "0";
                }

                try (InputStream inputStream = urlConnection.getInputStream()) {
                    if (option.equals(context.getString(R.string.download))) {
                        // Public Download: Use Bitmap and saveImage() to handle MediaStore (Q+)
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        return saveImage(bitmap, fileName, option) ? "2" : "0";
                    } else {
                        // Temporary Save (Share/Set Wallpaper): Write to cache file
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }
                        try (FileOutputStream fileOutput = new FileOutputStream(file)) {
                            byte[] buffer = new byte[1024];
                            int bufferLength;
                            while ((bufferLength = inputStream.read(buffer)) > 0) {
                                fileOutput.write(buffer, 0, bufferLength);
                            }
                            return "1"; // Download successful
                        }
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Download failed for " + urlString + ": " + e.getMessage());
                return "0";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();

            if (result.equals("0")) {
                showToast(context.getResources().getString(R.string.error_try_again));
                return;
            }

            if (option.equals(context.getString(R.string.download))) {
                showToast(context.getResources().getString(R.string.wallpaper_saved));
            } else if (option.equals(context.getString(R.string.set_as_wallpaper))) {
                if(file != null && file.exists()) {
                    Constant.uri_set = FileProvider.getUriForFile(context, context.getPackageName().concat(".fileprovider"), file);
                    Intent intent = new Intent(context, SetAsWallpaperActivity.class);
                    context.startActivity(intent);
                } else {
                    showToast(context.getResources().getString(R.string.error_try_again));
                }
            } else if (option.equals(context.getString(R.string.share))) {
                if(file != null && file.exists()) {
                    Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName().concat(".fileprovider"), file);

                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/*"); // Assuming it's an image
                    share.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.get_more_wall) + "\n" + context.getString(R.string.app_name) + " - " + "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                    share.putExtra(Intent.EXTRA_STREAM, contentUri);
                    context.startActivity(Intent.createChooser(share, context.getString(R.string.share)));
                } else {
                    showToast(context.getResources().getString(R.string.error_try_again));
                }
            }

            super.onPostExecute(result);
        }
    }

    /**
     * Handles ringtone download and saving to MediaStore.
     */
    public class LoadRingtone extends AsyncTask<String, Void, Uri> {
        private ProgressDialog pDialog;
        private String option, ringtoneName;

        LoadRingtone(String url, String option, String ringtoneName) {
            this.option = option;
            this.ringtoneName = ringtoneName;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(context, android.app.AlertDialog.THEME_HOLO_LIGHT);
            pDialog.setMessage(context.getResources().getString(R.string.please_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Uri doInBackground(String... strings) {
            String urlString = strings[0];
            String fileName = FilenameUtils.getName(urlString);

            // 1. Temporary Save (Ringtone needs to be copied to a local file first)
            File tempDir = new File(context.getExternalCacheDir(), "ringtones");
            if (!tempDir.exists()) tempDir.mkdirs();
            File tempFile = new File(tempDir, fileName);

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection;

                if (urlString.startsWith("https://")) {
                    urlConnection = (HttpsURLConnection) url.openConnection();
                } else {
                    urlConnection = (HttpURLConnection) url.openConnection();
                }

                urlConnection.setRequestProperty("Accept", "*/*");
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + urlConnection.getResponseCode());
                    return null;
                }

                try (InputStream inputStream = urlConnection.getInputStream();
                     FileOutputStream fileOutput = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[1024];
                    int bufferLength;
                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fileOutput.write(buffer, 0, bufferLength);
                    }
                }

                // 2. Save to MediaStore (Public Audio/Ringtone Storage)
                return saveAudioFileToMediaStore(tempFile, ringtoneName, option.equals(context.getString(R.string.set_as_ringtone)));

            } catch (IOException e) {
                Log.e(TAG, "Ringtone Download/Save failed: " + e.getMessage());
                return null;
            } finally {
                // Clean up temporary file (optional, but good practice)
                // if (tempFile.exists()) tempFile.delete();
            }
        }

        @Override
        protected void onPostExecute(Uri savedUri) {
            pDialog.dismiss();

            if (savedUri != null) {
                if (option.equals(context.getString(R.string.set_as_ringtone))) {
                    if (canWriteSettings()) {
                        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, savedUri);
                        showToast(context.getString(R.string.ringtone_set));
                    } else {
                        showToast(context.getString(R.string.ringtone_permission_req));
                        // Direct the user to the "Write Settings" screen
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + context.getPackageName()));
                        context.startActivity(intent);
                    }
                } else {
                    showToast(context.getString(R.string.ringtone_saved));
                }
            } else {
                showToast(context.getResources().getString(R.string.error_try_again));
            }
        }
    }

    // --- Core Storage Logic ---

    /**
     * Correctly saves a Bitmap to public storage using MediaStore (Android Q/29+) or FileOutputStream (pre-Q).
     */
    private boolean saveImage(@NonNull Bitmap bitmap, @NonNull String fileName, @NonNull String type) {
        // Use MediaStore for public downloads (type == R.string.download) on Android Q/29 and above.
        if (SDK_INT >= Q && type.equalsIgnoreCase(context.getString(R.string.download))) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            // Use RELATIVE_PATH for Q+ to save in a subfolder of Pictures
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name));
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.IS_PENDING, true);

            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                    values.put(MediaStore.Images.Media.IS_PENDING, false);
                    context.getContentResolver().update(uri, values, null, null);
                    return true;
                } catch (Exception e) {
                    Log.e(TAG, "Error saving image with MediaStore: " + e.getMessage());
                    context.getContentResolver().delete(uri, null, null); // Clean up failed entry
                    return false;
                }
            } else {
                return false;
            }
        } else {
            // Logic for temporary files or public save on older versions (< Q/API 29).
            File directory;
            if (!type.equals(context.getString(R.string.download))) {
                // Temporary File (Share/Set Wallpaper) - uses app's private external cache
                directory = context.getExternalCacheDir();
            } else {
                // Public Download on Android < Q (API 29) - Requires WRITE_EXTERNAL_STORAGE permission
                directory = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name));
            }

            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, fileName);

            try (OutputStream outputStream = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                // Trigger MediaScan for pre-Q devices
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(file));
                context.sendBroadcast(mediaScanIntent);

                return true;
            } catch (Exception e) {
                Log.e(TAG, "Error saving image to file: " + e.getMessage());
                return false;
            }
        }
    }

    /**
     * Saves a downloaded audio file to the MediaStore for use as a ringtone/notification.
     */
    private Uri saveAudioFileToMediaStore(@NonNull File tempFile, @NonNull String title, boolean isRingtone) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, title + "_" + System.currentTimeMillis());
        values.put(MediaStore.MediaColumns.TITLE, title);
        values.put(MediaStore.MediaColumns.MIME_TYPE, getMIMEType(tempFile.getAbsolutePath()));
        values.put(MediaStore.MediaColumns.SIZE, tempFile.length());
        values.put(MediaStore.Audio.Media.ARTIST, context.getString(R.string.app_name));
        values.put(MediaStore.Audio.Media.IS_MUSIC, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, !isRingtone);
        values.put(MediaStore.Audio.Media.IS_RINGTONE, isRingtone);

        if (SDK_INT >= Q) {
            // Use RELATIVE_PATH for Q+
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_RINGTONES + File.separator + context.getString(R.string.app_name));
            values.put(MediaStore.MediaColumns.IS_PENDING, true);
        } else {
            // Use DATA for pre-Q
            values.put(MediaStore.MediaColumns.DATA, getDownloadRingtonePath(tempFile.getName()));
        }

        Uri uri = context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

        if (uri != null) {
            try (OutputStream os = context.getContentResolver().openOutputStream(uri)) {
                try (InputStream is = new java.io.FileInputStream(tempFile)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = is.read(buffer)) > 0) {
                        os.write(buffer, 0, len);
                    }
                }

                if (SDK_INT >= Q) {
                    values.clear();
                    values.put(MediaStore.MediaColumns.IS_PENDING, false);
                    context.getContentResolver().update(uri, values, null, null);
                }
                return uri;

            } catch (Exception e) {
                Log.e(TAG, "Failed to save audio to MediaStore: " + e.getMessage());
                context.getContentResolver().delete(uri, null, null);
                return null;
            }
        }
        return null;
    }

    /**
     * Gets the file path for image downloads on legacy systems (< Q).
     */
    private String getDownloadImagePath(String fileName) {
        return Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name) + File.separator + fileName;
    }

    /**
     * Gets the file path for ringtone downloads on legacy systems (< Q).
     */
    private String getDownloadRingtonePath(String fileName) {
        return Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_RINGTONES + File.separator + context.getString(R.string.app_name) + File.separator + fileName;
    }

    // --- Permission Checks ---

    public Boolean checkPer() {
        return true;
        // Read Media Permissions (Needed to read files not created by the app, e.g., for picking images)
//        if (SDK_INT >= 33) {
//            // Android 14+
//            if (ContextCompat.checkSelfPermission(context, READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
//                ((Activity) context).requestPermissions(new String[]{READ_MEDIA_IMAGES}, 1);
//                return false;
//            }
//        } else if (SDK_INT == 33) {
//            // Android 13
//            if (ContextCompat.checkSelfPermission(context, READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
//                ((Activity) context).requestPermissions(new String[]{READ_MEDIA_IMAGES}, 1);
//                return false;
//            }
//        } else if (SDK_INT >= Q) {
//            // Android 10-12 (Scoped Storage enforced, don't need WRITE_EXTERNAL)
//            if (ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ((Activity) context).requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 22);
//                return false;
//            }
//        } else if (SDK_INT <= Build.VERSION_CODES.P) {
//            // Android 9 and below
//            if (ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ((Activity) context).requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 22);
//                return false;
//            }
//        }
//        return true;
    }

    public void checkPerNotification() {
        if (SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(context, POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ((Activity) context).requestPermissions(new String[]{POST_NOTIFICATIONS}, 103);
            }
        }
    }

    public Boolean getPerNotificationStatus() {
        if (SDK_INT >= 33) {
            return ContextCompat.checkSelfPermission(context, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Assume granted on older versions
    }

    // Check for WRITE_SETTINGS permission for setting ringtone (needed for API 23+)
    public boolean canWriteSettings() {
        return SDK_INT < Build.VERSION_CODES.M || Settings.System.canWrite(context);
    }

    // --- Other Methods ---

    public void permissionDialog() {
        if (SDK_INT >= 33) {
            Dialog dialog_sync = new Dialog(context, android.R.style.Theme_Material_Light_Dialog_Alert);
            dialog_sync.setCancelable(false);
            dialog_sync.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog_sync.setContentView(R.layout.layout_permission);

            MaterialButton button = dialog_sync.findViewById(R.id.button_permission);
            button.setOnClickListener(view ->
            {
                checkPerNotification();
                dialog_sync.dismiss();
            });
            dialog_sync.show();
            Window window = dialog_sync.getWindow();
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    public void showUpdateAlert(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.ThemeDialog);
        alertDialog.setTitle(context.getString(R.string.update));
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(context.getString(R.string.update), (dialog, which) ->
        {
            String url = Constant.appUpdateURL;
            if (url.equals("")) {
                url = "http://play.google.com/store/apps/details?id=" + context.getPackageName();
            }
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);

            ((Activity) context).finish();
        });
        if (Constant.appUpdateCancel) {
            alertDialog.setNegativeButton(context.getString(R.string.cancel), (dialog, which) ->
            {
            });
        } else {
            alertDialog.setNegativeButton(context.getString(R.string.exit), (dialog, which) -> ((Activity) context).finish());
        }
        alertDialog.show();
    }

    public void getVerifyDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.ThemeDialog);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton(context.getString(R.string.ok), (dialog, which) ->
        {
//                finish();
        });
        alertDialog.show();
    }

    public RequestBody getAPIRequest(String method, int page, String deviceID, String itemID, String searchText) {
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", method);
        jsObj.addProperty("package_name", context.getPackageName());

        switch (method) {
            case Constant.METHOD_SINGLE_WALL:
                jsObj.addProperty("wall_id", itemID);
                break;
            case Constant.METHOD_SINGLE_RINGTONE:
                jsObj.addProperty("ring_id", itemID);
                break;
            case Constant.METHOD_SINGLE_QUIZ:
                jsObj.addProperty("quiz_id", itemID);
                break;
            case Constant.METHOD_SINGLE_QUOTES:
                jsObj.addProperty("sms_id", itemID);
                break;
        }

        Log.e(TAG, API.toBase64(jsObj.toString()));
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("data", API.toBase64(jsObj.toString()))
                .build();
    }

    /*public String getMIMEType(String url) {
        String mType = null;
        String mExtension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (mExtension != null) {
            mType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mExtension);
        }
        return mType != null ? mType : "application/octet-stream";
    }*/
// In Methods.java (modify this existing method)

    public String getMIMEType(String url) {
        String mType = null;
        // 1. Try to get MIME type from file extension
        String mExtension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (mExtension != null) {
            mType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mExtension);
        }

        // 2. Fallback check (CRITICAL FIX)
        if (mType == null || mType.equals("application/octet-stream")) {
            // Fallback: If the file is being saved as a ringtone, it MUST be audio/*.
            // We'll check the extension and provide a common fallback if needed.
            if (mExtension != null) {
                mExtension = mExtension.toLowerCase();
                switch (mExtension) {
                    case "mp3":
                        mType = "audio/mpeg";
                        break;
                    case "wav":
                        mType = "audio/wav";
                        break;
                    case "ogg":
                        mType = "audio/ogg";
                        break;
                    case "m4a":
                        mType = "audio/mp4";
                        break;
                    default:
                        // Final fallback for any other unknown audio type
                        mType = "audio/*"; // Use a general audio wildcard or a common type like audio/mpeg

                        break;
                }
            } else {
                // If there's no extension at all, assume a common audio type for saving
                mType = "audio/mpeg";
            }
        }
        if (mType.equals("audio/*")) {
            mType = "audio/mpeg"; // Use a specific common type
        }

        // Ensure we don't return null
        return mType != null ? mType : "audio/mpeg";
    }
}