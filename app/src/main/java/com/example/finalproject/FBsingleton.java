package com.example.finalproject;

import androidx.annotation.NonNull;
// THESE IMPORTS ARE REQUIRED TO FIX YOUR ERRORS
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FBsingleton {
    private static FBsingleton instance;
    private FirebaseDatabase database;

    private FBsingleton() {
        database = FirebaseDatabase.getInstance();
    }

    public static FBsingleton getInstance() {
        if (instance == null) {
            instance = new FBsingleton();
        }
        return instance;
    }

    // Saves the high wave score to Firebase
    public void saveHighScore(int wave) {
        String uid = FirebaseAuth.getInstance().getUid(); // Correctly finds FirebaseAuth now
        if (uid == null) return;

        DatabaseReference myRef = database.getReference("records/" + uid + "/HighScore");
        myRef.setValue(wave);
    }

    // Sets the player name
    public void setName(String name) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            DatabaseReference myRef = database.getReference("records/" + uid + "/MyName");
            myRef.setValue(name);
        }
    }
}