package com.ethan.FamiCare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//test//test
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    TextView hello=findViewById(R.id.homehello);
                    hello.setText("");
                    //用switch分析被按下的item
                    switch(item.getItemId()){
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