package com.ethan.FamiCare.Firebasecords;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UploadWorker extends Worker {// 背景執行的類別，用於定時將健康資料上傳到firebase，透過MyApplication.java定時呼叫

    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // 獲取使用者的ID
        String userId = getUserId();

        // 上傳Status資料表到Firebase Realtime Database
        uploadStatusToFirebase(userId);

        return Result.success();
    }

    private String getUserId() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        }
        return null;
    }

    private void uploadStatusToFirebase(String userId) {
        if (userId != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference statusRef = database.getReference("Status").child(userId);

            // 假設你有取得這些數據值，這裡用 0 代表示例，0代表沒有找到資料     1:待加油|2:及格|3:滿分
            int statusStep = 0;
            int statusHeartRate = 0;
            int statusSpeed = 0;
            int statusCalories = 0;
            int statusRespiratory = 0;
            int statusBloodOxygen = 0;
            int statusSleep = 0;

            // 使用 updateChildren 方法進行更新或新增資料
            HashMap<String, Object> statusData = new HashMap<>();
            statusData.put("status_step", statusStep);
            statusData.put("status_heartRate", statusHeartRate);
            statusData.put("status_speed", statusSpeed);
            statusData.put("status_calories", statusCalories);
            statusData.put("status_respiratory", statusRespiratory);
            statusData.put("status_bloodOxygen", statusBloodOxygen);
            statusData.put("status_sleep", statusSleep);

            statusRef.updateChildren(statusData);
        }
    }
}