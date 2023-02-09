package com.ethan.FamiCare;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class GroupNameEditFragment extends Fragment {
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
    public static GroupNameEditFragment newInstance(String param1, String param2) {
        GroupNameEditFragment fragment = new GroupNameEditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private Button editcreate;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_groupname_edit, container, false);
        GroupChatroom groupChatroom=new GroupChatroom();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        editcreate=view.findViewById(R.id.name);
        editcreate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                fm.beginTransaction().addToBackStack(null).replace(R.id.edit, groupChatroom).commit();
            }
        });
        return view;
    }
}
