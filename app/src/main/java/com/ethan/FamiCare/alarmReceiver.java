package com.ethan.FamiCare;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.ethan.FamiCare.Group.GroupCalendar;

import java.util.ArrayList;

public class alarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String event = intent.getStringExtra("event");
        String time = intent.getStringExtra("time");
        ArrayList<String> object=intent.getStringArrayListExtra("object");

        if(!event.equals("") && !time.equals("")) {
            FCMsend.pushNotification(
                    context,
                    object,
                    event,
                    time
            );
        }

        //"APA91bEg-xO9Rlyb72AGxpt3wNoyKAYsA-9-fdbWKSNxyaG8qxz2syGfiwWVXoHLwZ2EIygaygZXGF19Ge1lL9h40NDhimvwoYJXJc37P2X3gWZDn7O0cA4"
        NotificationHelper notificationHelper=new NotificationHelper(context);
        NotificationCompat.Builder nb=notificationHelper.notificationChannelBuild(event,time);
        Intent intent1=new Intent(context, GroupCalendar.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra("title",event);
        intent1.putExtra("body",time);
        PendingIntent contextIntent=PendingIntent.getActivity(context,0,intent1,PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
        nb.setContentIntent(contextIntent);

        notificationHelper.getManager().notify(1,nb.build());

    }
}