package com.ethan.FamiCare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    TextInputLayout email, password;
    Button login, signup;
    FirebaseAuth auth;
    ImageView google_img;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    public void onCreate(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        Button healthconnect = view.findViewById(R.id.setting_healthconnect);
        healthconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HealthConnect.class);
                startActivity(intent);
            }
        });

        google_img=view.findViewById(R.id.google);

        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN )
                .requestEmail()
                .build();

        gsc= GoogleSignIn.getClient(SettingsFragment.this.getContext(),gso);

        google_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                google_singin();
            }
        });



//1
        Intent intent = new Intent();
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        login = view.findViewById(R.id.login);
        signup = view.findViewById(R.id.signup);
        auth = FirebaseAuth.getInstance();
        FragmentManager fm = getActivity().getSupportFragmentManager();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e = email.getEditText().getText().toString();
                String p = password.getEditText().getText().toString();
                auth.signInWithEmailAndPassword(e, p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Login successd!", Toast.LENGTH_SHORT).show();
                            fm.beginTransaction().setReorderingAllowed(true).addToBackStack(null).replace(R.id.setting, new AfterLogin()).commit();

                        } else {
                            Toast.makeText(getActivity(), "Login Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e = email.getEditText().getText().toString();
                String p = password.getEditText().getText().toString();
                auth.createUserWithEmailAndPassword(e, p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Account Created Successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed!" + task.getException(), Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });
        return view;
    }
    private void google_singin() {

        Intent intent=gsc.getSignInIntent();
        startActivityForResult(intent,100);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        if(requestCode==100){
            Task<GoogleSignInAccount>task=GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                task.getResult(ApiException.class);
                fm.beginTransaction().setReorderingAllowed(true).addToBackStack(null).replace(R.id.setting, new AfterLogin()).commit();

            } catch (ApiException e) {
                Toast.makeText(SettingsFragment.this.getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
/*
    @Override
    public void onStart() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        super.onStart();
        FirebaseUser user=auth.getCurrentUser();
        if(user!=null ){
            fm.beginTransaction().setReorderingAllowed(true).replace(R.id.setting,new AfterLogin()).commit();
        }
    }

 */

}

