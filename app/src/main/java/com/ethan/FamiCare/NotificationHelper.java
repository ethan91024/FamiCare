package com.ethan.FamiCare;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {

    private static final String channelId="channeId";
    private static final String channelName="channelName";
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuildFinish;


    public NotificationHelper(Context context){
        super(context);
        creatNotificationChannel();
    }

    private void creatNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For API 26 and above
            String des = "Channel for alarm";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(des);
            getManager().createNotificationChannel(channel);
        }

    }
    // 初始化NotificationManager
    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public NotificationCompat.Builder notificationChannelBuild(String event,String time){
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,new Intent(this,GroupCalendar.class),PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
       return notificationBuildFinish=new NotificationCompat.Builder(getApplicationContext(),channelId).setSmallIcon(android.R.drawable.ic_dialog_info)
               .setContentTitle("今日行程")
               .setContentText(event+"\t"+time)
               .setPriority(NotificationCompat.PRIORITY_DEFAULT)
               .setAutoCancel(true)
               .setDefaults(NotificationCompat.DEFAULT_ALL);

    }

}
