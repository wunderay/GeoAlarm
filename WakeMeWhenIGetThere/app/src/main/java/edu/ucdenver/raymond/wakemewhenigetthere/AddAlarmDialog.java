package edu.ucdenver.raymond.wakemewhenigetthere;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.TimeUnit;

public class AddAlarmDialog extends DialogFragment {

    private Button buttonClear;
    private Button buttonCancel;
    private Button buttonSave;

    private EditText nameEditText;
    private EditText streetEditText;
    private EditText cityEditText;
    private EditText stateEditText;
    private EditText zipEditText;
    private EditText distanceEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View diaLogView = inflater.inflate(R.layout.dialog_add_alarm, null);

        builder.setView(diaLogView).setMessage("");

        buttonCancel = diaLogView.findViewById(R.id.buttonCancel);
        buttonClear = diaLogView.findViewById(R.id.buttonClear);
        buttonSave = diaLogView.findViewById(R.id.buttonSave);
        nameEditText = diaLogView.findViewById(R.id.inputAlarmName);
        streetEditText = diaLogView.findViewById(R.id.inputAddress);
        cityEditText = diaLogView.findViewById(R.id.inputCity);
        stateEditText = diaLogView.findViewById(R.id.inputState);
        zipEditText = diaLogView.findViewById(R.id.inputZipcode);
        distanceEditText = diaLogView.findViewById(R.id.inputDistance);

        private final EditText editTextScore1 = diaLogView.findViewById(R.id.textViewDistance);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double t = 0.0;
                String tempString  = editTextScore1.getText().toString();
                if(tempString == "")
                {t +=0;}
                else {
                    t += Double.parseDouble(editTextScore1.getText().toString());
                }
                tempString  = editTextScore2.getText().toString();
                if(tempString == "")
                {t +=0;}
                else {
                    t += Double.parseDouble(editTextScore2.getText().toString());
                }
                tempString  = editTextScore3.getText().toString();
                if(tempString == "")
                {t +=0;}
                else {
                    t += Double.parseDouble(editTextScore3.getText().toString());
                }
                t = t/3;
                textViewAverage.setText(Double.toString(t));
                if (  90<= t & t < 100 ){textViewLetterGrade.setText("A+");}
                else if (  80<= t & t < 90 ){textViewLetterGrade.setText("A");}
                else if (  70<= t & t < 80 ){textViewLetterGrade.setText("B+");}
                else if (  60<= t & t < 70 ){textViewLetterGrade.setText("B");}
                else if (  t < 60 ){textViewLetterGrade.setText("B-");}
                else{textViewLetterGrade.setText("");}
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editTextScore1.addTextChangedListener(textWatcher);
        editTextScore2.addTextChangedListener(textWatcher);
        editTextScore3.addTextChangedListener(textWatcher);
        buttonCancel.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {
            dismiss(); }    }
        );

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEditText.setText("");
                streetEditText.setText("");
                cityEditText.setText("");
                stateEditText.setText("");
                zipEditText.setText("");
                distanceEditText.setText("");
                nameEditText.requestFocus();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, street, city,state,zip, distance;
                name = nameEditText.getText().toString();
                street = streetEditText.getText().toString();
                city = cityEditText.getText().toString();
                state = stateEditText.getText().toString();
                zip = zipEditText.getText().toString();
                distance = distanceEditText.getText().toString();
                int radius = Integer.parseInt(distance);

                Alarm alarm = new Alarm(name, street, city, state, zip, radius);
                MainActivity callingAcctivity = (MainActivity) getActivity();
                alarm.passActivity(callingAcctivity);
                callingAcctivity.sendRequestResponse(alarm);    //get lat/long
                dismiss();
            }
        });

        return builder.create();
    }
}
