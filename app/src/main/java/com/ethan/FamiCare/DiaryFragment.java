package com.ethan.FamiCare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.util.Calendar;

public class DiaryFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DiaryFragment() {
        // Required empty public constructor
    }

    public static DiaryFragment newInstance(String param1, String param2) {
        DiaryFragment fragment = new DiaryFragment();
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

    private int selected_date;
    //Layout 元素
    private CalendarView calender;
    private TextView date;
    private Button title;
    private ImageView image_view;
    private TextView cal_fold;

    private Button cal;
    private Button look;

    //資料庫
    private DiaryDoa diaryDoa;
    private Diary diary;
    private Diary diary2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        calender = view.findViewById(R.id.calender);
        date = view.findViewById(R.id.date);
        title = view.findViewById(R.id.title);
        diaryDoa = DiaryDB.getInstance(this.getContext()).diaryDoa();
        image_view = view.findViewById(R.id.image_view);
        cal = view.findViewById(R.id.cal);
        cal_fold = view.findViewById(R.id.cal_fold);
        look = view.findViewById(R.id.look);

        //一跳轉頁面就可以顯示是否輸入過資料
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        selected_date = getSelected_date(year, month, day);
        diary = diaryDoa.getDiaryById(selected_date);

        date.setText((month + 1) + "/" + day);
        if (diary != null) {
            //設定日記標題
            title.setText(diary.getTitle());

            //設定日記照片
            if (diary.getPhotoPath() != null) {
                File imageFile = new File(diary.getPhotoPath());
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                image_view.setImageBitmap(bitmap);
            } else {
                image_view.setImageDrawable(null);

            }

        } else {
            title.setText("尚未命名標題");
            image_view.setImageDrawable(null);
        }

        //監聽選擇到的日期，改變date，抓取資料庫對應日期的資料，顯示資料庫標題到title
        calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                //變更日期
                date.setText((month + 1) + "/" + dayOfMonth);
                selected_date = getSelected_date(year, month, dayOfMonth);
                diary2 = diaryDoa.getDiaryById(selected_date);

                if (diary2 != null) {
                    title.setText(diary2.getTitle());

                    //設定日記照片
                    if (diary2.getPhotoPath() != null) {
                        File imageFile2 = new File(diary2.getPhotoPath());
                        Bitmap bitmap = BitmapFactory.decodeFile(imageFile2.getAbsolutePath());
                        image_view.setImageBitmap(bitmap);
                    } else {
                        image_view.setImageDrawable(null);
                    }

                } else {
                    title.setText("尚未命名標題");
                    image_view.setImageDrawable(null);
                }
            }
        });

        //點擊日記標題，紀錄選擇的日期(20230101)，跳轉到DiaryContent
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!date.getText().equals("日期")) {//已選擇日期
                    DiaryContentFragment diaryContentFragment = new DiaryContentFragment();
                    Bundle bundle = new Bundle();
                    FragmentManager fm = getActivity().getSupportFragmentManager();

                    if (title.getText().equals("尚未命名標題")) {//創建新的日記1
                        bundle.putBoolean("edited", false);//沒被編輯過
                        bundle.putInt("id", selected_date);
                        diaryContentFragment.setArguments(bundle);//把日期送到要跳轉的Fragment

                        fm.beginTransaction().addToBackStack(null).replace(R.id.Diary_layout, diaryContentFragment).commit();
                    } else {
                        bundle.putBoolean("edited", true);//被編輯過
                        bundle.putInt("id", selected_date);
                        diaryContentFragment.setArguments(bundle);//把日期送到要跳轉的Fragment

                        fm.beginTransaction().addToBackStack(null).replace(R.id.Diary_layout, diaryContentFragment).commit();
                    }

                } else if (date.getText().equals("日期")) {
                    Toast.makeText(getContext(), "請選擇日期", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), GroupCalendar.class);
                startActivity(intent);
            }
        });

        //摺疊行事曆
        cal_fold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (calender.getVisibility() == View.VISIBLE) {
                    calender.setVisibility(View.GONE);
                } else {
                    calender.setVisibility(View.VISIBLE);
                }
            }
        });

        //跳轉到DiaryPostsFragment
        look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().addToBackStack(null).replace(R.id.Diary_layout, new DiaryPostsFragment()).commit();
            }
        });

        return view;
    }

    public int getSelected_date(int year, int month, int dayOfMonth) {
        String s = String.format("%4d%02d%02d", year, month + 1, dayOfMonth);
        return Integer.parseInt(s);
    }
}