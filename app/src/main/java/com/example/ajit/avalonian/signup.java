package com.example.ajit.avalonian;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity {

    TextView log;
    FirebaseAuth auth;
    Button btn_sup;
    EditText n, e, p;
    ProgressBar progressBar;
    String name, email, pwd;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        n = (EditText) findViewById(R.id.nid);
        e = (EditText) findViewById(R.id.eid);
        p = (EditText) findViewById(R.id.spid);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        firebaseDatabase=FirebaseDatabase.getInstance();
        ref=firebaseDatabase.getReference();
        log = (TextView) findViewById(R.id.text);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(signup.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();
        btn_sup = (Button) findViewById(R.id.sid);
        btn_sup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = n.getText().toString();
                email = e.getText().toString();
                pwd = p.getText().toString();
                View v = signup.this.getCurrentFocus();
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Enter name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) Toast.makeText(signup.this, "Invalid Email Id!",Toast.LENGTH_SHORT).show();

                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pwd.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                auth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        User obj=new User();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                                auth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                   @Override
                                   public void onComplete(@NonNull Task<ProviderQueryResult> t) {
                                       if (t.isSuccessful()) Toast.makeText(signup.this, "Email Id already exists!",Toast.LENGTH_SHORT).show();
                                   }
                               });
                        } else {

                            User user =new User(name,email);
                            ref.child("users").child(obj.StringChanger(email)).setValue(user);
                            Toast.makeText(signup.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(signup.this, Login.class));
                            finish();
                        }
                    }
                });

            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}