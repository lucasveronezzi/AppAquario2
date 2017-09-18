package beta.user.appaquario2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
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

public class Screen_Config extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            stateEdtiLayout= savedInstanceState.getBoolean(KEY_STATE_EDIT);
            ln = new LayoutAgendamento(this);
            //onRestoreInstanceState(savedInstanceState);
            ln.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_STATE_AGENDA));

        }
        setContentView(R.layout.activity_screen__config);

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
                temp_max.setText(progresValue + "°C");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        setViewEditable(stateEdtiLayout);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(KEY_STATE_AGENDA,ln.onSaveInstanceState());
        savedInstanceState.putBoolean(KEY_STATE_EDIT, stateEdtiLayout);
    }


    public void showTimePickerDialog(View v) {
        DialogFragment fragmentTimePick = new TimePickerFragment(v);
        fragmentTimePick.show(getSupportFragmentManager(), "timePicker");
    }

    public void addAgendamento(View v){
        LinearLayout lnParent = (LinearLayout) v.getParent();
        ln = new LayoutAgendamento(this, lnParent);
        ln.setId(ln.generateViewId());
        lnParent.addView(ln,lnParent.getChildCount()-1);

        showTimePickerDialog(ln.getViewBtn());
        Toast.makeText(this, "ID: " + ln.getId(), Toast.LENGTH_LONG).show();
    }

    public void removeAgendamento(View v){
        LinearLayout lnPai = (LinearLayout) ((ViewGroup) v.getParent()).getParent();
        LinearLayout lnChild = (LinearLayout) v.getParent();
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

}
