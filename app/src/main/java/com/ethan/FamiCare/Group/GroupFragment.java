package com.ethan.FamiCare.Group;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ethan.FamiCare.ChatGPT.ChatGPTActivity;
import com.ethan.FamiCare.Firebasecords.Users;
import com.ethan.FamiCare.Firebasecords.UsersAdapter;
import com.ethan.FamiCare.R;
import com.ethan.FamiCare.databinding.FragmentGroupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public GroupFragment() {
        // Required empty public constructor
    }

    FragmentGroupBinding binding;
    ArrayList<Users> list = new ArrayList<>();
    FirebaseDatabase database;
    FirebaseAuth auth;

    public static GroupFragment newInstance(String param1, String param2) {
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private Button create;
    private Button chatgpt;

    //1
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentGroupBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        String uid=auth.getCurrentUser().getUid();
        UsersAdapter adapter = new UsersAdapter(list, getContext());


        binding.chatrecy.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.chatrecy.setLayoutManager(layoutManager);

        database.getReference().child("Friend").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    users.setUserId(dataSnapshot.getKey());
                    if (!users.getUserId().equals(FirebaseAuth.getInstance().getUid())) {
                        list.add(users);
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

        GroupNameEditFragment groupNameEditFragment = new GroupNameEditFragment();
        FragmentManager fm = getActivity().getSupportFragmentManager();

        binding.createGroup.findViewById(R.id.createGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), GroupChatActivity.class);
                startActivity(intent1);
            }
        });

        binding.ChatGPT.findViewById(R.id.ChatGPT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChatGPTActivity.class);
                startActivity(intent);
            }
        });


        return binding.getRoot();
    }

}