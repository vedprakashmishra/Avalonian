package com.example.ajit.avalonian;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

import static com.example.ajit.avalonian.R.id.progressBar;

public class MainActivity extends AppCompatActivity {

    TextView sup;
    Button btn_log;
    EditText e,p;
    FirebaseAuth auth;
    String email,pwd;
    ProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        auth = FirebaseAuth.getInstance();

        e=(EditText) findViewById(R.id.login_email);
        p=(EditText) findViewById(R.id.login_pwd);
        bar=(ProgressBar) findViewById(R.id.prog_Bar);
        sup=(TextView) findViewById(R.id.textView);
        sup.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(MainActivity.this,signup.class);
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
                View v = MainActivity.this.getCurrentFocus();
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pwd.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                bar.setVisibility(View.VISIBLE);
                auth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        bar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (pwd.length() < 6) {
                                e.setError("Invalid Password!");
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Authorization Failed", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Intent intent = new Intent(MainActivity.this, WelcomePage.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });
    }
}
