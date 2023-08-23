package com.ethan.FamiCare;

import android.app.Application;
import android.widget.Toast;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.ethan.FamiCare.Firebasecords.AnalyzeHealth;
import com.ethan.FamiCare.Firebasecords.UploadWorker;
import com.ethan.FamiCare.Firebasecords.UploadWorkerK;

import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        setupWorkManager();

        triggerUploadWorker(); // 立即觸發一次上傳
        triggerAmalyze();
    }

    private void setupWorkManager() {
        // 創建Constraints，定義任務的約束條件
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // 設定需要有網路連接
                .build();

        // 創建定時任務，每天在12點和0點時執行
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
                UploadWorkerK.class, 4, TimeUnit.HOURS)
                .setConstraints(constraints) // 設定約束條件
                .build();

        // 把定時任務加入WorkManager的排程
        WorkManager.getInstance(getApplicationContext()).enqueue(periodicWorkRequest);
    }

    private void triggerUploadWorker() {
        OneTimeWorkRequest uploadRequest = new OneTimeWorkRequest.Builder(UploadWorker.class).build();
        WorkManager.getInstance(getApplicationContext()).enqueue(uploadRequest);
//        Toast.makeText(this, "健康資料上傳成功", Toast.LENGTH_SHORT).show();
    }

    private void triggerAmalyze() {
        OneTimeWorkRequest uploadRequest = new OneTimeWorkRequest.Builder(AnalyzeHealth.class).build();
        WorkManager.getInstance(getApplicationContext()).enqueue(uploadRequest);
        Toast.makeText(this, "壓力分析上傳成功", Toast.LENGTH_SHORT).show();
    }
}