package net.manish.navratri.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.onesignal.notifications.IDisplayableMutableNotification;
import com.onesignal.notifications.INotificationReceivedEvent;
import com.onesignal.notifications.INotificationServiceExtension;

import net.manish.navratri.R;
import net.manish.navratri.activity.SplashActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;


public class OnesignalNotificationHelper implements INotificationServiceExtension
{

    String CHANNEL_ID = "christmas_push";
    String title, message, bigpicture, url;

    @Override
    public void onNotificationReceived(INotificationReceivedEvent event) {
        IDisplayableMutableNotification notification = event.getNotification();
        title = notification.getTitle();
        message = notification.getBody();
        bigpicture = notification.getBigPicture();

        try {
            url = notification.getAdditionalData().getString("external_link");
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendNotification(event.getContext());
        event.preventDefault();
    }

    private void sendNotification(Context context) {
        Random random = new Random();
        int noti_id = random.nextInt(100);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent;

        if (url != null && !url.equals("false") && !url.trim().isEmpty()) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
        } else {
            intent = new Intent(context, SplashActivity.class);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(context, noti_id, intent, PendingIntent.FLAG_IMMUTABLE);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Push Notification";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setAutoCancel(true)
                .setSound(uri)
                .setLights(Color.RED, 800, 800)
                .setContentText(message)
                .setChannelId(CHANNEL_ID);

        mBuilder.setSmallIcon(getNotificationIcon(mBuilder, context));
        try {
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.app_icon));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (title.trim().isEmpty()) {
            mBuilder.setContentTitle(context.getString(R.string.app_name));
            mBuilder.setTicker(context.getString(R.string.app_name));
        } else {
            mBuilder.setContentTitle(title);
            mBuilder.setTicker(title);
        }

        if (bigpicture != null) {
            mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(getBitmapFromURL(bigpicture)).setSummaryText(message));
        } else {
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        }

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(noti_id, mBuilder.build());

    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder, Context context) {
        notificationBuilder.setColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorPrimary));
        return R.drawable.ic_notification;
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            InputStream input;
            if (src.contains("https://")) {
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
            } else {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
            }
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            return null;
        }
    }
}