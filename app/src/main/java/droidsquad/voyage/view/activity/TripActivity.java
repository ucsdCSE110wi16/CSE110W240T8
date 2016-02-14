package droidsquad.voyage.view.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import droidsquad.voyage.R;
import droidsquad.voyage.controller.activityController.TripController;
import droidsquad.voyage.model.objects.Trip;

public class TripActivity extends AppCompatActivity {
    private CollapsingToolbarLayout mCollapsingToolbar;
    private FloatingActionButton mFAB;
    private ImageView mHeaderImageView;
    private TextView mTripLocTextView;
    private TextView mTripDatesTextView;
    private TripController mController;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        mController = new TripController(this);

        initUI();
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TripActivity.this, AddFriendsActivity.class);
                startActivity(intent);
            }
        });

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
            case android.R.id.home:
                finish();
                return true;
            case R.id.trip_action_share:
                startShareIntent();
                return true;
            case R.id.trip_action_edit:
                mController.editTrip();
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

        // Set the dates
        String dates = getString(R.string.trip_dates,
                dateFormat.format(mController.getDateFrom()),
                dateFormat.format(mController.getDateTo()));
        mTripDatesTextView.setText(dates);

        // Set the locations
        String transportation = getString(R.string.trip_locations,
                mController.getOrigin(), mController.getDestination());
        mTripLocTextView.setText(transportation);
        mTripLocTextView.setCompoundDrawablesWithIntrinsicBounds(mController.getDrawableId(), 0, 0, 0);

        // Set up toolbar and action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCollapsingToolbar.setExpandedTitleTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        mCollapsingToolbar.setTitle(mController.getTitle());

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
                int colorAccent = ContextCompat.getColor(
                        getApplicationContext(), R.color.colorAccent);

                Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                int vibrantColor = palette.getVibrantColor(colorAccent);

                // Status bar and toolbar color
                int toolbarColor = (mutedSwatch != null) ? mutedSwatch.getRgb() : colorPrimary;
                mCollapsingToolbar.setContentScrimColor(toolbarColor);
                mCollapsingToolbar.setStatusBarScrimColor(toolbarColor);

                // Floating Action Button color
                mFAB.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
            }
        });
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

    /**
     * Called to start the intent for editing the trip via CreateTripActivity with an extra boolean
     * to indicate the trip is being edited
     * @param trip  current trip, needed to populate the CreateTripActivity fields
     */
    public void editTripIntent(Trip trip) {
        Intent intent = new Intent(this, CreateTripActivity.class);
        intent.putExtra(this.getString(R.string.intent_key_trip), trip);
        intent.putExtra(getString(R.string.edit_trip), true);
        startActivity(intent);
    }
}
