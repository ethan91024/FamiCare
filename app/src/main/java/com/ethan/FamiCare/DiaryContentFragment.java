package com.ethan.FamiCare;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ethan.FamiCare.Post.Posts;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

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

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

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

        // 建立 Firebase Storage 的參考，用於上傳圖片、Firebase Realtime DB 的參考
        storageReference = FirebaseStorage.getInstance().getReference("Posts");
        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");

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
            temp.setId(date);
            temp.setTitle(title_text);
            temp.setContent(content_text);
            temp.setPhotoPath(null);//先設定為空值
            if (status) {//更新或創建
                diaryDoa.updateDiary(temp);
            } else {
                DiaryDB.getInstance(getContext()).diaryDoa().insertDiary(temp);
            }
        } else {
            Toast.makeText(getContext(), "先寫下標題跟內容吧", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    //照片相關
    Bitmap bitmap;
    private static final int TAKE_PHOTO_REQUEST = 0;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    //上傳照片
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
                        fm.beginTransaction().addToBackStack(null).replace(R.id.Diary_content_layout, new DiaryFragment()).commit();
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
                        fm.beginTransaction().addToBackStack(null).replace(R.id.Diary_content_layout, new DiaryFragment()).commit();
                    }
                })
                //直接跳回DiaryFragment
                .setNeutralButton("不用", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.beginTransaction().addToBackStack(null).replace(R.id.Diary_content_layout, new DiaryFragment()).commit();
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

            // 上傳照片到 Firebase Storage 並取得下載 URL
            selectedImageUri = data.getData();
            uploadPost();

            //照片放到DiaryFragment，並存到本地端
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                ImageView imageView = (ImageView) getView().findViewById(R.id.image_view);
                imageView.setImageBitmap(bitmap);
                Save_Photo(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK && data != null) {
            // 上傳照片到 Firebase Storage 並取得下載 URL
            Bundle extras = data.getExtras();
            if (extras != null) {
                bitmap = (Bitmap) extras.get("data");
            }
            selectedImageUri = getImageUri(getContext(), bitmap);
            uploadPost();

            //照片放到DiaryFragment，並存到本地端
            ImageView imageView = (ImageView) getView().findViewById(R.id.image_view);
            imageView.setImageBitmap(bitmap);
            Save_Photo(bitmap);
        }
    }

    //拿到bitmap的URI
    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }


    //將照片存到本地端
    public void Save_Photo(Bitmap bitmap) {
        // 創建一個照片ID
        int photoId = date;//"20230101"

        // 將照片保存到本地文件系統中
        File file = new File(getContext().getFilesDir(), photoId + ".jpg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 將照片ID和路徑存儲在資料庫中
        diary = diaryDoa.getDiaryById(photoId);//拿到剛儲存的日期
        diary.setPhotoPath(file.getAbsolutePath());
        diaryDoa.updateDiary(diary);
    }


    //實現 uploadPhoto 方法，使用 Firebase Storage 將照片上傳到雲端並取得下載 URL，最後將整個Post傳到firebase
    private void uploadPost() {
        if (selectedImageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");

            fileReference.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Posts post = new Posts(date, title.getText().toString(), content.getText().toString(), uri.toString());
//                                    Posts post = new Posts(date, title.getText().toString(), content.getText().toString());
                                    String postId = databaseReference.push().getKey();
                                    databaseReference.child(postId).setValue(new Posts(date, title.getText().toString(), content.getText().toString(), uri.toString())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getContext(), "photo uploaded",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "no photo selected", Toast.LENGTH_SHORT).show();
        }

//        // 建立一個隨機的檔名，避免檔名重複
//        String randomFileName = UUID.randomUUID().toString();
//        // 建立上傳圖片的參考
//        StorageReference imageReference = storageReference.child("images/" + randomFileName);
//
//        // 上傳照片到 Firebase Storage
//        imageReference.putFile(selectedImageUri)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        // 上傳成功
//                        Toast.makeText(getContext(), "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
//                        // 照片上傳成功，取得下載 URL
//                        imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
//
//                            // 創建 Post 對象並上傳到 Firebase Realtime Database
//                            Posts post = new Posts(date, title.getText().toString(), content.getText().toString(), uri.toString());
//                            databaseReference.setValue(post);
//
//                        });
//                    }
//                });


    }

}