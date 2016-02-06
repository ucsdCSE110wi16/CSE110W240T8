package droidsquad.voyage.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import droidsquad.voyage.R;
import droidsquad.voyage.controller.TripController;
import droidsquad.voyage.model.Trip;

public class TripActivity extends AppCompatActivity {
    private CollapsingToolbarLayout mCollapsingToolbar;
    private FloatingActionButton mFAB;
    private ImageView mHeaderImageView;
    private TextView mTripLocTextView;
    private TextView mTripDatesTextView;
    private TripController mController;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        initUI();
        mController = new TripController(this, (Trip) getIntent()
                .getSerializableExtra(getString(R.string.intent_key_trip)));

        mController.setGooglePlacePhoto(mHeaderImageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trip_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.trip_action_share:
                startShareIntent();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Initialize all the UI elements of this Activity
     */
    private void initUI() {
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mHeaderImageView = (ImageView) findViewById(R.id.header_image);
        mFAB = (FloatingActionButton) findViewById(R.id.fab);
        mTripLocTextView = (TextView) findViewById(R.id.trip_locations);
        mTripDatesTextView = (TextView) findViewById(R.id.trip_dates);

        // Set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mCollapsingToolbar.setExpandedTitleTypeface(Typeface.create("sans-serif", Typeface.BOLD));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Set the colors of the toolbar and FAB based on the header image
     */
    public void setColors() {
        Bitmap bitmap = ((BitmapDrawable) mHeaderImageView.getDrawable()).getBitmap();

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int colorPrimary = ContextCompat.getColor(
                        getApplicationContext(), R.color.colorPrimary);
                int colorPrimaryDark = ContextCompat.getColor(
                        getApplicationContext(), R.color.colorPrimaryDark);
                int colorAccent = ContextCompat.getColor(
                        getApplicationContext(), R.color.colorAccent);

                Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                int vibrantColor = palette.getVibrantColor(colorAccent);

                // Status bar and toolbar color
                mCollapsingToolbar.setContentScrimColor(
                        (mutedSwatch != null) ? mutedSwatch.getRgb() : colorPrimary);
                setStatusBarColor(
                        (mutedSwatch != null) ? darkenColor(mutedSwatch.getRgb()) : colorPrimaryDark);

                // Floating Action Button color
                mFAB.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
            }
        });
    }

    /**
     * Set the color of the status bar
     *
     * @param color Color to be set
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(int color) {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(color);
    }

    /**
     * Get a darker version of a color
     *
     * @param color Color to be darkened
     * @return Darkened color
     */
    private int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    /**
     * Displays a Chooser menu for sharing content
     *
     * TODO: Set up the content to be shared here
     */
    private void startShareIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share"));
    }

    public void setTripName(String name) {
        mCollapsingToolbar.setTitle(name);
    }

    public void setTripLocation(String location) {
        mTripLocTextView.setText(location);
    }

    public void setTripDates(String dates) {
        mTripDatesTextView.setText(dates);
    }
}
