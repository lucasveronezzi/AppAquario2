package beta.user.appaquario2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import beta.user.appaquario2.Graficos.Screen_grafico;

public class MainActivity extends AppCompatActivity {
    public static String C_TEMP_MIN = "0";
    public static String C_TEMP_MAX = "0";
    public static String C_PH = "0.00";
    public static String C_VAZAO = "0.00";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
    }

    public void open_screen_config(View view) {
        Intent intent = new Intent(this, Screen_Config.class);
        startActivity(intent);
    }

    public void open_screen_grafico(View view) {
        Intent intent = new Intent(this, Screen_grafico.class);
        startActivity(intent);
    }

    public void open_screen_monitor(View view) {
        Intent intent = new Intent(this, Screen_Monitor.class);
        startActivity(intent);
    }

    public static boolean stringToBool(String s) {
        if (s.equals("1"))
            return true;
        if (s.equals("0"))
            return false;
        throw new IllegalArgumentException(s+" is not a bool. Only 1 and 0 are.");
    }

}
