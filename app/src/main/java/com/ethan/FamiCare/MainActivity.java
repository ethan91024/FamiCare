package com.ethan.FamiCare;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.health.connect.client.HealthConnectClient;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private HealthConnectClient healthConnectClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //啟動直接在群組
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().addToBackStack(null).replace(R.id.fragment_container, new GroupFragment()).commit();

        checkAvailability();
    }

    //判斷HealthConnect是否安裝了
    void checkAvailability() {
        if (HealthConnectClient.isProviderAvailable(MainActivity.this)) {
            Toast.makeText(MainActivity.this, "Health Connect is installed and supported!", Toast.LENGTH_LONG).show();
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            Toast.makeText(MainActivity.this, "Health Connect is not supported!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Health Connect is not installed!", Toast.LENGTH_LONG).show();
            installHealthConnect();
        }
    }

    //----!----導向下載頁面的method沒反應(只有我)
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