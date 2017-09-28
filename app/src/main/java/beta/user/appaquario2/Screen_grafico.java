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

import org.json.JSONArray;
import org.json.JSONException;

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
        chart.setDrawBorders(true);
        chart.getDescription().setEnabled(false);

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
                    float[] m = {0f,1f,2f,3f,4f,5f,6f,7f,8f,9f,10f,11f,12f,13f,14f};
                    for(int i=0; i < array.length(); i++){
                        float x =  m[i];
                        float y =  Float.parseFloat(array.getJSONArray(i).getString(2));
                        entries.add(new Entry(x, y));
                    }

                    LineDataSet dataSet = new LineDataSet(entries, "Temperatura em C°");
                    LineData lineData = new LineData(dataSet);
                    chart.setData(lineData);
                    chart.invalidate();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                DialogError alert1 = new DialogError(atividade, erro);
                alert1.show();
            }
            pDialog.dismiss();
        }
    }
}
