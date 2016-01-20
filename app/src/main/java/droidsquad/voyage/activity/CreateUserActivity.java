package droidsquad.voyage.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import droidsquad.voyage.R;
import droidsquad.voyage.controller.CreateUserController;

public class CreateUserActivity extends AppCompatActivity {

    private CreateUserController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        controller = new CreateUserController(this);
    }
}
