package com.ethan.FamiCare;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.provider.Settings.System.getString;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.constraintlayout.widget.Group;
import androidx.core.app.NotificationCompat;

public class alarmReceiver extends BroadcastReceiver {


    // 建立notificationManager與notification物件
    private NotificationManager notificationManager;
    private Notification notification;
    private NotificationChannel channel;

    // 建立能辨識通知差別的ID
    String Channel_id="ID";
    String Channel_name="FemiCare";
    String description="FemiCare";


    public void onReceive(Context context, Intent intent){
        channel=new NotificationChannel("ID","FemiCare",NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);

        //實作觸發通知訊息，開啟首頁動作
        Intent notifyIntent=new Intent(context, GroupCalendarFragment.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,notifyIntent,0);

        // 執行通知
        broadcastNotify(context, pendingIntent);
    }

    private void broadcastNotify(Context context, PendingIntent pendingIntent) {
        notificationManager= (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(context);
                builder.setWhen(System.currentTimeMillis())
                        .setChannelId("ID")
                .setSmallIcon(R.drawable.ic_baseline_groups_24)
                .setContentTitle("訊息")
                .setContentText("行程")
                        .setAutoCancel(true)
                .setContentIntent(pendingIntent);

                Notification notification=builder.build();

        notificationManager.notify(0, notification);
    }


    }





