package com.example.psyps6_cw2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.psyps6_cw2.database.Run;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
Data adapter for giving previous runs to the recyclerview
 */

public class RunAdapter extends RecyclerView.Adapter<RunAdapter.DataViewHolder>{

    private List<Run> data;
    private LayoutInflater layoutInflater;
    private RunAdapter.ItemClickListener clickListener;


    public RunAdapter(Context context) {
        this.data = new ArrayList<>();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public RunAdapter.DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.run_itemview, parent, false);
        return new RunAdapter.DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RunAdapter.DataViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<Run> newData) {
        if (data != null) {
            data.clear();
            data.addAll(newData);
        }
        else {
            data = newData;
        }
    }

    void setClickListener(RunAdapter.ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(Run run);
    }


    class DataViewHolder extends RecyclerView.ViewHolder {

        TextView dateText;
        TextView distanceText;
        TextView durationText;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.date);
            distanceText = itemView.findViewById(R.id.distance);
            durationText = itemView.findViewById(R.id.duration);

            //click listener returns the id of the run clicked
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                clickListener.onItemClick(data.get(position));
            });
        }

        public void bind(Run run) {
            if (run != null) {
                long time = run.getDuration();
                String displayDuration = (String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(time),
                        TimeUnit.MILLISECONDS.toMinutes(time) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
                        TimeUnit.MILLISECONDS.toSeconds(time) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))));

                double distance = run.getDistance();
                String displayDistance = String.format("%.2f",distance/1000)+"km";

                String displayDate = run.getDate();

                durationText.setText(displayDuration);
                distanceText.setText(displayDistance);
                dateText.setText(displayDate);
            }
        }
    }
}
