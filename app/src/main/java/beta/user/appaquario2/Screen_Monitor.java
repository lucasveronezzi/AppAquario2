package beta.user.appaquario2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Screen_Monitor extends AppCompatActivity {

    private Context context;
    private TextView text_temp;
    private TextView text_tempMin;
    private TextView text_tempMax;
    private TextView text_PH;
    private TextView text_vazao;
    private Switch switch_luz;
    private Switch switch_bomba;
    private ToggleButton toggle_alimentar;
    private ToggleButton toggle_cooler;
    private TextView text_hora_cooler;
    private TextView text_data_cooler;
    private ImageView img_temp;
    private ImageView img_ph;

    private static final int ACTION_LUZ = 0;
    private static final int ACTION_BOMBA = 1;
    private static final int ACTION_ALIMENTAR = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen__monitor);
        context = this;

        text_temp = (TextView) findViewById(R.id.text_temp);
        text_tempMin = (TextView) findViewById(R.id.text_tempMin);
        text_tempMax = (TextView) findViewById(R.id.text_tempMax);
        text_PH = (TextView) findViewById(R.id.text_PH);
        text_vazao = (TextView) findViewById(R.id.text_vazao);
        text_hora_cooler = (TextView) findViewById(R.id.text_hora_cooler);
        text_data_cooler = (TextView) findViewById(R.id.text_data_cooler);
        switch_luz = (Switch) findViewById(R.id.switch_luz);
        switch_bomba = (Switch) findViewById(R.id.switch_bomba);
        toggle_alimentar = (ToggleButton) findViewById(R.id.toggle_alimentar);
        toggle_cooler = (ToggleButton) findViewById(R.id.toggle_cooler);
        img_ph = (ImageView) findViewById(R.id.imgPH);
        img_temp = (ImageView) findViewById(R.id.imgTemp);

        String param = "query=monitor_atual";

        TaskMonitor task = new TaskMonitor(true);
        task.execute(param);
    }

    public void actionLuz(View v){
        switch_luz.setEnabled(false);
        TaskAction task = new TaskAction(ACTION_LUZ);
        task.execute();
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
        private String erro;
        private Boolean firstTask;
        private TaskMonitor(Boolean firstTask){
            this.firstTask = firstTask;
        }
        protected void onPreExecute(){
            super.onPreExecute();
            if(firstTask) {
                pDialog = new ProgressDialog(context);
                pDialog.setMessage("Buscando informações...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }else{
                Toast.makeText(context, "Atualizando informações...",Toast.LENGTH_SHORT);
            }
        }
        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray dados = null;
            try {
                dados = APIHTTP.getArray(params[0]);
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
                    JSONArray dados = array.getJSONArray(0);
                    JSONArray config = array.getJSONArray(1);

                    text_temp.setText(dados.getString(2).substring(0,2) + "°C");
                    text_tempMin.setText(dados.getString(3).substring(0,2) + "°");
                    text_tempMax.setText(dados.getString(4).substring(0,2) + "°");
                    text_PH.setText(dados.getString(5));
                    text_vazao.setText(dados.getString(6) + " (L/m)");

                    text_hora_cooler.setText(FormataData.formatToString(dados.getString(8),"HH:mm"));
                    text_data_cooler.setText(FormataData.formatToString(dados.getString(8),"dd/MM/yyyy"));
                    if(MainActivity.stringToBool(dados.getString(7))){
                        toggle_cooler.setChecked(true);
                        toggle_cooler.setTextColor(Color.parseColor("#fff17a0a"));
                    }else{
                        toggle_cooler.setChecked(true);
                        toggle_cooler.setTextColor(Color.parseColor("#60000000"));
                    }

                    if(!firstTask){
                        if(switch_luz.isChecked() && !MainActivity.stringToBool(dados.getString(9)) )
                            Toast.makeText(context, "Iluminação Desligada!",Toast.LENGTH_LONG);
                        if(!switch_luz.isChecked() && MainActivity.stringToBool(dados.getString(9)) )
                            Toast.makeText(context, "Iluminação Ligada!",Toast.LENGTH_LONG);

                        if(switch_bomba.isChecked() && !MainActivity.stringToBool(dados.getString(10)) )
                            Toast.makeText(context, "Bomba de Água Desligada!",Toast.LENGTH_LONG);
                        if(!switch_bomba.isChecked() && MainActivity.stringToBool(dados.getString(10)) )
                            Toast.makeText(context, "Bomba de Água Ligada!",Toast.LENGTH_LONG);

                        if(!toggle_alimentar.isChecked() && !MainActivity.stringToBool(dados.getString(9)) )
                            Toast.makeText(context, "Os peixes foram alimentados!",Toast.LENGTH_LONG);
                    }

                    switch_luz.setChecked(MainActivity.stringToBool(dados.getString(9)));
                    switch_luz.setEnabled(!MainActivity.stringToBool(config.getString(6)));

                    switch_bomba.setChecked(MainActivity.stringToBool(dados.getString(10)));
                    switch_bomba.setEnabled(!MainActivity.stringToBool(config.getString(8)));

                    toggle_alimentar.setChecked(!MainActivity.stringToBool(config.getString(4)));
                    toggle_alimentar.setEnabled(!MainActivity.stringToBool(config.getString(4)));

                    int int_temp = Integer.parseInt(dados.getString(2).substring(0,2));
                    int int_tempMin = Integer.parseInt(config.getString(0).substring(0,2));
                    int int_tempMax = Integer.parseInt(config.getString(1).substring(0,2));
                    if(int_temp > int_tempMin && int_temp < int_tempMax){
                        img_temp.setVisibility(View.INVISIBLE);
                    }else if(int_temp > int_tempMax){
                        img_temp.setVisibility(View.VISIBLE);
                        img_temp.setImageResource(R.mipmap.ic_arrow_red);
                    }else{
                        img_temp.setVisibility(View.VISIBLE);
                        img_temp.setImageResource(R.mipmap.ic_arrow_blue);
                    }

                    float float_ph = Float.parseFloat(dados.getString(5));
                    float float_ph_config = Float.parseFloat(config.getString(2));
                    if((float_ph_config + 0.90) > float_ph && (float_ph_config - 0.90) < float_ph){
                        img_ph.setVisibility(View.INVISIBLE);
                    }else if((float_ph_config + 0.90) < float_ph ){
                        img_ph.setVisibility(View.VISIBLE);
                        img_ph.setImageResource(R.mipmap.ic_arrow_red);
                    }else{
                        img_ph.setVisibility(View.VISIBLE);
                        img_ph.setImageResource(R.mipmap.ic_arrow_blue);
                    }

                    float float_vazao = Float.parseFloat(dados.getString(6));
                    float float_vazao_config = Float.parseFloat(config.getString(3));
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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle("Erro");
                builder1.setMessage(erro);
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "Fechar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
            if(firstTask) pDialog.dismiss();
            else Toast.makeText(context, "Informações atualizadas.",Toast.LENGTH_SHORT).show();
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
                case ACTION_LUZ:
                    if(switch_luz.isChecked())
                        textShow = "Ligando Iluminação...";
                    else
                        textShow = "Desligando Iluminação...";
                    param = "query=update_action&luz=1";
                    break;
                case ACTION_BOMBA:
                    if(switch_bomba.isChecked())
                        textShow = "Ligando Bomba de Água...";
                    else
                        textShow = "Desligando Bomba de Água...";
                    param = "query=update_action&bomba=1";
                    break;
                case ACTION_ALIMENTAR:
                        textShow = "Alimentando os peixes. Aguarde;";
                    param = "query=update_action&motor=1";
                    break;
            }
            Toast.makeText(context, textShow ,Toast.LENGTH_LONG).show();
        }
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject dados = null;
            try {
                dados = APIHTTP.getObject(param);
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
                    case ACTION_LUZ:
                        switch_luz.setEnabled(true);
                        switch_luz.setChecked(!switch_luz.isChecked());
                        break;
                    case ACTION_BOMBA:
                        switch_bomba.setEnabled(true);
                        switch_bomba.setChecked(!switch_bomba.isChecked());
                        break;
                    case ACTION_ALIMENTAR:
                        toggle_alimentar.setEnabled(true);
                        toggle_alimentar.setChecked(!toggle_alimentar.isChecked());
                        break;
                }
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle("Erro");
                builder1.setMessage(erro);
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "Fechar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        }
    }
}
