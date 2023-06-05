package com.ethan.FamiCare;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ethan.FamiCare.Diary.Diary;
import com.ethan.FamiCare.Diary.DiaryDB;
import com.ethan.FamiCare.Diary.DiaryDoa;
import com.ethan.FamiCare.Post.Posts;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DiaryContentFragment extends Fragment {

    private Diary temp;
    public static final String NOTE_EXTRA_key = "note_id";
    public boolean status;

    //Layout 元素
    private Button save_diary;
    private EditText title;
    private MultiAutoCompleteTextView content;

    //Bundle
    private int date;
    private String t;

    //資料庫
    private DiaryDoa diaryDoa;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diary_content, container, false);
        //更新標題
        getActivity().setTitle("新增日記");

        title = view.findViewById(R.id.Title);
        content = view.findViewById(R.id.Content);

        save_diary = view.findViewById(R.id.save_diary);
        diaryDoa = DiaryDB.getInstance(this.getContext()).diaryDoa();

        //接收Bundle
        Bundle arguments = getArguments();
        if (arguments != null) {
            status = arguments.getBoolean("edited", false);

            if (status) {
                date = arguments.getInt("id");
                t = arguments.getString("title");
                temp = diaryDoa.getDiaryByIdAndTitle(date, t);
                title.setText(temp.getTitle());
                content.setText(temp.getContent());
            } else {
                date = arguments.getInt("id");
                temp = new Diary();

            }
        }


        //按下按鈕後，將標題跟內容處存到資料庫，並跳轉回DiaryFragment
        save_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onSaveNote()) {
                    Call_AlertDialog();

                }
            }
        });

        return view;
    }

    //是否有寫內容
    private boolean onSaveNote() {
        String title_text = title.getText().toString();
        String content_text = content.getText().toString();

        if (!title_text.isEmpty() && !content_text.isEmpty()) {
            temp = new Diary(date, title_text, content_text, null);
            return true;

        }
        Toast.makeText(getContext(), "先寫下標題跟內容吧", Toast.LENGTH_SHORT).show();
        return false;

    }


    //照片相關
    Bitmap bitmap;
    private static final int TAKE_PHOTO_REQUEST = 0;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;


    //上傳照片或相簿挑選或直接離開編輯
    private void Call_AlertDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("紀錄今天的你")
                .setMessage("拍張照或選擇圖片?")
                //拍照
                .setPositiveButton("現在拍！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, TAKE_PHOTO_REQUEST);

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.beginTransaction().addToBackStack(null).replace(R.id.Diary_Content_layout, new DiaryFragment()).commit();
                    }
                })
                //選擇照片
                .setNegativeButton("選擇照片", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.beginTransaction().addToBackStack(null).replace(R.id.Diary_Content_layout, new DiaryFragment()).commit();
                    }
                })
                //直接跳回DiaryFragment
                .setNeutralButton("不用", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //若編輯過，更新，否則創建
                        if (status) {//更新
                            diaryDoa.updateDiary(temp);

                        } else {//創建
                            diaryDoa.insertDiary(temp);

                        }
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.beginTransaction().addToBackStack(null).replace(R.id.Diary_Content_layout, new DiaryFragment()).commit();
                    }
                })
                .setCancelable(false)
                .show();
    }


    //跳轉到相機或是相簿
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            //照片放到DiaryFragment，並存到本地端
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                Save_Photo(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();

            if (extras != null) {
                bitmap = (Bitmap) extras.get("data");
            }
            Save_Photo(bitmap);

        }
    }


    //將照片存到本地端
    public void Save_Photo(Bitmap bitmap) {
        // 創建一個照片ID
        String photoId = (date + "") + (title.getText().toString().replace("/", "_"));//"20230101" + "日記標題"
        Toast.makeText(getContext(), photoId, Toast.LENGTH_SHORT).show();

        // 將照片保存到本地文件系統中
        File file = new File(getContext().getFilesDir(), photoId + ".jpg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Upload image in local failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        temp.setPhotoPath(file.getAbsolutePath());

        //若編輯過，更新，否則創建
        if (status) {//更新
            diaryDoa.updateDiary(temp);

        } else {//創建
            diaryDoa.insertDiary(temp);

        }

    }

}