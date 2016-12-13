package alexyou.tetrisplus;

/**
 * Created by Alex on 12/2/2016.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, StartScreen.class);
        startActivity(intent);
        finish();
    }
}
