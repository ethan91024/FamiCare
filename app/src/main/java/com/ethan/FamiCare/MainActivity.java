package com.ethan.FamiCare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.health.connect.client.HealthConnectClient;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private HealthConnectClient healthConnectClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        checkAvailability();
    }

    //判斷HealthConnect是否安裝了
    void checkAvailability() {
        if (HealthConnectClient.isProviderAvailable(MainActivity.this)) {
            Toast.makeText(MainActivity.this, "Health Connect is installed and supported!", Toast.LENGTH_LONG).show();
            healthConnectClient = HealthConnectClient.getOrCreate(MainActivity.this);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            Toast.makeText(MainActivity.this, "Health Connect is not supported!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Health Connect is not installed!", Toast.LENGTH_LONG).show();
            installHealthConnect();
        }
    }

    //----!----導向下載頁面的method沒反應
    void installHealthConnect() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri data = Uri.parse("market://details")
                .buildUpon()
                .appendQueryParameter("id", "com.google.android.apps.healthdata")
                .appendQueryParameter("url", "healthconnect://onboarding")
                .build();
        intent.setData(data);
        startActivity(intent);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    TextView hello = findViewById(R.id.homehello);
                    hello.setText("");
                    //用switch分析被按下的item
                    switch (item.getItemId()) {
                        case R.id.navigation_health:
                            selectedFragment = new HealthFragment();
                            break;
                        case R.id.navigation_group:
                            selectedFragment = new GroupFragment();
                            break;
                        case R.id.navigation_mood:
                            selectedFragment = new MoodFragment();
                            break;
                        case R.id.navigation_diary:
                            selectedFragment = new DiaryFragment();
                            break;
                        case R.id.navigation_settings:
                            selectedFragment = new SettingsFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };
}