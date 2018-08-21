package com.home.dfundak.mewpurr;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.home.dfundak.mewpurr.Class.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class TimestampAdapter extends RecyclerView.Adapter<TimestampAdapter.ViewHolder> {

    private ArrayList<Timestamp> mTimestamps;

    public TimestampAdapter(ArrayList<Timestamp> timestamps) {
        mTimestamps = timestamps;
    }

    @Override
    public TimestampAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View timestampView = inflater.inflate(R.layout.timestamp_list_item, parent, false);
        TimestampAdapter.ViewHolder timestampViewHolder = new TimestampAdapter.ViewHolder(timestampView);
        return timestampViewHolder;
    }

    @Override
    public void onBindViewHolder(TimestampAdapter.ViewHolder holder, int position) {
        Timestamp timestamp = this.mTimestamps.get(position);
        /*Log.d("timestamp", timestamp.getDate());
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = f.parse(timestamp.getDate());
           Log.d("date", date.toString());
        } catch (ParseException ee) {
            Log.d("error","can't parse");
            holder.time.setText(formatter.format(date));
        }*/
        holder.time.setText(timestamp.getDate());
    }

    @Override
    public int getItemCount() {
        return this.mTimestamps.size();
    }

    public void updateData(ArrayList<Timestamp> viewModels) {
        mTimestamps.clear();
        mTimestamps.addAll(viewModels);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            this.time = itemView.findViewById(R.id.time);
        }
    }
}
