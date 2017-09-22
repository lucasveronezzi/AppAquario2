package beta.user.appaquario2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import org.json.JSONObject;

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
    private static final String KEY_STATE_AGENDA = "state_agenda";
    private final int SET_CONFIG = 0;
    private final int GET_CONFIG = 1;
    private List<Integer> list_del_ag_luz;
    private List<Integer> list_del_ag_alimentacao;

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

        Intent intent = getIntent();

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

        TaskConfig task = new TaskConfig(GET_CONFIG);
        task.execute("query=get_config");
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
            if(lnPai.getId() == R.id.ln_agenda_luz){
                list_del_ag_luz.add(lnChild.getId());
            }else if(lnPai.getId() == R.id.ln_agenda_motor){
                list_del_ag_alimentacao.add(lnChild.getId());
            }
        }
        lnPai.removeView(lnChild);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

                TaskConfig task = new TaskConfig(SET_CONFIG);
                String array_ag_luz = "";
                LinearLayout ln_agenda = (LinearLayout) findViewById(R.id.ln_agenda_luz);
                for(int x=1; x < ln_agenda.getChildCount()-1;x++){
                    LinearLayout l = (LinearLayout) ln_agenda.getChildAt(x);
                    array_ag_luz += "|" + l.getId() + ";" +
                            ( (Button) l.getChildAt(1) ).getText() + ";" +
                            ( (Button) l.getChildAt(3) ).getText();
                }
                if(array_ag_luz.length() > 0)
                    array_ag_luz = array_ag_luz.substring(1);

                String array_ag_alimentar = "";
                ln_agenda = (LinearLayout) findViewById(R.id.ln_agenda_motor);
                for(int x=1; x < ln_agenda.getChildCount()-1;x++){
                    LinearLayout l = (LinearLayout) ln_agenda.getChildAt(x);
                    array_ag_alimentar += "|" + l.getId() + ";" +
                            ( (Button) l.getChildAt(1) ).getText() + ";" +
                            ( (EditText) l.getChildAt(3) ).getText();
                }
                if(array_ag_alimentar.length() > 0)
                    array_ag_alimentar = array_ag_alimentar.substring(1);

                String array_del_luz = "";
                for(int x=0; x < list_del_ag_luz.size();x++){
                    array_del_luz += "|" + list_del_ag_luz.get(x);
                }
                if(array_del_luz.length() > 0)
                    array_del_luz = array_del_luz.substring(1);

                String array_del_alimentar = "";
                for(int x=0; x < list_del_ag_alimentacao.size();x++){
                    array_del_alimentar += "|" + list_del_ag_alimentacao.get(x);;
                }
                if(array_del_alimentar.length() > 0)
                    array_del_alimentar = array_del_alimentar.substring(1);

                String param = "query=save_config" +
                        "&temp_min="+ bar_temp_min.getProgress() +
                        "&temp_max="+bar_temp_max.getProgress() +
                        "&ph="+textPH.getText() +
                        "&vazao="+textVazao.getText() +
                        "&ag_luz="+array_ag_luz +
                        "&ag_alimentar="+array_ag_alimentar +
                        "&del_luz=" + array_del_luz +
                        "&del_alimentar=" + array_del_alimentar;
                task.execute(param);
            }else{
                stateEdtiLayout = true;
                item.setIcon(R.drawable.ic_save_black_24dp);
                setViewEditable(stateEdtiLayout);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setViewEditable(boolean stat) {
        LinearLayout vg = (LinearLayout) findViewById(R.id.screen_config);
        if (!stat) {
            bar_temp_min.setEnabled(false);
            bar_temp_max.setEnabled(false);
            for (int i = 0; i < vg.getChildCount(); i++) {
                LinearLayout ln = (LinearLayout) vg.getChildAt(i);
                for (int x = 0; x < ln.getChildCount(); x++) {
                    View subView = ln.getChildAt(x);
                    if (subView instanceof EditText) {
                        subView.setEnabled(false);
                        //subView.setFocusable(false);
                        //subView.setBackground(getResources().getDrawable(R.drawable.view_round_disable));
                        ((EditText) subView).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorTextDisable, null));
                    } else if(subView instanceof TextView) {
                        ((TextView) subView).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorEditDisable, null));
                    }if(subView instanceof Button){
                        subView.setEnabled(false);
                        ((Button) subView).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorTextDisable, null));
                    }else if (subView instanceof LinearLayout) {
                        LinearLayout ln2 = (LinearLayout) ln.getChildAt(x);
                        for (int y = 0; y < ln2.getChildCount(); y++) {
                            View subView2 = ln2.getChildAt(y);
                            if (subView2 instanceof EditText) {
                                subView2.setEnabled(false);
                                subView2.setBackground(getResources().getDrawable(R.drawable.view_round_disable));
                                ((EditText) subView2).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorTextDisable, null));
                            }else if(subView2 instanceof TextView) {
                                ((TextView) subView2).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorEditDisable, null));
                            }if(subView2 instanceof Button) {
                                subView2.setEnabled(false);
                                subView2.setBackground(getResources().getDrawable(R.drawable.view_round_disable));
                                ((Button) subView2).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorTextDisable, null));
                            }else if(subView2 instanceof ImageButton){
                                subView2.setEnabled(false);
                            }
                        }
                    }
                }
            }
        } else {
            textVazao.setFocusableInTouchMode(true);
            textPH.setFocusableInTouchMode(true);
            bar_temp_min.setEnabled(true);
            bar_temp_max.setEnabled(true);
            for (int i = 0; i < vg.getChildCount(); i++) {
                LinearLayout ln = (LinearLayout) vg.getChildAt(i);
                for (int x = 0; x < ln.getChildCount(); x++) {
                    View subView = ln.getChildAt(x);
                    if (subView instanceof EditText) {
                        subView.setEnabled(true);
                        subView.setFocusable(true);
                        //subView.setBackground(getResources().getDrawable(R.drawable.view_round_enable));
                        ((EditText) subView).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorTextUnable, null));
                    } else if(subView instanceof TextView){
                        ((TextView) subView).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorEditUnable, null));
                    }if(subView instanceof Button){
                        subView.setEnabled(true);
                        ((Button) subView).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorTextUnable, null));
                    }else if (subView instanceof LinearLayout) {
                        LinearLayout ln2 = (LinearLayout) ln.getChildAt(x);
                        for (int y = 0; y < ln2.getChildCount(); y++) {
                            View subView2 = ln2.getChildAt(y);
                            if (subView2 instanceof EditText) {
                                subView2.setEnabled(true);
                                subView2.setBackground(getResources().getDrawable(R.drawable.view_round_enable));
                                ((EditText) subView2).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorTextUnable, null));
                            }else if(subView2 instanceof TextView) {
                                ((TextView) subView2).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorEditUnable, null));
                            }if(subView2 instanceof Button) {
                                subView2.setEnabled(true);
                                subView2.setBackground(getResources().getDrawable(R.drawable.view_round_enable));
                                ((Button) subView2).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorTextUnable, null));
                            }else if(subView2 instanceof ImageButton){
                                subView2.setEnabled(true);
                            }
                        }
                    }
                }

            }
        }
    }

    private class TaskConfig extends AsyncTask<String, Void, JSONArray> {
        private ProgressDialog pDialog;
        private String text_dialog;
        private String erro;
        private int op;

        private TaskConfig(int op){
            this.op = op;
            if(op == GET_CONFIG){
                text_dialog = "Carregando informações...";
            }else{
                text_dialog = "Salvando informações...";
            }
        }
        protected void onPreExecute(){
            list_del_ag_alimentacao = new ArrayList<Integer>();
            list_del_ag_luz = new ArrayList<Integer>();

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
                dados = APIHTTP.getArray(param[0]);
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
                    if(op == GET_CONFIG) {
                        JSONArray agenda_alimentar = array.getJSONArray(0);
                        JSONArray agenda_luz = array.getJSONArray(1);
                        JSONArray config = array.getJSONArray(2);

                        bar_temp_min.setProgress(Integer.parseInt(config.getString(0)));
                        bar_temp_max.setProgress(Integer.parseInt(config.getString(1)));
                        textPH.setText(config.getString(2));
                        textVazao.setText(config.getString(3));

                        for (int x = 0; x < agenda_alimentar.length(); x++) {
                            JSONArray arr = agenda_alimentar.getJSONArray(x);
                            addAgendamento(findViewById(R.id.btn_add_alimenta));
                            ln.setId(Integer.parseInt(arr.getString(0)));
                            ((Button) ln.getChildAt(1)).setText(arr.getString(1).substring(0, 5));
                            ((EditText) ln.getChildAt(3)).setText(arr.getString(2));
                        }
                        for (int x = 0; x < agenda_luz.length(); x++) {
                            JSONArray arr = agenda_luz.getJSONArray(x);
                            addAgendamento(findViewById(R.id.btn_add_luz));
                            ln.setId(Integer.parseInt(arr.getString(0)));
                            ((Button) ln.getChildAt(1)).setText(arr.getString(1).substring(0, 5));
                            ((Button) ln.getChildAt(3)).setText(arr.getString(2).substring(0, 5));
                        }

                        setViewEditable(stateEdtiLayout);
                    }else{
                        Toast.makeText(atividade, "Salvo.",Toast.LENGTH_LONG).show();
                    }
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
