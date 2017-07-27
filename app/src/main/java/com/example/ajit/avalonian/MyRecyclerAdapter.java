package com.example.ajit.avalonian;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by vpmishra on 26-07-2017.
 */

    public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

        private List<Notes> items;
        private int itemLayout;

        public MyRecyclerAdapter(List<Notes> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
        }

        @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_note, parent, false);
            return new ViewHolder(v);

        }

        @Override public void onBindViewHolder(final ViewHolder holder, int position) {
            Notes item = items.get(position);
            holder.des.setText(item.getDes());
            holder.title.setText(item.getTitle());
            Calendar c=Calendar.getInstance();
            c.setTimeInMillis(Long.parseLong(item.getTime()));

            holder.time.setText(c.get(Calendar.DATE)+"/"+ c.get(Calendar.MONTH)+"/" +c.get(Calendar.YEAR)+"   "+c.get(Calendar.HOUR)+" : "+c.get(Calendar.MINUTE)+" "+(c.get(Calendar.AM_PM)==0?"AM":"PM"));
            holder.itemView.setTag(item);
            /*holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("hua click?","ha hua");
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext(),R.style.DialogTheme);

                    builder.setCancelable(false).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                            .setNegativeButton("Update", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    nbutton.setTextColor(Color.WHITE);
                    Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(Color.WHITE);

                }
            });*/
        }

        @Override public int getItemCount() {
            return items.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public TextView title;
            public TextView time;
            public TextView des;

            public ViewHolder(View itemView) {
                super(itemView);

                title = (TextView) itemView.findViewById(R.id.note_title);
                time = (TextView) itemView.findViewById(R.id.note_time);
                des = (TextView) itemView.findViewById(R.id.note_description);
            }
        }
    }


