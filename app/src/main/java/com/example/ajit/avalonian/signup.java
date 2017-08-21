package com.example.ajit.avalonian;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class signup extends AppCompatActivity {

    private static final String TAG = signup.class.getSimpleName();
    TextView log;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    Button btn_sup;
    EditText n, e, p;
    ProgressBar progressBar;
    String name, email, pwd;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    AlertDialog dialog;
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
        btn_sup = (Button) findViewById(R.id.sid);
        btn_sup.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View view) {
                name = n.getText().toString().trim();
                email = e.getText().toString().trim();
                pwd = p.getText().toString().trim();
                View v = signup.this.getCurrentFocus();
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Enter Name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email Id/Phone No.!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if(!Patterns.PHONE.matcher(email).matches()||email.length()!=10) {
                        Toast.makeText(signup.this, "Invalid Email Id/Phone No.!",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        return;
                    }
                    //else Toast.makeText(signup.this, "Invalid Email Id!",Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(getApplicationContext(), "Enter Password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                /*if (isValidPassword(pwd)) {
                    Toast.makeText(getApplicationContext(), "Password must contain minimum 6 characters with atleast 1 upper and 1 lower case letter, 1 digit and 1 special symbol [@#$%]", Toast.LENGTH_LONG).show();
                    return;
                }*/
                email=email.toLowerCase();
                progressBar.setVisibility(View.VISIBLE);
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) signup_email(email);
                else if (Patterns.PHONE.matcher(email).matches()&&email.length()==10) {
                    Log.e("1","1");
                    Validator validator=new Validator(email) {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(signup.this, "Phone No. already exists!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure() {
                            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                                    Log.e("1", "" + phoneAuthCredential);
                                    dialog.dismiss();
                                    btn_sup.setEnabled(false);
                                    signInWithPhoneAuthCredential(phoneAuthCredential);
                                }

                                @Override
                                public void onVerificationFailed(FirebaseException e) {
                                    Log.e("2", " " + e);
                                    dialog.dismiss();
                                    Toast.makeText(signup.this, "Incorrect Phone No.!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                                    // The SMS verification code has been sent to the provided phone number, we
                                    // now need to ask the user to enter the code and then construct a credential
                                    // by combining the code with a verification ID.

                                    // Save verification ID and resending token so we can use them later
                                    mVerificationId = verificationId;
                                    mResendToken = token;
                                    Log.e("3", verificationId);
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(signup.this,R.style.DialogTheme);
                            builder.setMessage(Html.fromHtml("<font color='#FDFEFE'>Enter the OTP for verification (6 digits code)</font>"));
                            //builder.setCancelable(false);
                            final LinearLayout lm = new LinearLayout(signup.this);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

                            final EditText otp = new EditText(signup.this);
                            Button submit = new Button(signup.this);
                            otp.setLayoutParams(params);
                            otp.setHint("Enter the received OTP");
                            otp.setInputType(InputType.TYPE_CLASS_NUMBER);
                            submit.setLayoutParams(params);
                            submit.setPadding(5, 0, 0, 0);
                            submit.setText("Submit");
                            submit.setTextColor(Color.WHITE);
                            submit.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String code = otp.getText().toString();
                                    if (TextUtils.isEmpty(otp.getText().toString())) {
                                        otp.setError("Can't be empty!");
                                        otp.requestFocus();
                                        return;
                                    }
                                    dialog.dismiss();
                                    progressBar.setVisibility(View.VISIBLE);
                                    verifyPhoneNumberWithCode(mVerificationId, code);
                                }
                            });
                            lm.addView(otp);
                            lm.addView(submit);
                            params.setMargins(5, 5, 10, 50);
                            builder.setView(lm);

                            dialog = builder.create();
                            progressBar.setVisibility(View.INVISIBLE);
                            dialog.show();
                            signup_phone(email);
                        }
                    };
                    validator.validate();
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    public  void signup_email(final String email) {
        auth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {
                Log.e("2222","2222");
                progressBar.setVisibility(View.INVISIBLE);
                User obj=new User();
                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    auth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<ProviderQueryResult> t) {
                            if (t.isSuccessful()) {
                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthWeakPasswordException e) {
                                    Toast.makeText(signup.this, "Password should be of 6 characters!", Toast.LENGTH_SHORT).show();
                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    Toast.makeText(signup.this, "Email Id is badly formatted!", Toast.LENGTH_SHORT).show();
                                } catch(FirebaseAuthUserCollisionException e) {
                                    Toast.makeText(signup.this, "Email Id is already exists!", Toast.LENGTH_SHORT).show();
                                } catch(Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }
                                Log.e(TAG, t.getException() + " " + task.getException());
                                return;
                            }
                            else if (!isNetworkAvailable(signup.this))
                                Toast.makeText(signup.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                        Log.e("3333","3333");
                    User user =new User(name,email);
                    ref.child("users").child(obj.StringChanger(email)).setValue(user);
                    Toast.makeText(signup.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(signup.this, Login.class));
                    finish();
                }
            }
        });
    }
    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
    public void signup_phone(String phoneNumber) {
        //Toast.makeText(signup.this, phone, Toast.LENGTH_SHORT).show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //FirebaseUser user = task.getResult().getUser();
                            dialog.dismiss();
                            User obj =new User();
                            obj.User_phone(name,pwd);
                            ref.child("Phone_users").child(email).setValue(obj);
                            Toast.makeText(getApplicationContext(), "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(signup.this,Login.class));
                            finish();
                        } else {try {
                            progressBar.setVisibility(View.INVISIBLE);
                            throw task.getException();
                        } catch(FirebaseAuthWeakPasswordException e) {
                            Toast.makeText(signup.this, "Password should be of 6 characters!", Toast.LENGTH_SHORT).show();
                        } catch(FirebaseAuthInvalidCredentialsException e) {
                            Toast.makeText(signup.this, "Incorrect OTP!", Toast.LENGTH_SHORT).show();
                        } catch(FirebaseAuthUserCollisionException e) {
                            Toast.makeText(signup.this, "Phone No. is already exists!", Toast.LENGTH_SHORT).show();
                        } catch(Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                            Log.e(TAG, task.getException() + " " + task.getException());
                            return;
                        }
                    }
                });
    }
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }
    public static boolean isValidPassword(String s) {
        Pattern PASSWORD_PATTERN
                = Pattern.compile(
                "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})"
        );
        return !PASSWORD_PATTERN.matcher(s).matches();
    }
}