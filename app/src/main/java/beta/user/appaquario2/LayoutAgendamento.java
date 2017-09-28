package beta.user.appaquario2;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * Created by Lucas on 17/09/2017.
 */
public class LayoutAgendamento extends LinearLayout {

    private int stateToSave;
    private Screen_Config context;

    public LayoutAgendamento(Context context){
        super(context);
    }

    public LayoutAgendamento(final Screen_Config context, LinearLayout lnParent) {
        super(context);
        this.context = context;

        setParams();
        createTextView("Ligar: ", new LinearLayout.LayoutParams(80,ViewGroup.LayoutParams.WRAP_CONTENT));
        createButton();

        LinearLayout.LayoutParams lp_textView = new LinearLayout.LayoutParams(100,ViewGroup.LayoutParams.WRAP_CONTENT);
        lp_textView.setMargins(10,0,0,0);

        if(lnParent.getId() == R.id.ln_agenda_bomba) {
            createTextView("Desligar: ", lp_textView);
            createButton();
        } else {
            createTextView("Qnt: ", lp_textView);
            createEditText();
        }
        createImgButton();
    }
    private void setParams(){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,0,0,10);
        this.setLayoutParams(lp);
        this.setGravity(Gravity.CENTER);
        this.setOrientation(LinearLayout.HORIZONTAL);
    }

    private void createTextView(String title, LayoutParams lp){
        TextView text = new TextView(context);
        text.setLayoutParams(lp);
        text.setText(title);
        text.setTextColor(getResources().getColor(R.color.colorTitleTextView));
        text.setGravity(Gravity.CENTER);
        text.setTextSize(16);

        this.addView(text);
    }

    private void createEditText(){
        EditText editTxt = new EditText(context);
        editTxt.setLayoutParams(new ViewGroup.LayoutParams(85,50));
        editTxt.setBackground(getResources().getDrawable(R.drawable.view_round_enable));
        editTxt.setTextColor(getResources().getColor(R.color.colorTextUnable));
        editTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTxt.setPadding(10,10,10,10);
        editTxt.setEms(10);

        this.addView(editTxt);
    }

    private void createButton(){
        Button btn = new Button(context);
        btn.setLayoutParams(new ViewGroup.LayoutParams(120,55));
        btn.setBackground(getResources().getDrawable(R.drawable.view_round_enable));
        btn.setTextColor(getResources().getColor(R.color.colorTextUnable));
        btn.setPadding(10,10,10,10);
        btn.setTextSize(16);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.showTimePickerDialog(v);
            }
        });

        this.addView(btn);
    }

    private void createImgButton(){
        ImageButton img = new ImageButton(context);
        img.setLayoutParams(new LinearLayout.LayoutParams(45,45,1));
        img.setBackgroundColor(Color.TRANSPARENT);
        img.setImageResource(getResources().getIdentifier("android:drawable/ic_delete",null,null));
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.removeAgendamento(v);
            }
        });

        this.addView(img);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.stateToSave = this.stateToSave;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.stateToSave = ss.stateToSave;
    }

    static class SavedState extends BaseSavedState {
        int stateToSave;
        SavedState(Parcelable superState) {
            super(superState);
        }
        private SavedState(Parcel in) {
            super(in);
            this.stateToSave = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.stateToSave);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
