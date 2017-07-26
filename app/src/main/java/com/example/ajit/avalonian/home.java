package com.example.ajit.avalonian;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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

public class home extends Activity{

    String s,x;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        Thread t=new Thread()
        {
            public void run()
            {
                try
                {
                    sleep(2500);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        // User is signed in

                            Intent intent = new Intent(home.this, WelcomePage.class);
                            //intent.putExtra("username", x);
                            startActivity(intent);

                    } else {
                        // No user is signed in
                        Intent intent = new Intent(home.this, MainActivity.class);
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
    }
}
