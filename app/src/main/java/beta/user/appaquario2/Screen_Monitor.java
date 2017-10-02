package beta.user.appaquario2;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class Screen_Monitor extends AppCompatActivity {

    private AppCompatActivity atividade;
    private TextView text_temp;
    private TextView text_tempMin;
    private TextView text_tempMax;
    private TextView text_PH;
    private TextView text_vazao;
    private Switch switch_bomba;
    private ToggleButton toggle_alimentar;
    private ToggleButton toggle_cooler;
    private TextView text_hora_cooler;
    private TextView text_data_cooler;
    private TextView text_date_motor;
    private ImageView img_temp;
    private ImageView img_ph;
    private Timer timer;

    private static final int ACTION_BOMBA = 1;
    private static final int ACTION_ALIMENTAR = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_screen__monitor);
        atividade = this;

        text_temp = (TextView) findViewById(R.id.text_temp);
        text_tempMin = (TextView) findViewById(R.id.text_tempMin);
        text_tempMax = (TextView) findViewById(R.id.text_tempMax);
        text_PH = (TextView) findViewById(R.id.text_PH);
        text_vazao = (TextView) findViewById(R.id.text_vazao);
        text_hora_cooler = (TextView) findViewById(R.id.text_hora_cooler);
        text_data_cooler = (TextView) findViewById(R.id.text_data_cooler);
        text_date_motor = (TextView) findViewById(R.id.text_date_motor);
        switch_bomba = (Switch) findViewById(R.id.switch_bomba);
        toggle_alimentar = (ToggleButton) findViewById(R.id.toggle_alimentar);
        toggle_cooler = (ToggleButton) findViewById(R.id.toggle_cooler);
        img_ph = (ImageView) findViewById(R.id.imgPH);
        img_temp = (ImageView) findViewById(R.id.imgTemp);

        TaskMonitor task = new TaskMonitor(true);
        task.execute();

        final Handler handler = new Handler();
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        TaskMonitor task = new TaskMonitor(false);
                        task.execute();
                    }
                });
            }
        };
        timer.schedule(timerTask, 15000, 15000);;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        timer.cancel();
    }

    public void actionBomba(View v){
        switch_bomba.setEnabled(false);
        TaskAction task = new TaskAction(ACTION_BOMBA);
        task.execute();
    }

    public void actionAlimentar(View v){
        toggle_alimentar.setEnabled(false);
        TaskAction task = new TaskAction(ACTION_ALIMENTAR);
        task.execute();
    }

    private class TaskMonitor extends AsyncTask<String, Void, JSONArray> {
        private ProgressDialog pDialog;
        private DialogError alert1;
        private String erro;
        private Boolean firstTask;
        private TaskMonitor(Boolean firstTask){
            this.firstTask = firstTask;
        }
        protected void onPreExecute(){
            super.onPreExecute();
            if(firstTask) {
                pDialog = new ProgressDialog(atividade);
                pDialog.setMessage("Buscando informações...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }else{
                //Toast.makeText(atividade, "Atualizando informações...",Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray dados = null;
            try {
                dados = APIHTTP.getArray("monitor","GET","");
            } catch (Exception e) {
                Log.i("API", e.getMessage());
                erro = e.getMessage();
            }
            return dados;
        }
        @Override
        protected void onPostExecute(JSONArray array){
            if(array != null) {
                try {
                    JSONArray sensor = array.getJSONArray(0);
                    JSONArray rele = array.getJSONArray(1);

                    if( MainActivity.stringToBool(sensor.getJSONArray(0).getString(3)) ){
                        if(!alert1.isShowing()) {
                            alert1 = new DialogError(atividade, "Atenção",
                                    "Faz mais de 5 minutos que o monitor não é atualizado pelo arduino.\n" +
                                            "O equipamento pode estar desligado ou sem acesso a internet para atualizar o monitor.");
                            alert1.show();
                        }
                    }

                    text_temp.setText(sensor.getJSONArray(0).getString(0).substring(0,sensor.getJSONArray(0).getString(0).length()-3) + "°C");
                    text_tempMin.setText(sensor.getJSONArray(0).getString(1).substring(0,sensor.getJSONArray(0).getString(1).length()-3) + "°");
                    text_tempMax.setText(sensor.getJSONArray(0).getString(2).substring(0,sensor.getJSONArray(0).getString(2).length()-3) + "°");
                    text_PH.setText(sensor.getJSONArray(2).getString(0));
                    text_vazao.setText(sensor.getJSONArray(1).getString(0) + " (L/m)");

                    text_hora_cooler.setText(FormataData.formatToString(rele.getJSONArray(2).getString(2),"HH:mm"));
                    text_data_cooler.setText(FormataData.formatToString(rele.getJSONArray(2).getString(2),"dd/MM/yyyy"));
                    if(MainActivity.stringToBool(rele.getJSONArray(2).getString(0))){
                        toggle_cooler.setChecked(true);
                        toggle_cooler.setTextColor(Color.parseColor("#fff17a0a"));
                    }else{
                        toggle_cooler.setChecked(false);
                        toggle_cooler.setTextColor(Color.parseColor("#60000000"));
                    }

                    if(!firstTask){
                        if(switch_bomba.isChecked() && !MainActivity.stringToBool(rele.getJSONArray(1).getString(0)) )
                            Toast.makeText(atividade, "Bomba de Água Desligada!",Toast.LENGTH_LONG);
                        if(!switch_bomba.isChecked() && MainActivity.stringToBool(rele.getJSONArray(1).getString(0)) )
                            Toast.makeText(atividade, "Bomba de Água Ligada!",Toast.LENGTH_LONG);

                        if(!toggle_alimentar.isChecked() && !MainActivity.stringToBool(rele.getJSONArray(0).getString(1)) )
                            Toast.makeText(atividade, "Os peixes foram alimentados!",Toast.LENGTH_LONG);
                    }

                    switch_bomba.setChecked(MainActivity.stringToBool(rele.getJSONArray(1).getString(0)));
                    switch_bomba.setEnabled(!MainActivity.stringToBool(rele.getJSONArray(1).getString(1)));

                    toggle_alimentar.setChecked(!MainActivity.stringToBool(rele.getJSONArray(0).getString(1)));
                    toggle_alimentar.setEnabled(!MainActivity.stringToBool(rele.getJSONArray(0).getString(1)));
                    text_date_motor.setText(FormataData.formatToString(rele.getJSONArray(0).getString(2), "HH:mm (dd/MM/yyyy)"));

                    int int_temp = Integer.parseInt(sensor.getJSONArray(0).getString(0).substring(0,sensor.getJSONArray(0).getString(0).length()-3));
                    int int_tempMin = Integer.parseInt(MainActivity.C_TEMP_MIN);
                    int int_tempMax = Integer.parseInt(MainActivity.C_TEMP_MAX);
                    if(int_temp > int_tempMin && int_temp < int_tempMax){
                        img_temp.setVisibility(View.INVISIBLE);
                    }else if(int_temp > int_tempMax){
                        img_temp.setVisibility(View.VISIBLE);
                        img_temp.setImageResource(R.mipmap.ic_arrow_red);
                    }else{
                        img_temp.setVisibility(View.VISIBLE);
                        img_temp.setImageResource(R.mipmap.ic_arrow_blue);
                    }

                    float float_ph = Float.parseFloat(sensor.getJSONArray(2).getString(0));
                    float float_ph_config = Float.parseFloat(MainActivity.C_PH);
                    if((float_ph_config + 0.90) > float_ph && (float_ph_config - 0.90) < float_ph){
                        img_ph.setVisibility(View.INVISIBLE);
                    }else if((float_ph_config + 0.90) < float_ph ){
                        img_ph.setVisibility(View.VISIBLE);
                        img_ph.setImageResource(R.mipmap.ic_arrow_red);
                    }else{
                        img_ph.setVisibility(View.VISIBLE);
                        img_ph.setImageResource(R.mipmap.ic_arrow_blue);
                    }

                    float float_vazao = Float.parseFloat(sensor.getJSONArray(1).getString(0));
                    float float_vazao_config = Float.parseFloat(MainActivity.C_VAZAO);
                    if(float_vazao < float_vazao_config - 10.00){
                        text_vazao.setTextColor(Color.parseColor("#e30917"));
                    }else if(float_vazao < float_vazao_config - 5.00){
                        text_vazao.setTextColor(Color.parseColor("#ffff00"));
                    }else{
                        text_vazao.setTextColor(Color.parseColor("#ffffff"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                alert1 = new DialogError(atividade, "Erro",erro);
                if(timer != null)
                    timer.cancel();
                alert1.show();
            }
            if(firstTask) pDialog.dismiss();
        }
    }

    private class TaskAction extends AsyncTask<String, Void, JSONObject> {
        private String param = "";
        private String erro;
        private int action;
        private TaskAction(int action){
            this.action = action;
        }
        protected void onPreExecute(){
            super.onPreExecute();
            String textShow = "";
            switch (action){
                case ACTION_BOMBA:
                    if(switch_bomba.isChecked())
                        textShow = "Ligando Bomba de Água...";
                    else
                        textShow = "Desligando Bomba de Água...";
                    param = "id=2&action=1";
                    break;
                case ACTION_ALIMENTAR:
                        textShow = "Alimentando os peixes. Aguarde;";
                    param = "id=1&action=1";
                    break;
            }
            Toast.makeText(atividade, textShow ,Toast.LENGTH_LONG).show();
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
            }else{
                switch (action){
                    case ACTION_BOMBA:
                        switch_bomba.setEnabled(true);
                        switch_bomba.setChecked(!switch_bomba.isChecked());
                        break;
                    case ACTION_ALIMENTAR:
                        toggle_alimentar.setEnabled(true);
                        toggle_alimentar.setChecked(!toggle_alimentar.isChecked());
                        break;
                }
                DialogError alert1 = new DialogError(atividade, "Erro",erro);
                timer.cancel();
                alert1.show();
            }
        }
    }
}
