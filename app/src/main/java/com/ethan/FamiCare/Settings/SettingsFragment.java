package com.ethan.FamiCare.Settings;

import static androidx.databinding.DataBindingUtil.setContentView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ethan.FamiCare.MainActivity;
import com.ethan.FamiCare.R;
import com.ethan.FamiCare.databinding.FragmentSettingsBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FragmentSettingsBinding binding;
    Button login, signup, logout;
    AppCompatTextView accountBtn, friendsBtn, notificationBtn;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ImageView google_img;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;

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

        binding = FragmentSettingsBinding.inflate(getLayoutInflater());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        logout = view.findViewById(R.id.logout);
        login = view.findViewById(R.id.login);
        signup = view.findViewById(R.id.signup);
        //按鈕們
        accountBtn = view.findViewById(R.id.accountBtn);
        friendsBtn = view.findViewById(R.id.friendsBtn);
        notificationBtn = view.findViewById(R.id.notificationBtn);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        gsc = GoogleSignIn.getClient(SettingsFragment.this.getContext(), gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(SettingsFragment.this.getContext());


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Signup.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getCurrentUser().getUid() != null) {
                    Signout();
                } else {
                    Toast.makeText(SettingsFragment.this.getContext(), "You need to login first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AccountActivity.class);
                startActivity(intent);
            }
        });
        friendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendsActivity.class);
                startActivity(intent);
            }
        });
        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent);
            }
        });
//
//        if (auth.getCurrentUser().getUid() != null) {
//            Button login = view.findViewById(R.id.login), signup = view.findViewById(R.id.signup);
//            login.setVisibility(View.INVISIBLE);
//            signup.setVisibility(View.INVISIBLE);
//        }
        return view;
    }

    private void Signout() {
        auth.signOut();
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(SettingsFragment.this.getContext(), "logout success", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);

        /*
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()) {
                    Toast.makeText(SettingsFragment.this.getContext(), "Signout Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), GroupFragment.class);
                    startActivity(intent);
                }
            }
        });
        */

    }
//    private void onStart(LayoutInflater inflater, ViewGroup container,
//                         Bundle savedInstanceState) {
//        super.onStart();
//        View view = inflater.inflate(R.layout.fragment_settings, container, false);
//        if (auth.getCurrentUser().getUid() == null) {
//            Button logout = view.findViewById(R.id.logout);
//            logout.setVisibility(View.INVISIBLE);
//
//        }
//    }
}