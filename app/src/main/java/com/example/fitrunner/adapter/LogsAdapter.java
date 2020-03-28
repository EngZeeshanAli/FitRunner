package com.example.fitrunner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitrunner.R;
import com.example.fitrunner.objects.RunningLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.Item> {
    Context c;
    ArrayList<RunningLog> list;

    public LogsAdapter(Context c, ArrayList<RunningLog> list) {
        this.c = c;
        this.list = list;
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item, parent, false);
        return new Item(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        RunningLog log = list.get(position);
        holder.speed.setText("Speed : " + log.getSpeed() + " km/h");
        holder.pace.setText("Pace : " + log.getPace() + " hr/km");
        holder.time.setText("Time : " + log.getTime());
        holder.distance.setText("Distance : " + log.getDistance() + "km");
        holder.date_time.setText("Date&Time : " + dateFormate(log.getDateTime()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Item extends RecyclerView.ViewHolder {
        TextView speed, pace, time, distance, date_time;

        public Item(@NonNull View itemView) {
            super(itemView);
            speed = itemView.findViewById(R.id.speed_item);
            pace = itemView.findViewById(R.id.pace_item);
            time = itemView.findViewById(R.id.time_item);
            distance = itemView.findViewById(R.id.distance_item);
            date_time = itemView.findViewById(R.id.dt_item);
        }
    }

    private String dateFormate(String time) {
        Calendar cal1 = Calendar.getInstance();
        long yourmilliseconds = Long.parseLong(time);
        cal1.setTimeInMillis(yourmilliseconds);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss a");
        return dateFormat.format(cal1.getTime());
    }
}
