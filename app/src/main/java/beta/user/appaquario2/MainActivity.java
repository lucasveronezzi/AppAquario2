package beta.user.appaquario2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void open_screen_config(View view) {
        Intent intent = new Intent(this, Screen_Config.class);
        startActivity(intent);
    }

    public void open_screen_monitor(View view) {
        Intent intent = new Intent(this, Screen_Monitor.class);
        startActivity(intent);
    }

}
