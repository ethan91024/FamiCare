package com.ethan.FamiCare;

import android.app.Application;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.ethan.FamiCare.Firebasecords.UploadWorker;

import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        setupWorkManager();
    }

    private void setupWorkManager() {
        // 創建Constraints，定義任務的約束條件
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // 設定需要有網路連接
                .build();

        // 創建定時任務，每天在12點和0點時執行
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
                UploadWorker.class, 1, TimeUnit.DAYS)
                .setConstraints(constraints) // 設定約束條件
                .build();

        // 把定時任務加入WorkManager的排程
        WorkManager.getInstance(this).enqueue(periodicWorkRequest);
    }
}