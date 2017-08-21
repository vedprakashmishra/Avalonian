package com.example.ajit.avalonian;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Note extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    FirebaseUser user;
    String ses, time,t,s;
    TextView note_title, note_time, note_des;
    Query query;
    int min,hour,date,month,yr;
    MenuItem item;
    long militime,justtime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        android.support.v7.widget.Toolbar toolbar= (android.support.v7.widget.Toolbar) findViewById(R.id.note_toolbar);
        Note.this.setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        note_title = (TextView) findViewById(R.id.note_title);
        note_time = (TextView) findViewById(R.id.note_time);
        note_des = (TextView) findViewById(R.id.note_des);
        Intent intent = getIntent();
        user = FirebaseAuth.getInstance().getCurrentUser();
        sharedPreferences = getSharedPreferences(getString(R.string.Pref_Name), MODE_PRIVATE);
        ses = sharedPreferences.getString(getString(R.string.Pref_Key), "tttt");
        if (intent.hasExtra("time")) {
            time = intent.getStringExtra("time");
            if (ses.equalsIgnoreCase("t")) getEmailNote();
            else getPhoneNote();
        }
    }

    private void getEmailNote() {
        final User obj = new User();
        query = FirebaseDatabase.getInstance().getReference().getRoot().child("users").child(obj.StringChanger(user.getEmail())).child("notes");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String t = (String) snapshot.child("time").getValue();
                    if (t.equalsIgnoreCase(time)) {
                        note_title.setText(snapshot.child("title").getValue().toString());
                        note_des.setText("\n" + snapshot.child("des").getValue().toString());
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(Long.parseLong(t));
                        note_time.setText(c.get(Calendar.DATE) + "/" + String.valueOf(c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR) + "   " + c.get(Calendar.HOUR) + " : " + c.get(Calendar.MINUTE) + " " + (c.get(Calendar.AM_PM) == 0 ? "AM" : "PM"));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPhoneNote() {
        String phone = sharedPreferences.getString(getString(R.string.phone), "t");
        query = FirebaseDatabase.getInstance().getReference().getRoot().child("Phone_users").child(phone).child("notes");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    t = (String) snapshot.child("time").getValue();
                    if (t.equalsIgnoreCase(time)) {
                        note_title.setText(snapshot.child("title").getValue().toString());
                        note_des.setText(snapshot.child("des").getValue().toString());
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(Long.parseLong(t));
                        note_time.setText(c.get(Calendar.DATE) + "/" + String.valueOf(c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR) + "   " + c.get(Calendar.HOUR) + " : " + c.get(Calendar.MINUTE) + " " + (c.get(Calendar.AM_PM) == 0 ? "AM" : "PM"));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note, menu);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String t = (String) snapshot.child("time").getValue();
                    if (t.equalsIgnoreCase(time)) {
                        note_title.setText(snapshot.child("title").getValue().toString());
                        note_des.setText("\n" + snapshot.child("des").getValue().toString());
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(Long.parseLong(t));
                        note_time.setText(c.get(Calendar.DATE) + "/" + String.valueOf(c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR) + "   " + c.get(Calendar.HOUR) + " : " + c.get(Calendar.MINUTE) + " " + (c.get(Calendar.AM_PM) == 0 ? "AM" : "PM"));
                        item = menu.findItem(R.id.reminder);
                        if (snapshot.hasChild("reminder")) {
                            item.setIcon(getResources().getDrawable(R.drawable.reminder_on));
                        }
                        else item.setIcon(getResources().getDrawable(R.drawable.reminder_off));
                    }
                }   
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case android.R.id.home : finish();
                break;

            case R.id.reminder:
                Calendar c=Calendar.getInstance();
                yr=c.get(Calendar.YEAR);
                month=c.get(Calendar.MONTH);
                date=c.get(Calendar.DAY_OF_MONTH);
                hour=c.get(Calendar.HOUR_OF_DAY);
                min=c.get(Calendar.MINUTE);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Note.this,R.style.AppTheme_Dark,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                yr=year;
                                date=dayOfMonth;
                                month=monthOfYear;
                                monthOfYear+=1;
                                s=dayOfMonth+"-"+monthOfYear+"-"+year;
                                TimePickerDialog timePickerDialog = new TimePickerDialog(Note.this,R.style.AppTheme_Dark,
                                        new TimePickerDialog.OnTimeSetListener() {

                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                                  int minute) {
                                                s=s+" "+hourOfDay+":"+minute;
                                                hour = hourOfDay;
                                                min=minute;
                                                Log.e("s",s);
                                                SimpleDateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm"); // I assume d-M, you may refer to M-d for month-day instead.
                                                Date date = null; // You will need try/catch around this
                                                try {
                                                    date = formatter.parse(s);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                militime = date.getTime();
                                                justtime=Calendar.getInstance().getTimeInMillis();
                                                if (militime>justtime) {
                                                    setReminder(militime);
                                                    Log.e("mili",""+militime+" "+date);
                                                    Log.e("just",""+justtime);
                                                }
                                                else {
                                                    Log.e("mili",""+militime+" "+date);
                                                    Log.e("just",""+justtime);
                                                    Toast.makeText(Note.this, "Invalid", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }, hour, min, false);

                                timePickerDialog.show();
                            }
                        }, yr, month, date);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                break;

            case R.id.edit:
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (final DataSnapshot snapshot:dataSnapshot.getChildren()) {
                        String t = (String) snapshot.child("time").getValue();
                        final String de,ti;
                        if (time.equalsIgnoreCase(t)) {
                            de = (String) snapshot.child("des").getValue();
                            ti = (String) snapshot.child("title").getValue();
                            Log.e("oye key",snapshot.getKey());
                            final AlertDialog.Builder builder = new AlertDialog.Builder(Note.this,R.style.DialogTheme);
                            LayoutInflater li = LayoutInflater.from(Note.this);
                            View promptsView = li.inflate(R.layout.add_note, null);
                            builder.setView(promptsView);
                            final EditText t1 = (EditText) promptsView.findViewById(R.id.tid1);
                            final EditText n = (EditText) promptsView.findViewById(R.id.noteid1);
                            Button save =(Button) promptsView.findViewById(R.id.save);
                            Button cancel =(Button) promptsView.findViewById(R.id.cancel);
                            t1.setText(ti);
                            n.setText(de);
                            builder.setCancelable(false);
                            final AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String title =t1.getText().toString().trim();
                                    String description=n.getText().toString().trim();
                                    if (TextUtils.isEmpty(title)) {
                                        t1.setError("Can't be empty!");
                                        t1.requestFocus();
                                        return;
                                    }
                                    if (TextUtils.isEmpty(description)) {
                                        n.setError("Can't be empty!");
                                        n.requestFocus();
                                        return;
                                    }
                                    String time1 = String.valueOf(Calendar.getInstance().getTime().getTime());
                                    //Notes notes=new Notes(title,description,time);
                                    User obj=new User();
                                    //DatabaseReference r=firebaseDatabase.getReference().child("users").child(obj.StringChanger(user.getEmail())).child("notes").child(snapshot.getKey());
                                    DatabaseReference r;
                                    SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.Pref_Name),MODE_PRIVATE);
                                    String phone=sharedPreferences.getString(getString(R.string.phone),"t");
                                    if (phone.equalsIgnoreCase("t"))  {
                                        r=FirebaseDatabase.getInstance().getReference().child("users").child(obj.StringChanger(user.getEmail())).child("notes").child(snapshot.getKey());
                                        r.child("title").setValue(title);
                                        r.child("des").setValue(description);
                                        r.child("time").setValue(time1);
                                    }
                                    else  {
                                        r=FirebaseDatabase.getInstance().getReference().child("Phone_users").child(phone).child("notes").child(snapshot.getKey());
                                        r.child("title").setValue(title);
                                        r.child("des").setValue(description);
                                        r.child("time").setValue(time1);
                                    }
                                    time=time1;
                                    Toast.makeText(Note.this,"Note Edited Successfully!",Toast.LENGTH_SHORT).show();
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
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
                break;
            case R.id.share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String des,title;
                title=note_title.getText().toString();
                des=note_des.getText().toString();
                Calendar cal=Calendar.getInstance();
                cal.setTimeInMillis(Long.parseLong(time));
                time=cal.get(Calendar.DATE)+"/"+String.valueOf(cal.get(Calendar.MONTH)+1)+"/" +cal.get(Calendar.YEAR);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,"*Title*: "+title+"\n"+"*Date*: "+time+"\n"+"*Time*: "+cal.get(Calendar.HOUR)+" : "+cal.get(Calendar.MINUTE)+" "+(cal.get(Calendar.AM_PM)==0?"AM":"PM")+"\n"+"*Note*: "+des);
                startActivity(Intent.createChooser(sharingIntent, "** Sharing Options **"));
                break;

            case R.id.delete:
                final AlertDialog.Builder builder = new AlertDialog.Builder(Note.this,R.style.DialogTheme);
                builder.setMessage(Html.fromHtml("<font color='#FDFEFE'>Are you sure you want to delete?</font>"));
                builder.setPositiveButton(Html.fromHtml("<font color='#FDFEFE'>Yes</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        delete();
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
        return super.onOptionsItemSelected(item);
    }

    private void delete() {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    String t = (String) snapshot.child("time").getValue();
                    if (time.equalsIgnoreCase(t)) {
                        startActivity(new Intent(Note.this,MainActivity.class));
                        snapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setReminder(final long militime) {;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    String t = (String) snapshot.child("time").getValue();
                    if (time.equalsIgnoreCase(t)) {
                        snapshot.getRef().child("reminder").setValue(militime);
                        item.setIcon(getResources().getDrawable(R.drawable.reminder_on));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}