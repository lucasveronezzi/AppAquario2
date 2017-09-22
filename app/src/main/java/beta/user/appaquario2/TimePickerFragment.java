package beta.user.appaquario2;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

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
            myEditText.setText(time);
        }
    }

}
