package com.ethan.FamiCare.Group;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ethan.FamiCare.R;

public class GroupNameEditFragment extends AppCompatActivity {
    //1
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //1
    private String mParam1;
    private String mParam2;

    public GroupNameEditFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private Button editcreate;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_groupname_edit, container, false);
        GroupChatroom groupChatroom=new GroupChatroom();
        editcreate=view.findViewById(R.id.name);
        editcreate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GroupNameEditFragment.this, GroupChatroom.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
