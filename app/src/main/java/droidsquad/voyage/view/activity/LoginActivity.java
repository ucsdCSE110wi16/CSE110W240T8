package droidsquad.voyage.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.parse.ParseFacebookUtils;

import droidsquad.voyage.R;
import droidsquad.voyage.model.objects.VoyageUser;

public class LoginActivity extends AppCompatActivity {
    private boolean currentlyLoggingIn = false;

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    // UI references.
    private Button mLoginWithFBButton;
    private final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginWithFBButton = (Button) findViewById(R.id.login_with_facebook_button);
        mLoginWithFBButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard();
                VoyageUser.attemptFBLogin(LoginActivity.this, v);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Hides keyboard from the user
     **/
    private void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}