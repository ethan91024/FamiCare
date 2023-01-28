package com.ethan.FamiCare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class DiaryContentFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DiaryContentFragment() {
        // Required empty public constructor
    }

    public static DiaryContentFragment newInstance(String param1, String param2) {
        DiaryContentFragment fragment = new DiaryContentFragment();
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

    private int date;
    private Diary temp;
    public static final String NOTE_EXTRA_key = "note_id";
    private boolean status;

    //Layout 元素
    Button save_diary;
    EditText title;
    MultiAutoCompleteTextView content;

    //資料庫
    private DiaryDoa diaryDoa;
    private Diary diary;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diary_content, container, false);

        Bundle arguments = getArguments();
        date = arguments.getInt("id");
        title = view.findViewById(R.id.Title);
        content = view.findViewById(R.id.Content);

        save_diary = view.findViewById(R.id.save_diary);
        diaryDoa = DiaryDB.getInstance(this.getContext()).diaryDoa();

        if (arguments != null) {
            status = arguments.getBoolean("edited", false);
        }
        if (status) {
            temp = diaryDoa.getDiaryById(date);
            title.setText(temp.getTitle());
            content.setText(temp.getContent());
        } else {
            temp = new Diary();
        }

        //按下按鈕後，將標題跟內容處存到資料庫，並跳轉回DiaryFragment
        save_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveNote();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().addToBackStack(null).replace(R.id.Diary_content_layout, new DiaryFragment()).commit();
            }
        });

        return view;
    }

    private void onSaveNote() {
        String title_text = title.getText().toString();
        String content_text = content.getText().toString();

        if (!title_text.isEmpty() && !content_text.isEmpty()) {
            temp.setId(date);
            temp.setTitle(title_text);
            temp.setContent(content_text);
            if (status) {//更新或創建
                diaryDoa.updateDiary(temp);
            } else {
                DiaryDB.getInstance(getContext()).diaryDoa().insertDiary(temp);
            }
        } else {
            Toast.makeText(getContext(), "先寫下標題跟內容吧", Toast.LENGTH_SHORT).show();
        }
    }
}