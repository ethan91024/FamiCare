package com.ethan.FamiCare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ethan.FamiCare.Firebasecords.Users;
import com.ethan.FamiCare.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class Signup extends AppCompatActivity {
    public Signup() {
        // Required empty public constructor
    }
    ActivitySignupBinding binding;
    TextInputLayout email, password,username;
    Button cancel, signup;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String token;//紀錄裝置


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);


        username=findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        cancel= findViewById(R.id.cancel);
        signup = findViewById(R.id.signupb);

        auth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        token = task.getResult();
                        System.out.println("Token=" + token);
                       /* FCMaddgroup.addgroup(
                                Signup.this,
                                "add",
                                token
                        );

                        */
                    }
                });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this, GroupFragment.class);
                startActivity(intent);

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String u = username.getEditText().getText().toString();
                String e = email.getEditText().getText().toString();
                String p = password.getEditText().getText().toString();
                if(!u.isEmpty()&&!e.isEmpty()&&!p.isEmpty()) {
                    auth.createUserWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Users user = new Users(u, e, p,token);
                                String id = task.getResult().getUser().getUid();
                                database.getReference().child("Users").child(id).setValue(user);
                                Toast.makeText(Signup.this, "Account Created Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Signup.this, GroupFragment.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(Signup.this, "Sing up Failed!" + task.getException(), Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }else{
                    Toast.makeText(Signup.this, "Failed!" , Toast.LENGTH_LONG).show();

                }
            }
        });
    }
}


