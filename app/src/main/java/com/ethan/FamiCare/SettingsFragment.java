package com.ethan.FamiCare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import androidx.fragment.app.FragmentTransaction;

import com.ethan.FamiCare.Firebasecords.Users;
import com.ethan.FamiCare.databinding.FragmentSettingsBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    FragmentSettingsBinding binding;
    TextInputLayout email, password,username;
    Button login, signup;
    FirebaseAuth auth;
    FirebaseDatabase database;
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
        binding=FragmentSettingsBinding.inflate(getLayoutInflater());

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        Button healthconnect = view.findViewById(R.id.setting_healthconnect);
        FragmentManager fm = getActivity().getSupportFragmentManager();
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

        gsc= GoogleSignIn.getClient(SettingsFragment.this.getActivity(),gso);

        google_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                google_singin();
                fm.beginTransaction().setReorderingAllowed(true).addToBackStack(null).replace(R.id.setting, new AfterLogin()).commit();
            }
        });



//1
        Intent intent = new Intent();
        username=view.findViewById(R.id.username);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        login = view.findViewById(R.id.login);
        signup = view.findViewById(R.id.signup);
        auth = FirebaseAuth.getInstance();


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
        startActivityForResult(intent,65);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==65){
            Task<GoogleSignInAccount>task=GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account=task.getResult(ApiException.class);
                Log.d("TAG","firebaseAuthWithGoogle:"+account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("TAG","Sign in fail",e);
                Toast.makeText(SettingsFragment.this.getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential= GoogleAuthProvider.getCredential(idToken,null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(SettingsFragment.this.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "firebaseAuthWithGoogle:success");
                            FirebaseUser user = auth.getCurrentUser();
                            /*
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.afterlogin, new SettingsFragment());
                            transaction.addToBackStack(null);
                            transaction.commit();
                             */
                            Toast.makeText(SettingsFragment.this.getContext(), "Error", Toast.LENGTH_SHORT).show();
                        }else{
                            Log.w("TAG","Sign in fail",task.getException());
                        }
                    }
                });
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

