package com.example.ajit.avalonian;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    TextView sup;
    Button btn_log;
    EditText e,p;
    FirebaseAuth auth;
    String email,pwd;
    ProgressBar bar;
    Query query;
    int a = 0;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;
    String s, n;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        auth = FirebaseAuth.getInstance();
        sharedPreferences=getSharedPreferences(getString(R.string.Pref_Name),MODE_PRIVATE);
        e=(EditText) findViewById(R.id.login_email);
        p=(EditText) findViewById(R.id.login_pwd);
        bar=(ProgressBar) findViewById(R.id.prog_Bar);
        sup=(TextView) findViewById(R.id.textView);
        sup.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(Login.this,signup.class);
                startActivity(intent);
                finish();
            }
        });
        btn_log=(Button) findViewById(R.id.lid);
        btn_log.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                email = e.getText().toString();
                pwd = p.getText().toString();
                View v = Login.this.getCurrentFocus();
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) Toast.makeText(Login.this, "Invalid Email Id!",Toast.LENGTH_SHORT).show();

                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pwd.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                bar.setVisibility(View.VISIBLE);
                auth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        //bar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (pwd.length() < 6) {
                                e.setError("Invalid Password!");
                            }
                            else {
                                Toast.makeText(Login.this, "Incorrect Email Id/Password", Toast.LENGTH_LONG).show();
                                bar.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
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
                                        sharedPreferences.edit().putString(getString(R.string.Pref_Key),"Signed").apply();
                                        Intent intent = new Intent(Login.this, MainActivity.class);
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
                                        bar.setVisibility(View.VISIBLE);
                                        }
                                },2500);
                            }
                        }
                    }
                });
            }
        });
    }
}