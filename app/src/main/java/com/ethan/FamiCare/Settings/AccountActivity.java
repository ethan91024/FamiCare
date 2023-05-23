package com.ethan.FamiCare.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.ethan.FamiCare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountActivity extends AppCompatActivity {

    TextView username, userid, email;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        username = findViewById(R.id.username);
        userid = findViewById(R.id.userid);
        email = findViewById(R.id.emailTextView);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        String uid = auth.getCurrentUser().getUid();
        if (auth.getCurrentUser() == null) {
            username.setText("username");
            userid.setText("#userid");
        } else {
            database.getReference().child("Users").child(uid).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String username1 = snapshot.getValue(String.class);
                        // 在這裡將取得的 username 設置給 TextView
                        username.setText(username1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }


            });

            database.getReference().child("Users").child(uid).child("id").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String userid1 = snapshot.getValue(String.class);
                        // 在這裡將取得的 username 設置給 TextView
                        userid.setText("#" + userid1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
            database.getReference().child("Users").child(uid).child("userEmail").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String useremail = snapshot.getValue(String.class);
                        // 在這裡將取得的 username 設置給 TextView
                        email.setText(useremail);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}