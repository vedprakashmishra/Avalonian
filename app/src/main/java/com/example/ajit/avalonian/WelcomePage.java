package com.example.ajit.avalonian;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WelcomePage extends AppCompatActivity {

    ImageView iv;
    RecyclerView rv;
    String s;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;
    Query query,q;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);
        iv=(ImageView) findViewById(R.id.add);
        rv=(RecyclerView) findViewById(R.id.recyclerview);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);

        getNotes();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) s=user.getEmail();
        User obj=new User();
        firebaseDatabase=FirebaseDatabase.getInstance();
        ref=firebaseDatabase.getReference(obj.StringChanger(s));
        query=ref.getRoot().child("users").child(obj.StringChanger(s)).child("username");
        q=ref.getRoot().child("users").child(obj.StringChanger(s));
        Log.e("fr waise he notes ka",ref.getRoot().child("users").child(obj.StringChanger(s)).toString());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                WelcomePage.this.getSupportActionBar().setTitle("Hi, "+dataSnapshot.getValue().toString()+"!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(WelcomePage.this,R.style.DialogTheme);
                LayoutInflater li = LayoutInflater.from(WelcomePage.this);
                View promptsView = li.inflate(R.layout.add_note, null);
                builder.setView(promptsView);
                final EditText t = (EditText) promptsView.findViewById(R.id.tid1);
                final EditText n = (EditText) promptsView.findViewById(R.id.noteid1);

                builder.setCancelable(false).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title =t.getText().toString();
                        String description=n.getText().toString();
                        if (TextUtils.isEmpty(title)) {
                            Toast.makeText(getApplicationContext(), "Please enter the Title/Note!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(description)) {
                            Toast.makeText(getApplicationContext(), "Please enter the Title/Note!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String time = String.valueOf(Calendar.getInstance().getTime().getTime());
                        Notes notes=new Notes(title,description,time);
                        User obj=new User();
                        DatabaseReference r=firebaseDatabase.getReference().child("users").child(obj.StringChanger(s)).child("notes").push();
                        r.setValue(notes);
                    }
                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                Toast.makeText(WelcomePage.this,"Cancelled!!",Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(Color.WHITE);
                Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.WHITE);

            }
        });
    }

    private void getNotes() {
        String x="";
        User obj=new User();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) x=user.getEmail();
        FirebaseDatabase.getInstance().getReference().getRoot().child("users").child(obj.StringChanger(x)).child("notes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              final  List<Notes> l=new ArrayList();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    String des = (String) snapshot.child("des").getValue();
                    String time = (String) snapshot.child("time").getValue();
                    String title = (String) snapshot.child("title").getValue();
                    Log.e("abey",des+" "+time+" "+ title);
                    Notes notes=new Notes(title,des,time);
                    l.add(notes);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rv.setAdapter(new MyRecyclerAdapter(l,R.layout.one_note));
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.welcome_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.logout:
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                fAuth.signOut();
                Toast.makeText(this,"You have successfully logged out!!",Toast.LENGTH_SHORT).show();
                Intent i=new Intent(this,MainActivity.class);
                startActivity(i);
                finish();
                break;
            default:
                break;
        }

        return true;
    }
}



