package com.example.ajit.avalonian;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by rsingh on 24-07-2017.
 */

public class Home extends Activity {

    Query query;
    int a = 0;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;
    String s, n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

       /* Thread t=new Thread()
        {
            public void run()
            {
                try
                {
                    sleep(2500);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        // User is signed in

                            Intent intent = new Intent(Home.this, WelcomePage.class);
                            //intent.putExtra("username", x);
                            startActivity(intent);

                    } else {
                        // No user is signed in
                        Intent intent = new Intent(Home.this, MainActivity.class);
                        startActivity(intent);
                    }
                    finish();

                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

        };
        t.start();
        */

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            s = user.getEmail();
            User obj = new User();
            firebaseDatabase = FirebaseDatabase.getInstance();
            ref = firebaseDatabase.getReference(obj.StringChanger(s));
            query = ref.getRoot().child("users").child(obj.StringChanger(s)).child("username");
            query.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    n = dataSnapshot.getValue().toString();
                    a = 1;
                    Intent intent = new Intent(Home.this, WelcomePage.class);
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
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Home.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            },2500);
        }
    }
}
