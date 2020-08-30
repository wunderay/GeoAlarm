package edu.ucdenver.raymond.wakemewhenigetthere;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ListItemHolder> {

    private MainActivity mainActivity;
    private ArrayList<Alarm> alarmArrayList;

    public AlarmAdapter(MainActivity mainActivity, ArrayList<Alarm> alarmArrayList){
        this.alarmArrayList = alarmArrayList;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public ListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,
                false);
        return new ListItemHolder(listItem);

    }

    @Override
    public void onBindViewHolder(@NonNull ListItemHolder holder, int position) {
        Alarm alarm = alarmArrayList.get(position);
        holder.alarmName.setText(alarm.getAlarmName());


    }

    @Override
    public int getItemCount() {
        return alarmArrayList.size();
    }

    public class ListItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView alarmName;

        public ListItemHolder(View view){
            super(view);
            alarmName = view.findViewById(R.id.alarm_name);
            view.setClickable(true);
            view.setOnClickListener(this);

        }
         public void onClick (View view){
             mainActivity.showAlarm(getAdapterPosition());
         }

    }
}
