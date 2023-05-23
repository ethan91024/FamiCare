package com.ethan.FamiCare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.ethan.FamiCare.Firebasecords.Users;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {


    public Login() {
        // Required empty public constructor
    }
    TextInputLayout email, password;
    Button login, cancel,signup;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ImageView google_img;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        //google_img = findViewById(R.id.google);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        BeginSignInRequest signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .build();

        gsc = GoogleSignIn.getClient(Login.this, gso);

        /*
        google_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                google_singin();
            }
        });

         */


        //1
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.loginb);
        //cancel = findViewById(R.id.cancelb);
        auth = FirebaseAuth.getInstance();

        String username="";
        String userid="";

        signup = findViewById(R.id.signup);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e = email.getEditText().getText().toString();
                String p = password.getEditText().getText().toString();

                if(!e.isEmpty()&&!p.isEmpty()) {
                    auth.signInWithEmailAndPassword(e, p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(Login.this, "Login successd!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(Login.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(Login.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);

            }
        });

        /*cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        });

         */
    }





    private void google_singin() {

        Intent intent=gsc.getSignInIntent();
        startActivityForResult(intent,65);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SignInClient oneTapClient = null;
        switch (requestCode) {
            case REQ_ONE_TAP:
                try {

                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    if (idToken !=  null) {
                        // Got an ID token from Google. Use it to authenticate
                        // with Firebase.
                        Log.d("TAG", "Got ID token.");
                    }
            } catch (ApiException e) {
                Log.w("TAG","Sign in fail",e);
                Toast.makeText(Login.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
        SignInCredential googleCredential = null;
        try {
            googleCredential = oneTapClient.getSignInCredentialFromIntent(data);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        String idToken = googleCredential.getGoogleIdToken();
        if (idToken !=  null) {
            // Got an ID token from Google. Use it to authenticate
            // with Firebase.
            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
            auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "signInWithCredential:success");
                                FirebaseUser user = auth.getCurrentUser();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "signInWithCredential:failure", task.getException());
                            }
                        }
                    });
        }
    }

//    private void firebaseAuthWithGoogle(String idToken) {
//        AuthCredential credential= GoogleAuthProvider.getCredential(idToken,null);
//        auth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Log.d("TAG", "firebaseAuthWithGoogle:success");
//                            FirebaseUser user = auth.getCurrentUser();
//
//                            Users users=new Users();
//                            users.setUserId(user.getUid());
//                            users.setUsername(user.getDisplayName());
//                            users.setProfilepic(user.getPhotoUrl().toString());
//                            database.getReference().child("Users").child(user.getUid()).setValue(users);
//
//                            Intent intent=new Intent(Login.this, MainActivity.class);
//                            startActivity(intent);
//
//                            Toast.makeText(Login.this, "Sign in with Google", Toast.LENGTH_SHORT).show();
//                        }else{
//                            Log.w("TAG","Sign in fail",task.getException());
//                        }
//                    }
//                });
//    }

}
