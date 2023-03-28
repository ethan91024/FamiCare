package com.ethan.FamiCare;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ethan.FamiCare.Post.PostAdapter;
import com.ethan.FamiCare.Post.Posts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DiaryPostsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DiaryPostsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    //FireBase
    private DatabaseReference databaseReference;
    private PostAdapter postAdapter;
    ArrayList<Posts> posts;
    private RecyclerView recyclerView;


    private List<Diary> diaries;
    //資料庫
    private DiaryDoa diaryDoa;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diary_posts, container, false);

//        diaries = new ArrayList<>();
//
//        //資料庫
//        diaryDoa = DiaryDB.getInstance(this.getContext()).diaryDoa();
//
//        //拿今天日期
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        DiaryFragment diaryFragment = new DiaryFragment();
//        int selected_date = diaryFragment.getSelected_date(year, month, day);//今天日期
//
//        diaries.add(diaryDoa.getDiaryById(selected_date));
//        postrecycler = view.findViewById(R.id.PostRecycler);
//        postAdapter = new PostAdapter(diaries);
//        postrecycler.setAdapter(postAdapter);
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        postrecycler.setLayoutManager(layoutManager);


        // Initialize database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Posts");

        // Initialize the RecyclerView and Adapter
        recyclerView = view.findViewById(R.id.PostRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), posts);
        recyclerView.setAdapter(postAdapter);

        // Attach database listener
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Posts post = snapshot.getValue(Posts.class);
                    posts.add(post);

                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}