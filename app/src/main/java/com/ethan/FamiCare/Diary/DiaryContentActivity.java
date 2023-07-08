package com.ethan.FamiCare.Diary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ethan.FamiCare.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DiaryContentActivity extends AppCompatActivity {

    private Diary temp;
    public static final String NOTE_EXTRA_KEY = "note_id";
    public boolean status;

    // Layout 元素
    private Button saveDiary;
    private EditText title;
    private MultiAutoCompleteTextView content;

    // Bundle
    private int date;
    private String t;

    // 資料庫
    private DiaryDoa diaryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_content);

        // 更新標題
        setTitle("新增日記");

        title = findViewById(R.id.ContentTitle);
        content = findViewById(R.id.ContentContent);

        saveDiary = findViewById(R.id.save_diary);
        diaryDao = DiaryDB.getInstance(this).diaryDoa();

        // 接收Bundle
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            status = arguments.getBoolean("edited", false);

            if (status) {
                date = arguments.getInt("id");
                t = arguments.getString("title");
                temp = diaryDao.getDiaryByIdAndTitle(date, t);
                title.setText(temp.getTitle());
                content.setText(temp.getContent());
            } else {
                date = arguments.getInt("id");
                temp = new Diary();
            }
        }

        // 按下按钮后，将标题和内容存储到数据库，并跳转回DiaryFragment
        saveDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onSaveNote()) {
                    callAlertDialog();
                }
            }
        });
    }

    // 是否有写内容
    private boolean onSaveNote() {
        String titleText = title.getText().toString();
        String contentText = content.getText().toString();

        if (!titleText.isEmpty() && !contentText.isEmpty()) {
            temp = new Diary(date, titleText, contentText, null);
            return true;
        }
        Toast.makeText(this, "先寫下標題和內容吧", Toast.LENGTH_SHORT).show();
        return false;
    }

    // 照片相关
    Bitmap bitmap;
    private static final int TAKE_PHOTO_REQUEST = 0;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    // 上传照片或相册选择或直接退出编辑
    private void callAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("紀錄今天的你吧")
                .setMessage("拍張照或選擇照片?")
                // 拍照
                .setPositiveButton("現在拍！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, TAKE_PHOTO_REQUEST);
                    }
                })
                // 选择照片
                .setNegativeButton("選擇照片", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                    }
                })
                // 直接跳回DiaryFragment
                .setNeutralButton("不用", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 若编辑过，更新，否则创建
                        if (status) { // 更新
                            diaryDao.updateDiary(temp);
                        } else { // 创建
                            diaryDao.insertDiary(temp);
                        }
                        onBackPressed();
                    }
                })
                .setCancelable(false)
                .show();
    }

    // 跳转到相机或是相册
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // 照片放到DiaryFragment，并存到本地端
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                savePhoto(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();

            if (extras != null) {
                bitmap = (Bitmap) extras.get("data");
            }
            savePhoto(bitmap);
        }
    }

    // 将照片存到本地端
    public void savePhoto(Bitmap bitmap) {
        // 创建一个照片ID
        String photoId = (date + "") + (title.getText().toString().replace("/", "_")); // "20230101" + "日记标题"
//        Toast.makeText(this, photoId, Toast.LENGTH_SHORT).show();

        // 将照片保存到本地文件系统中
        File file = new File(getFilesDir(), photoId + ".jpg");
        try (FileOutputStream fos = new FileOutputStream(file);) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

        } catch (Exception e) {
            Toast.makeText(this, "圖片上傳失敗", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        temp.setPhotoPath(file.getAbsolutePath());

        // 若编辑过，更新，否则创建
        if (status) { // 更新
            diaryDao.updateDiary(temp);
        } else { // 创建
            diaryDao.insertDiary(temp);
        }

        onBackPressed();
    }
}