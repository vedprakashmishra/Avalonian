package com.example.ajit.avalonian;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

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

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
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
                                Query qry;
                                SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences(holder.itemView.getContext().getString(R.string.Pref_Name),MODE_PRIVATE);
                                String phone=sharedPreferences.getString(holder.itemView.getContext().getString(R.string.phone),"t");
                                Log.e("phone",phone);
                                if(user!=null&&phone.equalsIgnoreCase("t")) {
                                    x=user.getEmail();
                                    qry=FirebaseDatabase.getInstance().getReference().getRoot().child("users").child(obj.StringChanger(x)).child("notes");
                                }
                                else  qry=FirebaseDatabase. getInstance().getReference().getRoot().child("Phone_users").child(phone).child("notes");
                                        qry.addListenerForSingleValueEvent(new ValueEventListener() {
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
                holder.cardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(holder.itemView.getContext(),Note.class);
                        final int i=holder.getAdapterPosition();
                        final Notes n=items.get(i);
                        String time=n.getTime();
                        intent.putExtra("time",time);
                        holder.itemView.getContext().startActivity(intent);
                        //holder.itemView.overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
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
            public ImageView delete;
            SwipeRevealLayout swipeLayout;
            public View cardview;

            public ViewHolder(final View itemView) {
                super(itemView);

                title = (TextView) itemView.findViewById(R.id.note_title);
                time = (TextView) itemView.findViewById(R.id.note_time);
                des = (TextView) itemView.findViewById(R.id.note_description);
                deleteLayout = itemView.findViewById(R.id.delete_layout);
                swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
                delete=(ImageView) itemView.findViewById(R.id.delete);
                cardview= itemView.findViewById(R.id.card);

            }

            public void bind(Notes data) {

            }

        }
    }


