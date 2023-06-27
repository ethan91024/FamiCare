package com.ethan.FamiCare.Group;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ethan.FamiCare.Firebasecords.FriendModel;
import com.ethan.FamiCare.MainActivity;
import com.ethan.FamiCare.R;
import com.ethan.FamiCare.databinding.ActivityAddNewGroupBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddNewGroup extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    Button add,cancel;
    TextView edittext;
    CircleImageView editprofileimage;
    ActivityAddNewGroupBinding binding;
    String uid;//使用者的uid
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_group);
        binding= ActivityAddNewGroupBinding.inflate(getLayoutInflater());

        add=findViewById(R.id.add);
        cancel=findViewById(R.id.cancel);
        edittext=findViewById(R.id.groupname);
        editprofileimage = findViewById(R.id.profile_image);

        auth=FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        uid=auth.getCurrentUser().getUid();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edittext.getText().toString().isEmpty()){
                    Toast.makeText(AddNewGroup.this, "請輸入群組名稱 " , Toast.LENGTH_SHORT).show();
                }else {
                    String groupname=edittext.getText().toString();
                    FriendModel friend = new FriendModel( groupname);
                    database.getReference().child("Grouplist").child(uid).push().setValue(friend)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AddNewGroup.this, "群組創建成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddNewGroup.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewGroup.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}