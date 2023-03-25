package com.ethan.FamiCare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ethan.FamiCare.Firebasecords.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {
    public Signup() {
        // Required empty public constructor
    }
    TextInputLayout email, password,username;
    Button cancel, signup;
    FirebaseAuth auth;
    FirebaseDatabase database;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_signup, container, false);

        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        cancel= view.findViewById(R.id.cancel);
        signup = view.findViewById(R.id.signupb);
        auth = FirebaseAuth.getInstance();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);


            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String u = username.getEditText().getText().toString();
                String e = email.getEditText().getText().toString();
                String p = password.getEditText().getText().toString();
                auth.createUserWithEmailAndPassword(e, p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Users user=new Users(u,e,p);
                            String id=task.getResult().getUser().getUid();
                            database.getInstance().getReference().child("Users").push().child(id).setValue(user);
                            Toast.makeText(Signup.this, "Account Created Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Signup.this, Login.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Signup.this, "Failed!" + task.getException(), Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });
        return view;
    }
    }


