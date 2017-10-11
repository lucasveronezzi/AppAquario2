package beta.user.appaquario2.Graficos;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by User on 11/10/2017.
 */

public class HourAxisFormart implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        String v = Integer.toString((int)value);
        if(value < 10){
            v = "0"+v;
        }
        return v+":00";
    }
}
