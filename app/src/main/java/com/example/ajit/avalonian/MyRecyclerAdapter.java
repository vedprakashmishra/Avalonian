package com.example.ajit.avalonian;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        @Override public void onBindViewHolder(ViewHolder holder, int position) {
            Notes item = items.get(position);
            holder.des.setText(item.getDes());
            holder.title.setText(item.getTitle());
            Date date=new Date(Long.parseLong(item.getTime()));
            Calendar c=Calendar.getInstance();
            c.setTimeInMillis(Long.parseLong(item.getTime()));

            holder.time.setText(c.get(Calendar.DATE)+"/"+ c.get(Calendar.MONTH)+"/" +c.get(Calendar.YEAR)+"   "+c.get(Calendar.HOUR)+" : "+c.get(Calendar.MINUTE)+" "+(c.get(Calendar.AM_PM)==0?"AM":"PM"));
            holder.itemView.setTag(item);
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


