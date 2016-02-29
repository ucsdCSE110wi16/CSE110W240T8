package droidsquad.voyage.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.parse.ParseUser;

import droidsquad.voyage.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // check if the user is logged in
        final Intent intent;
        if (ParseUser.getCurrentUser() == null) {
            intent = new Intent(this, LoginActivity.class);
        } else {
            intent = new Intent(this, MainNavDrawerActivity.class);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(0, R.anim.fade_out);
            }
        },1000);
    }
}
