package com.example.ajit.avalonian;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.prefs.Preferences;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by rsingh on 24-07-2017.
 */

public class Splash_Activity extends Activity {

    Query query;
    int a = 0;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;
    String s, n;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        sharedPreferences=getSharedPreferences(getString(R.string.Pref_Name),MODE_PRIVATE);
        Log.e("11",sharedPreferences.getString(getString(R.string.Pref_Key),"ttt"));
        String ses=sharedPreferences.getString(getString(R.string.Pref_Key),"tttt");
        if (user != null||!ses.equalsIgnoreCase("t")) {
            // user is signed in
            if (ses.equalsIgnoreCase("tttt")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Splash_Activity.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                },2500);
            }
            else if (ses.equalsIgnoreCase("t")) {
                s = user.getEmail();
                User obj = new User();
                firebaseDatabase = FirebaseDatabase.getInstance();
                ref = firebaseDatabase.getReference(obj.StringChanger(s));
                query = ref.getRoot().child("users").child(obj.StringChanger(s)).child("username");
                query.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try{
                            n = dataSnapshot.getValue().toString();
                            if (n==null) {
                                Log.e("phone_ses"," ");
                                startActivity(new Intent(Splash_Activity.this,Login.class));
                            }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                        a = 1;
                        Intent intent = new Intent(Splash_Activity.this, MainActivity.class);
                        intent.putExtra("username",n);
                        startActivity(intent);
                        finish();
                        Log.e("login hai already", "kuch bhi");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            else {
                query = FirebaseDatabase.getInstance().getReference().getRoot().child("Phone_users").child(ses).child("username");
                query.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try{
                            n = dataSnapshot.getValue().toString();
                            if (n==null) {
                                Log.e("phone_ses"," ");
                                startActivity(new Intent(Splash_Activity.this,Login.class));
                            }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                        a = 1;
                        Intent intent = new Intent(Splash_Activity.this, MainActivity.class);
                        intent.putExtra("username",n);
                        startActivity(intent);
                        finish();
                        Log.e("login hai already", "kuch bhi");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Splash_Activity.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            },2500);
        }
    }
}
