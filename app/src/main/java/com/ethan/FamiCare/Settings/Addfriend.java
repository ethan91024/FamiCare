package com.ethan.FamiCare.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ethan.FamiCare.MainActivity;
import com.ethan.FamiCare.R;
import com.ethan.FamiCare.databinding.ActivityAddfriendBinding;
import com.ethan.FamiCare.databinding.ActivityFriendsBinding;

public class Addfriend extends AppCompatActivity {

    ActivityAddfriendBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);
        binding= ActivityAddfriendBinding.inflate(getLayoutInflater());

        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Addfriend.this, FriendsActivity.class);
                startActivity(intent);
            }
        });
        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Addfriend.this, FriendsActivity.class);
                startActivity(intent);
            }
        });
    }
}