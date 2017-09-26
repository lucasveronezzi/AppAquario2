package beta.user.appaquario2;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Screen_grafico extends AppCompatActivity {
    private AppCompatActivity atividade;
    private LineChart chart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        atividade = this;
        setContentView(R.layout.activity_screen_grafico);
        chart = (LineChart) findViewById(R.id.chart);

        TaskGraficos task = new TaskGraficos();
        task.execute();
    }
    private class TaskGraficos extends AsyncTask<String, Void, JSONObject> {
        private String param = "";
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
        protected JSONObject doInBackground(String... params) {
            JSONObject dados = null;
            try {
                dados = APIHTTP.getObject("action","PUT",this.param);
            } catch (Exception e) {
                Log.i("API", e.getMessage());
                this.erro = e.getMessage();
            }
            return dados;
        }
        @Override
        protected void onPostExecute(JSONObject array){
            if(array != null){
                List<Entry> entries = new ArrayList<Entry>();

                Entry c1e1 = new Entry();

                LineDataSet dataSet = new LineDataSet(entries, "Label");
                LineData lineData = new LineData(dataSet);
                chart.setData(lineData);
                chart.invalidate();

            }else{
                DialogError alert1 = new DialogError(atividade, erro);
                alert1.show();
            }
        }
    }
}
