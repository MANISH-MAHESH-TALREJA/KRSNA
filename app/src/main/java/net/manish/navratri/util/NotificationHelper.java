package net.manish.navratri.util;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import net.manish.navratri.R;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class NotificationHelper
{
    private Context mContext;
    private int NOTIFICATION_ID = 1;
    private Notification.Builder mNotification;
    private NotificationManager mNotificationManager;
    private PendingIntent mContentIntent;
    private CharSequence mContentTitle;

    public NotificationHelper(Context context)
    {
        mContext = context;
    }

    public void createNotification()
    {
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent();
        mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        int icon = android.R.drawable.stat_sys_download;
        CharSequence tickerText = mContext.getString(R.string.download);
        long when = System.currentTimeMillis();
        mNotification = new Notification.Builder(mContext)
                .setSmallIcon(icon)
                .setTicker(tickerText)
                .setContentIntent(mContentIntent)
                .setOngoing(true);

        mContentTitle = "Ringtone";
        CharSequence contentText = "0% complete";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            mNotification.build();
        } else
        {
            mNotification.getNotification();
        }
    }

    public void progressUpdate(int percentageComplete)
    {
        CharSequence contentText = percentageComplete + "% complete";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            mNotification.build();
        } else
        {
            mNotification.getNotification();
        }
    }

    public void completed()
    {

        mNotificationManager.cancel(NOTIFICATION_ID);


    }
}