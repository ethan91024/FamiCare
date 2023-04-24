package com.ethan.FamiCare;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ethan.FamiCare.Diary.Diary;
import com.ethan.FamiCare.Diary.DiaryDoa;
import com.ethan.FamiCare.Post.PostAdapter;
import com.ethan.FamiCare.Post.Posts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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

        // Initialize database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Posts");

        // Initialize the RecyclerView and Adapter
        recyclerView = view.findViewById(R.id.PostRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
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

                // 如果有資料就滾動到最上方
                if (posts.size() > 0) {
                    int latestPosition = postAdapter.getItemCount() - 1;
                    recyclerView.scrollToPosition(latestPosition);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}