package com.ethan.FamiCare;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class alarmReceiver extends BroadcastReceiver {

    //private static final String Channel_ID="cal_channel";
    private int notificationId = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Get id from intent

        String event = intent.getStringExtra("event");


        //Call GroupCalendar when notification is tapped
        Intent main = new Intent(context, GroupCalendar.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contextTntent = PendingIntent.getActivity(context, 0, main, 0);

        //NotificationManager
        //NotificationManager notificationManager=(NotificationManager) context.getSystemService((Context.NOTIFICATION_SERVICE));

        //準備通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "calandroid")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("行程")
                .setContentText(event)
                .setContentIntent(contextTntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        //Notify
        notificationManagerCompat.notify(notificationId, builder.build());

    }
}