package com.example.ajit.avalonian;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

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
    String s;
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
                email = e.getText().toString().trim();
                pwd = p.getText().toString().trim();
                View v = Login.this.getCurrentFocus();
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter Email Id/Phone No.!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) Toast.makeText(Login.this, "Invalid Email Id!",Toast.LENGTH_SHORT).show();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if(!Patterns.PHONE.matcher(email).matches()||email.length()!=10) {
                        Toast.makeText(Login.this, "Invalid Email Id/Phone No.!",Toast.LENGTH_SHORT).show();
                        bar.setVisibility(View.INVISIBLE);
                        return;
                    }
                    //else Toast.makeText(signup.this, "Invalid Email Id!",Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(getApplicationContext(), "Enter Password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                /*if (isValidPassword(pwd) ) {
                    Toast.makeText(getApplicationContext(), "Incorrect Password!", Toast.LENGTH_SHORT).show();
                    return;
                }*/
                email=email.toLowerCase();
                bar.setVisibility(View.VISIBLE);
                if (Patterns.PHONE.matcher(email).matches()) {
                    if (Patterns.PHONE.matcher(email).matches() && email.length() == 10)
                    {
                        Log.e("here","here");
                        firebaseDatabase = FirebaseDatabase.getInstance();
                        ref = firebaseDatabase.getReference(email);
                        query = ref.getRoot().child("Phone_users");
                        Log.e("oye", FirebaseDatabase.getInstance().getReference().getRoot().child("Phone_users").child(email).toString());
                        FirebaseDatabase.getInstance().getReference().getRoot().child("Phone_users").
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Log.e("104",dataSnapshot.toString());
                                        long a=0;
                                        for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                                            String n = (String) snapshot.child("username").getValue();
                                            String p = (String) snapshot.child("pwd").getValue();
                                            Log.e("105",n+" "+snapshot.getKey());
                                            if(snapshot.getKey().equalsIgnoreCase(email)) {
                                                if (p.equalsIgnoreCase(pwd)) {
                                                    sharedPreferences.edit().putString(getString(R.string.Pref_Key), "Signed").apply();
                                                    sharedPreferences.edit().putString(getString(R.string.phone),email).apply();
                                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                                    intent.putExtra("username", n);
                                                    intent.putExtra("phone",email);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                                else Toast.makeText(Login.this, "Incorrect Password!", Toast.LENGTH_SHORT).show();
                                                a=2;
                                            }
                                        }
                                        if (a==0) Toast.makeText(Login.this, "Phone No. doesn't exists!", Toast.LENGTH_SHORT).show();
                                        bar.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("55",databaseError.toString());
                                    }
                                });
                    }
                }
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            //bar.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                Toast.makeText(Login.this, "Incorrect Email Id/Password!", Toast.LENGTH_LONG).show();
                                bar.setVisibility(View.INVISIBLE);
                            }
                            else {
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
                                                Object n = dataSnapshot.getValue();
                                                sharedPreferences.edit().putString(getString(R.string.Pref_Key), "Signed").apply();
                                                sharedPreferences.edit().putString(getString(R.string.phone),"t").apply();
                                                Intent intent = new Intent(Login.this, MainActivity.class);
                                                intent.putExtra("username",""+ n);
                                                startActivity(intent);
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                } else {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            bar.setVisibility(View.VISIBLE);
                                        }
                                    }, 2500);
                                }
                            }
                        }
                    });
                }

            }
        });
    }

    public static boolean isValidPassword(String s) {
        Pattern PASSWORD_PATTERN
                = Pattern.compile(
                "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})"
        );
        return !PASSWORD_PATTERN.matcher(s).matches();
    }
}
