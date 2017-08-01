package com.example.ajit.avalonian;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by vpmishra on 26-07-2017.
 */

    public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

        private List<Notes> items;
        private int itemLayout;
        FirebaseDatabase firebaseDatabase;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

        public MyRecyclerAdapter(List<Notes> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
            viewBinderHelper.setOpenOnlyOne(true);
        }

        @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_note, parent, false);
            return new ViewHolder(v);

        }

        @Override public void onBindViewHolder(final ViewHolder holder, int position) {
            final Notes item = items.get(position);
            holder.des.setText(item.getDes());
            holder.title.setText(item.getTitle());
            Calendar c=Calendar.getInstance();
            c.setTimeInMillis(Long.parseLong(item.getTime()));
            firebaseDatabase=FirebaseDatabase.getInstance();
            holder.time.setText(c.get(Calendar.DATE)+"/"+ String.valueOf(c.get(Calendar.MONTH)+1)+"/" +c.get(Calendar.YEAR)+"   "+c.get(Calendar.HOUR)+" : "+c.get(Calendar.MINUTE)+" "+(c.get(Calendar.AM_PM)==0?"AM":"PM"));
            holder.itemView.setTag(item);
            if (items != null && 0 <= position && position < items.size()) {
                final Notes data = items.get(position);
                viewBinderHelper.bind(holder.swipeLayout, position+"");

                holder.bind(data);
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext(),R.style.DialogTheme);
                        builder.setMessage(Html.fromHtml("<font color='#FDFEFE'>Are you sure you want to delete?</font>"));
                        builder.setPositiveButton(Html.fromHtml("<font color='#FDFEFE'>Yes</font>"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int arg1) {

                                final int i=holder.getAdapterPosition();
                                final Notes n=items.get(i);
                                String x="";
                                final User obj=new User();
                                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                                if(user!=null) x=user.getEmail();
                                FirebaseDatabase.getInstance().getReference().getRoot().child("users").child(obj.StringChanger(x)).child("notes").
                                        addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                                                    String time = (String) snapshot.child("time").getValue();
                                                    if (time==n.getTime()) snapshot.getRef().removeValue();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                            }
                        });
                        builder.setNegativeButton(Html.fromHtml("<font color='#FDFEFE'>No</font>"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int arg1) {
                                viewBinderHelper.closeLayout(String.valueOf(holder.getAdapterPosition()));
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                        nbutton.setTextColor(Color.WHITE);
                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setTextColor(Color.WHITE);

                    }
                });

                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final int i=holder.getAdapterPosition();
                        final Notes n=items.get(i);
                        n.getTime();
                        Log.e("pos", " "+n.getTime());
                        final String x="";
                        final User obj=new User();
                        final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

                        FirebaseDatabase.getInstance().getReference().getRoot().child("users").child(obj.StringChanger(user.getEmail())).child("notes").
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (final DataSnapshot snapshot:dataSnapshot.getChildren()) {
                                            String time = (String) snapshot.child("time").getValue();
                                            String de,ti;
                                            if (time==n.getTime()) {
                                                de = (String) snapshot.child("des").getValue();
                                                ti = (String) snapshot.child("title").getValue();
                                                Log.e("oye key",snapshot.getKey());
                                                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext(),R.style.DialogTheme);
                                                LayoutInflater li = LayoutInflater.from(holder.itemView.getContext());
                                                View promptsView = li.inflate(R.layout.add_note, null);
                                                builder.setView(promptsView);
                                                final EditText t = (EditText) promptsView.findViewById(R.id.tid1);
                                                final EditText n = (EditText) promptsView.findViewById(R.id.noteid1);
                                                Button save =(Button) promptsView.findViewById(R.id.save);
                                                Button cancel =(Button) promptsView.findViewById(R.id.cancel);
                                                t.setText(ti);
                                                n.setText(de);
                                                builder.setCancelable(false);
                                                final AlertDialog alertDialog = builder.create();
                                                alertDialog.show();
                                                save.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        String title =t.getText().toString();
                                                        String description=n.getText().toString();
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
                                                        String time1 = String.valueOf(Calendar.getInstance().getTime().getTime());
                                                        //Notes notes=new Notes(title,description,time);
                                                        User obj=new User();
                                                        DatabaseReference r=firebaseDatabase.getReference().child("users").child(obj.StringChanger(user.getEmail())).child("notes").child(snapshot.getKey());
                                                        //r.setValue(notes);
                                                        r.child("title").setValue(title);
                                                        r.child("des").setValue(description);
                                                        r.child("time").setValue(time1);
                                                        Toast.makeText(holder.itemView.getContext(),"Note Edited Successfully!",Toast.LENGTH_SHORT).show();
                                                        alertDialog.cancel();
                                                    }
                                                });

                                                cancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        viewBinderHelper.closeLayout(String.valueOf(holder.getAdapterPosition()));
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
                    }
                });

                holder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String des,title,time;
                        final int i=holder.getAdapterPosition();
                        final Notes n=items.get(i);
                        des=n.getDes();
                        title=n.getTitle();
                        time=n.getTime();
                        Calendar c=Calendar.getInstance();
                        c.setTimeInMillis(Long.parseLong(time));
                        time=c.get(Calendar.DATE)+"/"+String.valueOf(c.get(Calendar.MONTH)+1)+"/" +c.get(Calendar.YEAR);
                        Log.e("02", String.valueOf(c.get(Calendar.MONTH)+1));
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,"Title: "+title+"\n"+"Date: "+time+"\n"+"Time: "+c.get(Calendar.HOUR)+" : "+c.get(Calendar.MINUTE)+" "+(c.get(Calendar.AM_PM)==0?"AM":"PM")+"\n"+"Note: "+des);
                        holder.itemView.getContext().startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));
                        viewBinderHelper.closeLayout(String.valueOf(holder.getAdapterPosition()));
                    }
                });
            }
        }

        @Override public int getItemCount() {
            return items.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public TextView title;
            public TextView time;
            public TextView des;
            private View deleteLayout;
            public ImageView delete,edit,share;
            SwipeRevealLayout swipeLayout;

            public ViewHolder(View itemView) {
                super(itemView);

                title = (TextView) itemView.findViewById(R.id.note_title);
                time = (TextView) itemView.findViewById(R.id.note_time);
                des = (TextView) itemView.findViewById(R.id.note_description);
                deleteLayout = itemView.findViewById(R.id.delete_layout);
                swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
                delete=(ImageView) itemView.findViewById(R.id.delete);
                edit=(ImageView) itemView.findViewById(R.id.edit);
                share=(ImageView) itemView.findViewById(R.id.share);
            }

            public void bind(Notes data) {

            }
        }
    }


