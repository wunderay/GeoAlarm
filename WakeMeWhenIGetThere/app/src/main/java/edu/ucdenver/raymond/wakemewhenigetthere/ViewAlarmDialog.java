package edu.ucdenver.raymond.wakemewhenigetthere;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ViewAlarmDialog extends DialogFragment {
    private TextView nameTextView;
    private TextView streetTextView;
    private TextView cityTextView;
    private TextView stateTextView;
    private TextView zipTextView;
    private TextView distanceTextView;
    private Button buttonCancel;
    private Button buttonDelete;
    private ToggleButton buttonToggle;

    private Alarm alarm;

    public ViewAlarmDialog(){}

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_view_alarm, null);

        nameTextView = dialogView.findViewById(R.id.textViewName);
        streetTextView = dialogView.findViewById(R.id.textViewStreet);
        cityTextView = dialogView.findViewById(R.id.textViewCity);
        stateTextView = dialogView.findViewById(R.id.textViewState);
        zipTextView = dialogView.findViewById(R.id.textViewZip);
        distanceTextView = dialogView.findViewById(R.id.textViewDistance);

        buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        buttonDelete = dialogView.findViewById(R.id.buttonDelete);
        buttonToggle = dialogView.findViewById(R.id.toggleAlarm);

        nameTextView.setText(alarm.getAlarmName());
        streetTextView.setText(alarm.getStreet());
        cityTextView.setText(alarm.getCity());
        stateTextView.setText(alarm.getMyState());
        zipTextView.setText(alarm.getZipcode());
        distanceTextView.setText(Double.toString(alarm.getRadius()));
        buttonToggle.setChecked(alarm.getOn());

        builder.setView(dialogView).setMessage(" ");

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity =(MainActivity) getActivity();
                mainActivity.removeAlarm(alarm);
                dismiss();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        buttonToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!alarm.getOn()){
                    //toggle alarm on
                    alarm.setOn(true);
                    MainActivity mainActivity = (MainActivity) getActivity();
                    alarm.passActivity(mainActivity);
                   // alarm.start();
                    mainActivity.startTracking(alarm);
                }
                else{
                    //toggle alarm off
                    alarm.setOn(false);
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.turnOff();
                }
            }
        });

        return builder.create();
    }

    public void sendAlarm(Alarm alarm){
        this.alarm = alarm;
    }
}
