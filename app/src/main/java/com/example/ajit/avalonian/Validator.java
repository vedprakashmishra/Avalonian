package com.example.ajit.avalonian;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by vpmishra on 11-08-2017.
 */

public abstract class Validator {
    private String key;
    public Validator(String emailOrPhone) {
        this.key=emailOrPhone;

    }
    public void validate(){
        FirebaseDatabase.getInstance().getReference().getRoot().child("Phone_users").
                addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e("2","2"+dataSnapshot.getChildrenCount());
                        int a=1;
                        if (dataSnapshot.getChildrenCount()==0) onFailure();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.getKey().equalsIgnoreCase(key)) {
                                onSuccess();
                                return;
                            }
                            else if(!(snapshot.getKey().equalsIgnoreCase(key))&& a == dataSnapshot.getChildrenCount()) onFailure();
                            ++a;
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }

                });
    }
    public abstract void onSuccess();
    public abstract void onFailure();
}
