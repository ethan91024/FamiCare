package com.ethan.FamiCare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.checkerframework.checker.units.qual.A;

public class GroupCalendarFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public GroupCalendarFragment() {
        // Required empty public constructor
    }

    public static GroupCalendarFragment newInstance(String param1, String param2) {
        GroupCalendarFragment fragment = new GroupCalendarFragment();
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

    private CalendarView calender;
    private TextView date;
    private Button add;

    //資料庫
    private GroupCalDoa groupCalDoa;
    private GroupCal groupCal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_calendar, container, false);

        calender = view.findViewById(R.id.cal1);
        date = view.findViewById(R.id.group_date);
        add = view.findViewById(R.id.add);
        groupCalDoa = GroupCalDB.getInstance(this.getContext()).groupCalDoa();

        calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                date.setText((month + 1) + "/" + dayOfMonth);
                selected_date = getSelected_date(year, month, dayOfMonth);
                if (groupCalDoa.getGroupCalById(selected_date) != null) {
                    add.setText(groupCalDoa.getGroupCalById(selected_date).getEvent() + "\n" + groupCalDoa.getGroupCalById(selected_date).getHour() + ":" + groupCalDoa.getGroupCalById(selected_date).getMinute());
                } else {
                    add.setText("沒有事件");
                }

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!date.getText().equals("日期")) {
                   AddGroupScheduleFragment addGroupScheduleFragment=new AddGroupScheduleFragment();
                    Bundle bundle = new Bundle();
                    FragmentManager fm = getActivity().getSupportFragmentManager();

                    if (add.getText().equals("沒有事件")) {
                        bundle.putBoolean("edit", false);
                        bundle.putInt("id", selected_date);
                        addGroupScheduleFragment.setArguments(bundle);//把日期送到要跳轉的Fragment
                        fm.beginTransaction().addToBackStack(null).replace(R.id.groupcal, addGroupScheduleFragment).commit();


                    } else {
                        bundle.putBoolean("edit", true);
                        bundle.putInt("id", selected_date);

                        addGroupScheduleFragment.setArguments(bundle);//把日期送到要跳轉的Fragment
                        fm.beginTransaction().addToBackStack(null).replace(R.id.groupcal, addGroupScheduleFragment).commit();

                    }
                } else if (date.getText().equals("日期")) {
                    Toast.makeText(getContext(), "請選擇日期", Toast.LENGTH_SHORT).show();
                }
            }




        });

        return view;

    }

    public int getSelected_date(int year, int month, int dayOfMonth) {
        String s = String.format("%4d%02d%02d", year, month + 1, dayOfMonth);
        return Integer.parseInt(s);
    }
    private void dialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(GroupCalendarFragment.super.getContext());
        final EditText editText=new EditText(GroupCalendarFragment.super.getContext());
        final EditText editText2=new EditText(GroupCalendarFragment.super.getContext());
        builder.setView(editText);
        builder.setView(editText2);
        builder.setTitle("請輸入行程、時間");builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(), editText.getText().toString(), Toast.LENGTH_SHORT).show();
                //將get到的文字轉成字串才可以給Toast顯示哦

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(), "取消", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
    }

}