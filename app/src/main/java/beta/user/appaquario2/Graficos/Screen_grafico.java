package beta.user.appaquario2.Graficos;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import beta.user.appaquario2.APIHTTP;
import beta.user.appaquario2.DialogError;
import beta.user.appaquario2.FormataData;
import beta.user.appaquario2.R;

public class Screen_grafico extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private AppCompatActivity atividade;
    private LineChart chart;
    private IAxisValueFormatter formartDayAxis;
    private IAxisValueFormatter formartHourAxis;
    private TextView date1;
    private TextView date2;
    private boolean bool_date;
    private CheckBox check_temp;
    private CheckBox check_ph;
    private CheckBox check_vazao;
    private LineDataSet setTemp;
    private LineDataSet setPH;
    private LineDataSet setVazao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        atividade = this;
        setContentView(R.layout.activity_screen_grafico);

        check_temp = (CheckBox) findViewById(R.id.check_temp);
        check_ph = (CheckBox) findViewById(R.id.check_ph);
        check_vazao = (CheckBox) findViewById(R.id.check_vazao);

        chart = (LineChart) findViewById(R.id.chart);
        chart.setDrawBorders(true);
        chart.setAutoScaleMinMaxEnabled(true);
        chart.getDescription().setEnabled(false);
        chart.setDragEnabled(true);
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        formartDayAxis = new DayAxisFormart(chart);
        formartHourAxis = new HourAxisFormart();

        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, 2017, 10, 04);
        date1 = (TextView) findViewById(R.id.date1);
        date1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        bool_date = true;
                        if(date1.getText() != "")
                            datePickerDialog.
                                    updateDate(Integer.parseInt(date1.getText().toString().substring(6,10)),
                                            Integer.parseInt(date1.getText().toString().substring(3,5))-1,
                                            Integer.parseInt(date1.getText().toString().substring(0,2)));
                        else
                            datePickerDialog.updateDate(calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.show();
                    }
                });

        date2 = (TextView) findViewById(R.id.date2);
        date2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                bool_date = false;
                if(date2.getText() != "")
                    datePickerDialog.
                            updateDate(Integer.parseInt(date2.getText().toString().substring(6,10)),
                                    Integer.parseInt(date2.getText().toString().substring(3,5)),
                                    Integer.parseInt(date2.getText().toString().substring(0,2)));
                else
                    datePickerDialog.updateDate(calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
    }

    public void onClickConsultar(View v){
        String data1 = date1.getText().toString();
        String data2 = date2.getText().toString();
        if(data1 != ""){
            TaskGraficos task = new TaskGraficos();
            data1 = FormataData.formatToMysql(data1,"yyyy-MM-dd");
            XAxis xAxis = chart.getXAxis();
            if(data2 == "") {
                xAxis.setValueFormatter(formartHourAxis);
            }else{
                xAxis.setValueFormatter(formartDayAxis);
                data2 = FormataData.formatToMysql(data2,"yyyy-MM-dd");
            }
            chart.getAxisRight().setEnabled(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1); // minimum axis-step (interval) is 1

            if(check_ph.isChecked() || check_temp.isChecked() || check_vazao.isChecked())
                task.execute("data1="+data1+"&data2="+data2+"");
            else
                Toast.makeText(atividade, "Marque ao menos um sensor.",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(atividade, "Preencha a data de ínicio..",Toast.LENGTH_LONG).show();
        }

    }

    public void onCheckTemp(View v){
        func_dataset_check(setTemp, v);
    }
    public void onCheckPH(View v){
        func_dataset_check(setPH, v);
    }
    public void onCheckVazao(View v){
        func_dataset_check(setVazao, v);
    }
    private void func_dataset_check(LineDataSet lined, View v){
        LineData ld;
        List<ILineDataSet> lds;
        if(!check_temp.isChecked() && !check_ph.isChecked() && !check_vazao.isChecked()){
            ((CheckBox) v).setChecked(true);
        }else {
            if ((ld = chart.getLineData()) != null) {
                if ((lds = ld.getDataSets()).size() > 0) {
                    if (((CheckBox) v).isChecked()) {
                        lds.add(lined);
                    } else {
                        lds.remove(lined);
                    }
                    ld.notifyDataChanged();
                    chart.invalidate();
                }
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month++;
        String sDayofMonth = ""+dayOfMonth;
        String sMonth = ""+month;
        if(dayOfMonth < 10)
            sDayofMonth = "0" + dayOfMonth;
        if(month < 10)
            sMonth = "0" + month;
        if (bool_date)
            date1.setText(sDayofMonth+"/"+sMonth+"/"+year);
        else
            date2.setText(sDayofMonth+"/"+sMonth+"/"+year);
    }

    private class TaskGraficos extends AsyncTask<String, Void, JSONArray> {
        private String erro;
        private ProgressDialog pDialog;

        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(atividade);
            pDialog.setMessage("Buscando informações...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray dados = null;
            try {
                dados = APIHTTP.getArray("registro/?"+params[0],"GET","");
            } catch (Exception e) {
                if(e.getMessage().startsWith("Unable to resolve host"))
                    erro = "Falha ao tentar se conectar com o servidor web.\nVerifique se seu celular possui sinal com a internet.";
                else
                    erro = e.getMessage();
            }
            return dados;
        }
        @Override
        protected void onPostExecute(JSONArray array){
            if(array != null){
                try {
                    List<Entry> entries_temp = new ArrayList<Entry>();
                    List<Entry> entries_ph = new ArrayList<Entry>();
                    List<Entry> entries_vazao = new ArrayList<Entry>();
                    float x;
                    float y;
                    if(array.length() != 0) {
                        for (int i = 0; i < array.length(); i++) {
                            if(date2.getText() == ""){
                                x = Float.parseFloat(array.getJSONArray(i).getString(1));
                            }else{
                                Calendar calen = FormataData.formatToCalendar(array.getJSONArray(i).getString(1));
                                x = calen.get(Calendar.DAY_OF_YEAR) + 366;
                            }


                            y = Float.parseFloat(array.getJSONArray(i).getString(2));
                            entries_temp.add(new Entry(x, y));

                            y = Float.parseFloat(array.getJSONArray(i).getString(3));
                            entries_ph.add(new Entry(x, y));

                            y = Float.parseFloat(array.getJSONArray(i).getString(4));
                            entries_vazao.add(new Entry(x, y));
                        }

                        setTemp = new LineDataSet(entries_temp, "Temperatura em C°");
                        setTemp.setColor(Color.RED);
                        setPH = new LineDataSet(entries_ph, "Nível do PH");
                        setPH.setColor(Color.GREEN);
                        setVazao = new LineDataSet(entries_vazao, "Vazão da Água (L/m)");

                        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                        if(check_temp.isChecked())
                            dataSets.add(setTemp);
                        if(check_ph.isChecked())
                            dataSets.add(setPH);
                        if(check_vazao.isChecked())
                            dataSets.add(setVazao);
                        LineData lineData = new LineData(dataSets);
                        chart.setData(lineData);
                        chart.invalidate();
                    }else{
                        chart.clear();
                        //chart.invalidate();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                DialogError alert1 = new DialogError(atividade,"Erro", erro);
                alert1.show();
            }
            pDialog.dismiss();
        }
    }
}
