package com.example.ajit.avalonian;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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

public class MainActivity extends AppCompatActivity {

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
        ImageView iv=(ImageView) findViewById(R.id.add);
        rv=(RecyclerView) findViewById(R.id.recyclerview);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);

        if(getIntent().hasExtra("username")){
            String title=getIntent().getStringExtra("username");
            MainActivity.this.getSupportActionBar().setTitle("Hi, "+title+"!");
        }

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
                MainActivity.this.getSupportActionBar().setTitle("Hi, "+dataSnapshot.getValue().toString()+"!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.DialogTheme);
                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                View promptsView = li.inflate(R.layout.add_note, null);
                builder.setView(promptsView);
                final EditText t = (EditText) promptsView.findViewById(R.id.tid1);
                final EditText n = (EditText) promptsView.findViewById(R.id.noteid1);
                      Button save =(Button) promptsView.findViewById(R.id.save);
                      Button cancel =(Button) promptsView.findViewById(R.id.cancel);

                builder.setCancelable(false);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title =t.getText().toString();
                        String description=n.getText().toString();
                        if (TextUtils.isEmpty(title)) {
                            //Toast.makeText(getApplicationContext(), "Please enter the Title/Note!", Toast.LENGTH_SHORT).show();
                            t.setError("Can't be empty!");
                            t.requestFocus();
                            return;
                        }

                        if (TextUtils.isEmpty(description)) {
                            //Toast.makeText(getApplicationContext(), "Please enter the Title/Note!", Toast.LENGTH_SHORT).show();
                            n.setError("Can't be empty!");
                            n.requestFocus();
                            return;
                        }
                        String time = String.valueOf(Calendar.getInstance().getTime().getTime());
                        Notes notes=new Notes(title,description,time);
                        User obj=new User();
                        DatabaseReference r=firebaseDatabase.getReference().child("users").child(obj.StringChanger(s)).child("notes").push();
                        r.setValue(notes);
                        alertDialog.cancel();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
            }
        });
    }

    private void getNotes() {
        String x="";
        final User obj=new User();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) x=user.getEmail();
        final String finalX = x;
        FirebaseDatabase.getInstance().getReference().getRoot().child("users").child(obj.StringChanger(x)).child("notes").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            final  List<Notes> l=new ArrayList();
            for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                String des = (String) snapshot.child("des").getValue();
                String time = (String) snapshot.child("time").getValue();
                String title = (String) snapshot.child("title").getValue();
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
                final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.DialogTheme);
                builder.setMessage(Html.fromHtml("<font color='#FDFEFE'>Are you sure you want to logout?</font>"));
                builder.setPositiveButton(Html.fromHtml("<font color='#FDFEFE'>Yes</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        FirebaseAuth fAuth = FirebaseAuth.getInstance();
                        fAuth.signOut();
                        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.Pref_Name),MODE_PRIVATE);
                        sharedPreferences.edit().remove(getString(R.string.Pref_Key)).apply();
                        Toast.makeText(getApplicationContext(),"You have successfully logged out!!",Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(getApplicationContext(),Login.class);
                        startActivity(i);
                        finish();
                    }
                });
                builder.setNegativeButton(Html.fromHtml("<font color='#FDFEFE'>No</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(Color.WHITE);
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.WHITE);
                break;
            default:
                break;
        }

        return true;
    }
}



