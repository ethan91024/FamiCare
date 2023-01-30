package com.ethan.FamiCare.Firebasecords;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseCords {

    public static final FirebaseFirestore firebase = FirebaseFirestore.getInstance();
    public static final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static final CollectionReference Main_Chat_Database = firebase.collection("Chat");
}
