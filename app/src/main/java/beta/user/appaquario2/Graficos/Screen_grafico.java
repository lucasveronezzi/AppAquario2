package beta.user.appaquario2.Graficos;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import beta.user.appaquario2.APIHTTP;
import beta.user.appaquario2.DialogError;
import beta.user.appaquario2.FormataData;
import beta.user.appaquario2.R;

public class Screen_grafico extends AppCompatActivity {
    private AppCompatActivity atividade;
    private LineChart chart;
    private IAxisValueFormatter formartDayAxis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        atividade = this;
        setContentView(R.layout.activity_screen_grafico);
        chart = (LineChart) findViewById(R.id.chart);
        chart.setDrawBorders(true);
        chart.getDescription().setEnabled(false);
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        formartDayAxis = new DayAxisFormart(chart);

        TaskGraficos task = new TaskGraficos();
        task.execute("data1=0000-00-00&data2=9999-99-99");
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
                Log.i("API", e.getMessage());
                this.erro = e.getMessage();
            }
            return dados;
        }
        @Override
        protected void onPostExecute(JSONArray array){
            if(array != null){
                try {
                    List<Entry> entries = new ArrayList<Entry>();
                    for(int i=0; i < array.length(); i++){
                        float x =  Float.parseFloat(FormataData.formatToString(array.getJSONArray(i).getString(1), "dd.MM"));
                        float y =  Float.parseFloat(array.getJSONArray(i).getString(2));
                        entries.add(new Entry(x, y));
                    }

                    LineDataSet dataSet = new LineDataSet(entries, "Temperatura em C°");
                    LineData lineData = new LineData(dataSet);
                    XAxis xAxis = chart.getXAxis();
                    xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
                    xAxis.setValueFormatter(formartDayAxis);

                    chart.setData(lineData);
                    chart.invalidate();
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
