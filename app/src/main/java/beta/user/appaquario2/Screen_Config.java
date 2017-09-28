package beta.user.appaquario2;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Screen_Config extends AppCompatActivity {

    private AppCompatActivity atividade;
    private TextView temp_min;
    private TextView temp_max;
    private SeekBar bar_temp_min;
    private SeekBar bar_temp_max;
    private EditText textVazao;
    private EditText textPH;
    private boolean stateEdtiLayout = false;
    private LayoutAgendamento ln;
    private static final String KEY_STATE_EDIT = "state_edit";
    private final int SET_CONFIG = 0;
    private final int GET_CONFIG = 1;
    private List<Integer> list_del_agenda = new ArrayList<Integer>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            stateEdtiLayout= savedInstanceState.getBoolean(KEY_STATE_EDIT);
        }
        setContentView(R.layout.activity_screen__config);
        atividade = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textVazao = (EditText)findViewById(R.id.config_text_vazao);
        textPH = (EditText)findViewById(R.id.config_text_ph);

        temp_min = (TextView) findViewById(R.id.text_temp_min);
        temp_max = (TextView) findViewById(R.id.text_temp_max);
        bar_temp_min = (SeekBar) findViewById(R.id.bar_temp_min);
        bar_temp_max = (SeekBar) findViewById(R.id.bar_temp_max);
        temp_min.setText(bar_temp_min.getProgress() + "°C");
        temp_max.setText(bar_temp_max.getProgress() + "°C");

        bar_temp_min.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                if(progresValue > bar_temp_max.getProgress())
                    bar_temp_max.setProgress(progresValue);
                temp_min.setText(progresValue + "°C");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        bar_temp_max.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                if(progresValue < bar_temp_min.getProgress())
                    bar_temp_min.setProgress(progresValue);
                temp_max.setText(progresValue + "°C");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        TaskConfig task = new TaskConfig("GET");
        task.execute("");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(KEY_STATE_EDIT, stateEdtiLayout);
    }

    public void showTimePickerDialog(View v) {
        TimePickerFragment fragmentTimePick = new TimePickerFragment();
        fragmentTimePick.setView(v);
        fragmentTimePick.show(getSupportFragmentManager(), "timePicker");
    }

    public void addAgendamento(View v){
        LinearLayout lnParent = (LinearLayout) v.getParent();
        ln = new LayoutAgendamento(this, lnParent);
        lnParent.addView(ln,lnParent.getChildCount()-1);
    }

    public void removeAgendamento(View v){
        LinearLayout lnPai = (LinearLayout) ((ViewGroup) v.getParent()).getParent();
        LinearLayout lnChild = (LinearLayout) v.getParent();
        if(lnChild.getId() > 0){
            list_del_agenda.add(lnChild.getId());
        }
        lnPai.removeView(lnChild);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.getItem(0);
        if (stateEdtiLayout) {
            item.setIcon(R.drawable.ic_save_black_24dp);
        }else{
            item.setIcon(R.drawable.ic_edit_black_24dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            if (stateEdtiLayout) {
                stateEdtiLayout = false;
                item.setIcon(R.drawable.ic_edit_black_24dp);

                setViewEditable(stateEdtiLayout);

                TaskConfig task = new TaskConfig("PUT");
                task.execute(getParam());
            }else{
                stateEdtiLayout = true;
                item.setIcon(R.drawable.ic_save_black_24dp);
                setViewEditable(stateEdtiLayout);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getParam(){
        String id_rele = "&id_rele=";
        String id_agenda = "&agenda_id=";
        String data_begin = "&data_begin=";
        String data_end = "&data_end=";
        String repetir = "&repetir=";
        LinearLayout ln_agenda = (LinearLayout) findViewById(R.id.ln_agenda_bomba);
        for(int x=1; x < ln_agenda.getChildCount()-1;x++){
            LinearLayout l = (LinearLayout) ln_agenda.getChildAt(x);
            id_rele += "2,";
            id_agenda += l.getId() + ",";
            data_begin += ( (Button) l.getChildAt(1) ).getText() + ",";
            data_end += ( (Button) l.getChildAt(3) ).getText() + ",";
            repetir += "0,";
        }
        ln_agenda = (LinearLayout) findViewById(R.id.ln_agenda_motor);
        for(int x=1; x < ln_agenda.getChildCount()-1;x++){
            LinearLayout l = (LinearLayout) ln_agenda.getChildAt(x);
            id_rele += "1,";
            id_agenda += l.getId() + ",";
            data_begin += ( (Button) l.getChildAt(1) ).getText()  + ",";
            data_end += "0";
            repetir += ( (EditText) l.getChildAt(3) ).getText() + ",";
        }

        if(id_rele.endsWith(",")){
            id_rele = id_rele.substring(0,id_rele.length()-1);
            id_agenda=  id_agenda.substring(0,id_agenda.length()-1);
            data_begin = data_begin.substring(0,data_begin.length()-1);
            data_end = data_end.substring(0,data_end.length()-1);
            repetir = repetir.substring(0,repetir.length()-1);
        }

        return  "id=1,2,3" +
                "&value_min="+bar_temp_min.getProgress()+","+textVazao.getText()+","+textPH.getText()+
                "&value_max="+bar_temp_max.getProgress()+",0,0"+
                id_rele+
                id_agenda+
                data_begin+
                data_end+
                repetir;
    }

    private void setViewEditable(boolean stat) {
        LinearLayout vg = (LinearLayout) findViewById(R.id.screen_config);
        bar_temp_min.setEnabled(stat);
        bar_temp_max.setEnabled(stat);
        textVazao.setFocusableInTouchMode(stat);
        textPH.setFocusableInTouchMode(stat);
        for (int i = 0; i < vg.getChildCount(); i++) {
            LinearLayout ln = (LinearLayout) vg.getChildAt(i);
            for (int x = 0; x < ln.getChildCount(); x++) {
                setViewStatus(ln.getChildAt(x),stat);
            }
        }
    }
    private void setViewStatus(View subView , Boolean status){
        if(status){
            if (subView instanceof EditText) {
                subView.setEnabled(status);
                subView.setFocusable(status);
                ((EditText) subView).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorTextUnable, null));
            } else if(subView instanceof Button){
                subView.setEnabled(status);
                ((Button) subView).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorTextUnable, null));
            }else if(subView instanceof TextView){
                ((TextView) subView).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorEditUnable, null));
            }
        }else{
            if (subView instanceof EditText) {
                subView.setEnabled(status);
                ((EditText) subView).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorTextDisable, null));
            } else if(subView instanceof Button) {
                subView.setEnabled(status);
                ((Button) subView).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorTextDisable, null));
            }else if(subView instanceof TextView){
                ((TextView) subView).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorEditDisable, null));
            }
        }
        if (subView instanceof LinearLayout) {
            LinearLayout ln = (LinearLayout) subView;
            for (int x = 0; x < ln.getChildCount(); x++) {
                setViewStatus2(ln.getChildAt(x), status);
            }
        }
    }
    private void setViewStatus2(View subView , Boolean status){
        if(status){
            if (subView instanceof EditText) {
                subView.setEnabled(status);
                subView.setBackground(getResources().getDrawable(R.drawable.view_round_enable));
                ((EditText) subView).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorTextUnable, null));
            }else if(subView instanceof Button) {
                subView.setEnabled(status);
                subView.setBackground(getResources().getDrawable(R.drawable.view_round_enable));
                ((Button) subView).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorTextUnable, null));
            }else if(subView instanceof TextView) {
                ((TextView) subView).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorEditUnable, null));
            }else if(subView instanceof ImageButton){
                subView.setEnabled(status);
            }
        }else{
            if (subView instanceof EditText) {
                subView.setEnabled(status);
                subView.setBackground(getResources().getDrawable(R.drawable.view_round_disable));
                ((EditText) subView).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorTextDisable, null));
            }else if(subView instanceof Button) {
                subView.setEnabled(status);
                subView.setBackground(getResources().getDrawable(R.drawable.view_round_disable));
                ((Button) subView).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorTextDisable, null));
            }else if(subView instanceof TextView) {
                ((TextView) subView).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorEditDisable, null));
            }else if(subView instanceof ImageButton){
                subView.setEnabled(status);
            }
        }
    }

    private class TaskConfig extends AsyncTask<String, Void, JSONArray> {
        private ProgressDialog pDialog;
        private String text_dialog;
        private String erro;
        private String metodo;
        private String path;

        private TaskConfig(String metodo){
            this.metodo = metodo;
            if(metodo == "GET"){
                text_dialog = "Carregando informações...";
            }else{
                text_dialog = "Salvando informações...";
            }
        }
        protected void onPreExecute(){
            pDialog = new ProgressDialog(atividade);
            pDialog.setMessage(text_dialog);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected JSONArray doInBackground(String... param) {
            JSONArray dados = null;
            try {
                dados = APIHTTP.getArray("setup/agendamento",metodo,param[0]);

                for(int x=0; x < list_del_agenda.size();x++){
                    APIHTTP.getArray("setup/agendamento/"+list_del_agenda.get(x),"DELETE","");
                }
                list_del_agenda = new ArrayList<Integer>();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("API", e.getMessage());
                this.erro = e.getMessage();
            }
            return dados;
        }

        @Override
        protected void onPostExecute(JSONArray array){
            if(array != null){
                try {
                    if(metodo == "GET") {
                        JSONArray agenda = array.getJSONArray(1);
                        JSONArray config = array.getJSONArray(0);

                        bar_temp_min.setProgress(Integer.parseInt(config.getJSONArray(0).getString(1).substring(0,config.getJSONArray(0).getString(1).length()-3)));
                        bar_temp_max.setProgress(Integer.parseInt(config.getJSONArray(0).getString(2).substring(0,config.getJSONArray(0).getString(2).length()-3)));
                        textPH.setText(config.getJSONArray(2).getString(1));
                        textVazao.setText(config.getJSONArray(1).getString(1));

                        for (int x = 0; x < agenda.length(); x++) {
                            if(agenda.getJSONArray(x).getString(1).contentEquals("1")) {
                                JSONArray agenda_alimentar = agenda.getJSONArray(x);
                                addAgendamento(findViewById(R.id.btn_add_alimenta));
                                ln.setId(Integer.parseInt(agenda_alimentar.getString(0)));
                                ((Button) ln.getChildAt(1)).setText(agenda_alimentar.getString(2).substring(0, 5));
                                ((EditText) ln.getChildAt(3)).setText(agenda_alimentar.getString(4));
                            }else if(agenda.getJSONArray(x).getString(1).contentEquals("2")) {
                                JSONArray agenda_bomba = agenda.getJSONArray(x);
                                addAgendamento(findViewById(R.id.btn_add_bomba));
                                ln.setId(Integer.parseInt(agenda_bomba.getString(0)));
                                ((Button) ln.getChildAt(1)).setText(agenda_bomba.getString(2).substring(0, 5));
                                ((Button) ln.getChildAt(3)).setText(agenda_bomba.getString(3).substring(0, 5));
                            }
                        }

                        setViewEditable(stateEdtiLayout);
                    }else{
                        Toast.makeText(atividade, "Salvo.",Toast.LENGTH_LONG).show();
                    }
                    MainActivity.C_TEMP_MIN = Integer.toString(bar_temp_min.getProgress());
                    MainActivity.C_TEMP_MAX = Integer.toString(bar_temp_max.getProgress());
                    MainActivity.C_PH = textPH.getText().toString();
                    MainActivity.C_VAZAO = textVazao.getText().toString();
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
