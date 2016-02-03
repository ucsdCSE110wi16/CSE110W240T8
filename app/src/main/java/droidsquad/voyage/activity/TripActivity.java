package droidsquad.voyage.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import droidsquad.voyage.R;

public class TripActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trip);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.trip_collapsing_toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.trip_toolbar);

        collapsingToolbar.setTitle("Backpacking in Yosemite");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // TODO instantiate TripController and use the method setGooglePlacePhoto(imageView)
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trip_toolbar, menu);
        return true;
    }
}
