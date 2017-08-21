package com.example.ajit.avalonian;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

    TextView nav_name;
    RecyclerView rv;
    String s;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;
    Query query,q;
    private DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    String phone;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = (RecyclerView) findViewById(R.id.recyclerview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        initNavigationDrawer();
        firebaseDatabase = FirebaseDatabase.getInstance();
        SharedPreferences sharedPreferences =getSharedPreferences(getString(R.string.Pref_Name),MODE_PRIVATE);
        phone=sharedPreferences.getString(getString(R.string.phone),"t");
        if (!phone.equalsIgnoreCase("t")) {
            query = FirebaseDatabase.getInstance().getReference().getRoot().child("Phone_users").child(phone).child("username");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() == null)
                            startActivity(new Intent(MainActivity.this, Login.class));
                        MainActivity.this.getSupportActionBar().setTitle("Hi, " + dataSnapshot.getValue().toString() + "!");
                        nav_name.setText("Hi, " + dataSnapshot.getValue().toString() + "!");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        MainActivity.this.setSupportActionBar(toolbar);
        getNotes();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null&&phone.equalsIgnoreCase("t")) {
            s = user.getEmail();
            User obj = new User();
            ref = firebaseDatabase.getReference(obj.StringChanger(s));
            query = ref.getRoot().child("users").child(obj.StringChanger(s)).child("username");
            //q = ref.getRoot().child("users").child(obj.StringChanger(s));
            Log.e("fr waise he notes ka", ref.getRoot().child("users").child(obj.StringChanger(s)).toString());

        nav_name.setText("Hi");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() == null)
                        startActivity(new Intent(MainActivity.this, Login.class));
                    MainActivity.this.getSupportActionBar().setTitle("Hi, " + dataSnapshot.getValue().toString() + "!");
                    nav_name.setText("Hi, " + dataSnapshot.getValue().toString() + "!");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}


    public void initNavigationDrawer() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id){
                   /* case R.id.camera:
                        Toast.makeText(getApplicationContext(),"camera",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.slide_show:
                        Toast.makeText(getApplicationContext(),"slides",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.share:
                        Toast.makeText(getApplicationContext(),"share",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;*/
                    case R.id.logout:
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.DialogTheme);
                        builder.setMessage(Html.fromHtml("<font color='#FDFEFE'>Are you sure you want to logout?</font>"));
                        builder.setPositiveButton(Html.fromHtml("<font color='#FDFEFE'>Yes</font>"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int arg1) {
                                if (s!=null) FirebaseAuth.getInstance().signOut();
                                else FirebaseAuth.getInstance().signOut();

                                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.Pref_Name),MODE_PRIVATE);
                                sharedPreferences.edit().remove(getString(R.string.Pref_Key)).apply();
                                sharedPreferences.edit().remove(getString(R.string.phone)).apply();
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

                }
                return true;
            }
        });

        View header = navigationView.getHeaderView(0);
        nav_name= (TextView) header.findViewById(R.id.User_name);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }


    private void getNotes() {
        String x="";
        Query qry ;
        final User obj=new User();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null&&phone.equalsIgnoreCase("t")) {
            x=user.getEmail();
            qry=FirebaseDatabase.getInstance().getReference().getRoot().child("users").child(obj.StringChanger(x)).child("notes");
        }
        else qry=FirebaseDatabase.getInstance().getReference().getRoot().child("Phone_users").child(phone).child("notes");
        qry.addValueEventListener(new ValueEventListener() {
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
            case R.id.add_note:

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
                        String title =t.getText().toString().trim();
                        String description=n.getText().toString().trim();

                        if (TextUtils.isEmpty(description)&&TextUtils.isEmpty(title)) {
                            Toast.makeText(getApplicationContext(), "Please enter the Title & Note!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(title)) {
                            t.setError("Can't be empty!");
                            t.requestFocus();
                            return;
                        }

                        if (TextUtils.isEmpty(description)) {
                            n.setError("Can't be empty!");
                            n.requestFocus();
                            return;
                        }
                        String time = String.valueOf(Calendar.getInstance().getTime().getTime());
                        Notes notes=new Notes(title,description,time);
                        User obj=new User();
                        Log.e("user"," "+phone);
                        DatabaseReference r;
                        if (phone.equalsIgnoreCase("t"))  {
                            r=firebaseDatabase.getReference().child("users").child(obj.StringChanger(s)).child("notes").push();
                        }
                        else  {
                            r=firebaseDatabase.getReference().child("Phone_users").child(phone).child("notes").push();
                        }
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

                break;
            default:
                break;
        }
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}



