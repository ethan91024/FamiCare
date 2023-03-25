package com.ethan.FamiCare;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DiaryPostsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DiaryPostsFragment() {
        // Required empty public constructor
    }

    public static DiaryPostsFragment newInstance(String param1, String param2) {
        DiaryPostsFragment fragment = new DiaryPostsFragment();
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

    private RecyclerView postrecycler;
    private DiaryAdapter diaryAdapter;
    private List<Diary> diaries;

    //資料庫
    private DiaryDoa diaryDoa;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diary_posts, container, false);

        diaries = new ArrayList<>();

        //資料庫
        diaryDoa = DiaryDB.getInstance(this.getContext()).diaryDoa();

        //拿今天日期
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DiaryFragment diaryFragment = new DiaryFragment();
        int selected_date = diaryFragment.getSelected_date(year, month, day);//今天日期

        diaries.add(diaryDoa.getDiaryById(selected_date));
        postrecycler = view.findViewById(R.id.PostRecycler);
        diaryAdapter = new DiaryAdapter(diaries);
        postrecycler.setAdapter(diaryAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        postrecycler.setLayoutManager(layoutManager);

        return view;
    }
}