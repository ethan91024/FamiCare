
package com.ethan.FamiCare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethan.FamiCare.Firebasecords.GroupMessage;
import com.ethan.FamiCare.Firebasecords.RecyclerViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class GroupChatroom extends Fragment {
    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;//1
    ArrayList<GroupMessage> list;
    TextInputLayout message;
    FloatingActionButton send;
    DatabaseReference db;
    FirebaseAuth auth;
    FirebaseUser user;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public GroupChatroom() {
        // Required empty public constructor
    }

    public static GroupChatroom newInstance(String param1, String param2) {
        GroupChatroom fragment = new GroupChatroom();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    public View onCreate(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_group_chatroom, container, false);

        return view;
    }


    private void receiveMessages() {
        db.child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    GroupMessage message = snap.getValue(GroupMessage.class);
                    list.add(message);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_chatroom, container, false);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        send = view.findViewById(R.id.fab_send);
        message = view.findViewById(R.id.message);
        recyclerView = view.findViewById(R.id.recyclerview);
        list = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        user = auth.getCurrentUser();
        String uid = user.getUid();
        String uemail = user.getEmail();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mma").format(Calendar.getInstance().getTime());

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = message.getEditText().getText().toString();
//這行可能有問題但找不出來影片30:23
                db.child("Messages").push().setValue(new GroupMessage(uemail, msg, timeStamp)).addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        message.getEditText().setText("");
                    }
                });
            }
        });

        adapter = new RecyclerViewAdapter(GroupChatroom.this.getContext(), list);
        LinearLayoutManager llm = new LinearLayoutManager(GroupChatroom.this.getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        receiveMessages();
        return view;
    }


}