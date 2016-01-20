package droidsquad.voyage.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import droidsquad.voyage.R;
import droidsquad.voyage.controller.CreateTripController;

public class CreateTripActivity extends AppCompatActivity {

    private CreateTripController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        controller = new CreateTripController(this);
    }
}
