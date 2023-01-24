package com.ethan.FamiCare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.service.FitnessSensorServiceRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private FitnessOptions fitnessOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //建bottomNav
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        //bottomNav選擇監聽器
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch(item.getItemId())
            {
                case R.id.navigation_group:
                    startActivity(new Intent(getApplicationContext(),GroupActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.navigation_health:
                    startActivity(new Intent(getApplicationContext(),HealthActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.navigation_mood:
                    startActivity(new Intent(getApplicationContext(),MoodActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.navigation_diary:
                    startActivity(new Intent(getApplicationContext(),DiaryActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.navigation_settings:
                    startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        }
    });
}}