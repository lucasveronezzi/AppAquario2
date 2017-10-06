package beta.user.appaquario2;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private View mView;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour;
        int minute;
        TextView myEditText = (TextView) mView;
        String date = myEditText.getText().toString();
        if(date != ""){
            hour = Integer.parseInt(myEditText.getText().toString().substring(0,2));
            minute = Integer.parseInt(myEditText.getText().toString().substring(3,5));
        }else{
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void setView(View view) {
        this.mView = view;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(mView instanceof TextView) {
            String sHour, sMinute;
            if(hourOfDay < 10)
                sHour = "0" + hourOfDay;
            else
                sHour = Integer.toString(hourOfDay);
            if(minute < 10)
                sMinute = "0" + minute;
            else
                sMinute = Integer.toString(minute);
            String time = sHour + ":" + sMinute;

            TextView myEditText = (TextView) mView;
            int minutes = hourOfDay * 60 + minute;
            LinearLayout vi = (LinearLayout)mView.getParent();
            int index = vi.indexOfChild(mView);
            LinearLayout ln = (LinearLayout) vi.getParent();
            for(int x=1; x < ln.getChildCount()-1;x++) {
                TextView bt = (TextView) ((LinearLayout)ln.getChildAt(x)).getChildAt(index);
                if(!bt.equals(myEditText)){
                    int hora = Integer.parseInt(bt.getText().toString().substring(0,2));
                    int min = Integer.parseInt( bt.getText().toString().substring(3,5));
                    if(hora == 0) hora = 24;
                    int minutes1 = (hora * 60) + min - 6;
                    int minutes2 = (hora * 60) + min + 6;
                    if(minutes1 < minutes && minutes < minutes2){
                        Toast.makeText(mView.getContext(), "Os agendamentos do mesmo equipamento devem ter um intervalo de 6 minutos.",Toast.LENGTH_SHORT).show();
                        break;
                    }else{

                        myEditText.setText(time);
                    }
                }else{
                    if(ln.getId() == R.id.ln_agenda_bomba) {
                        if (index == 1)
                            bt = (TextView) ((LinearLayout) ln.getChildAt(x)).getChildAt(3);
                        else
                            bt = (TextView) ((LinearLayout) ln.getChildAt(x)).getChildAt(1);
                        if(bt.getText().length() > 0) {
                            int hora = Integer.parseInt(bt.getText().toString().substring(0, 2));
                            int min = Integer.parseInt(bt.getText().toString().substring(3, 5));
                            if (hora == 0) hora = 24;
                            int minutes1 = (hora * 60) + min - 1;
                            int minutes2 = (hora * 60) + min + 1;
                            if (minutes1 < minutes && minutes < minutes2) {
                                Toast.makeText(mView.getContext(), "O agendamento deve ter um intervalo de 1 minutos entre ligar e desligar.", Toast.LENGTH_SHORT).show();
                                break;
                            } else {
                                myEditText.setText(time);
                            }
                        }else{
                            myEditText.setText(time);
                        }
                    }else
                        myEditText.setText(time);
                }
            }

        }
    }

}
