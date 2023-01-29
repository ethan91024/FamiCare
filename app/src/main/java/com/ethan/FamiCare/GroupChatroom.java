
package com.ethan.FamiCare;

        import static com.ethan.FamiCare.Firebasecords.FirebaseCords.Main_Chat_Database;

        import android.net.Uri;
        import android.os.Bundle;

        import androidx.annotation.NonNull;
        import androidx.fragment.app.Fragment;

        import android.os.Message;
        import android.text.TextUtils;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.ethan.FamiCare.Firebasecords.FirebaseCords;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.firestore.FieldValue;

        import java.text.SimpleDateFormat;
        import java.util.Date;
        import java.util.HashMap;

public class GroupChatroom extends Fragment {

    FirebaseAuth mAuth;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GroupChatroom() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static GroupChatroom newInstance(String param1, String param2) {
        GroupChatroom fragment = new GroupChatroom();
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

    EditText chatbox;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_chatroom, container, false);
        chatbox=view.findViewById(R.id.cahtbox);
        return view;
    }

    public void sendmessage(View view) {
        String message =chatbox.getText().toString();
        FirebaseUser user=mAuth.getCurrentUser();

        if (!TextUtils.isEmpty(message)){

            Date today=new Date();
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String messageID=format.format(today);
            String user_image_url="";
            Uri photoUrl=user.getPhotoUrl();
            String originalUrl="s96-c/photo.jpg";
            String resizeImageUrl="s400-c/photo.jpg";
            if(photoUrl!=null){
                String photoPath=photoUrl.toString();
                user_image_url = photoPath.replace(originalUrl,resizeImageUrl);
            }
            HashMap<String,Object>messageobj=new HashMap<>();
            messageobj.put("Message", message);
            messageobj.put("user name",user.getDisplayName());
            messageobj.put("timestamp", FieldValue.serverTimestamp());
            messageobj.put("messageID",messageID);
            messageobj.put("user_image_url",user_image_url);
            Main_Chat_Database.document(messageID).set(messageobj).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(GroupChatroom.this.getContext(),"Message Send",Toast.LENGTH_SHORT).show();
                        chatbox.setText("");
                    }else {
                        Toast.makeText(GroupChatroom.this.getContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}