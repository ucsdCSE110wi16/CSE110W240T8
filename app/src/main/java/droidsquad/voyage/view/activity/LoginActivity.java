package droidsquad.voyage.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.parse.ParseFacebookUtils;

import droidsquad.voyage.R;
import droidsquad.voyage.model.objects.VoyageUser;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = LoginActivity.class.getSimpleName();
    private Button mLoginWithFBButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set Facebook Login Button click listener
        Button mLoginWithFBButton = (Button) findViewById(R.id.facebook_login_button);
        mLoginWithFBButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                VoyageUser.attemptFBLogin(LoginActivity.this, v);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}