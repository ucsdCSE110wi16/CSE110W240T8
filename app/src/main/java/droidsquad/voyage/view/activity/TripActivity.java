package droidsquad.voyage.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import droidsquad.voyage.model.adapters.FBFriendsAdapter;
import droidsquad.voyage.model.objects.User;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.util.Constants;

public class TripActivity extends AppCompatActivity {
    private CollapsingToolbarLayout mCollapsingToolbar;
    private FloatingActionButton mFAB;
    private ImageView mHeaderImageView;
    private TextView mTripLocTextView;
    private TextView mTripDatesTextView;
    private TripController mController;
    private RecyclerView mMembersRecyclerView;
    private RecyclerView mInviteesRecyclerView;

    private static final String LEAVE_TRIP_ALERT = "LEAVE_TRIP_ALERT";
    private static final String DELETE_TRIP_ALERT = "DELETE_TRIP_ALERT";
    private static final String REMOVE_MEMBER_ALERT = "REMOVE_MEMBER_ALERT";
    private static final String REMOVE_INVITEE_ALERT = "REMOVE_INVITEE_ALERT";


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
                intent.putExtra(getString(R.string.intent_key_trip), mController.trip);
                startActivityForResult(intent, Constants.REQUEST_CODE_ADD_FRIENDS_ACTIVITY);
            }
        });

        mController = new TripController(this);
        populateData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Snackbar snackbar = Snackbar.make(mMembersRecyclerView, "", Snackbar.LENGTH_SHORT);
        switch (requestCode) {

            case Constants.REQUEST_CODE_CREATE_TRIP_ACTIVITY:
                if (resultCode == Constants.RESULT_CODE_TRIP_UPDATED) {
                    mController.trip = data.getParcelableExtra(getString(R.string.intent_key_trip));
                    populateData();
                    snackbar.setText(R.string.snackbar_trip_updated);
                    snackbar.show();
                }
                break;

            case Constants.REQUEST_CODE_ADD_FRIENDS_ACTIVITY:
                if (resultCode == Constants.RESULT_CODE_INVITEES_ADDED) {
                    mController.trip = data.getParcelableExtra(getString(R.string.intent_key_trip));
                    populateData();
                    snackbar.setText(R.string.snackbar_invitees_added);
                    snackbar.show();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trip_menu, menu);
        setUIForCreator(mController.isCreator(), menu);
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

            case R.id.trip_action_delete_trip:
                showAlertDialog(DELETE_TRIP_ALERT, null);
                return true;

            case R.id.trip_action_leave_trip:
                showAlertDialog(LEAVE_TRIP_ALERT, null);
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

        mMembersRecyclerView = (RecyclerView) findViewById(R.id.members_recycler_view);
        mMembersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mMembersRecyclerView.setNestedScrollingEnabled(false);
        mMembersRecyclerView.setHasFixedSize(false);

        mInviteesRecyclerView = (RecyclerView) findViewById(R.id.invitees_recycler_view);
        mInviteesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mInviteesRecyclerView.setNestedScrollingEnabled(false);
        mInviteesRecyclerView.setHasFixedSize(false);

        // Set up toolbar and action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCollapsingToolbar.setExpandedTitleTypeface(Typeface.create("sans-serif", Typeface.BOLD));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void populateData() {
        mTripDatesTextView.setText(mController.getDatesStringRepresentation());
        mTripLocTextView.setText(mController.getTransportationStringRepresentation());
        mTripLocTextView.setCompoundDrawablesWithIntrinsicBounds(mController.getTransportationIcon(), 0, 0, 0);

        mCollapsingToolbar.setTitle(mController.getTitle());

        // set and populate members and invitees list
        mMembersRecyclerView.setAdapter(mController.mMemAdapter);
        mController.updateMembersAdapter();

        mInviteesRecyclerView.setAdapter(mController.mInviteesAdapter);
        mController.updateInviteesAdapter();

        if (mController.isCreator()) {
            mController.mMemAdapter.setOnClickListener(new FBFriendsAdapter.OnClickListener() {
                @Override
                public void onClick(User user) {
                    showAlertDialog(REMOVE_MEMBER_ALERT, user);
                }
            });

            mController.mInviteesAdapter.setOnClickListener(new FBFriendsAdapter.OnClickListener() {
                @Override
                public void onClick(User user) {
                    showAlertDialog(REMOVE_INVITEE_ALERT, user);
                }
            });
        }
    }

    private void showAlertDialog(final String alertType, @Nullable final User user) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(getAlertDialogMessage(alertType, user));

        alertDialog.setPositiveButton(getAlertPositiveButtonText(alertType),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onAlertDialogPositiveClick(alertType, user);
                    }
                });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void onAlertDialogPositiveClick(String alertType, User user) {
        switch (alertType) {
            case LEAVE_TRIP_ALERT:
                mController.leaveTrip();
                break;

            case DELETE_TRIP_ALERT:
                mController.deleteTrip();
                break;

            case REMOVE_MEMBER_ALERT:
                mController.kickMember(user);
                break;

            case REMOVE_INVITEE_ALERT:
                mController.kickInvitee(user);
                break;
        }
    }

    private String getAlertDialogMessage(String alertType, User user) {
        switch (alertType) {
            case LEAVE_TRIP_ALERT:
                return getString(R.string.leave_trip_alert);

            case DELETE_TRIP_ALERT:
                return getString(R.string.delete_trip_alert);

            case REMOVE_MEMBER_ALERT:
                return getString(R.string.kick_friend_alert, user.getFullName(), mController.trip.getName());

            case REMOVE_INVITEE_ALERT:
                return getString(R.string.kick_friend_alert, user.getFullName(), mController.trip.getName());

            default:
                return "";
        }
    }

    private String getAlertPositiveButtonText(String alertType) {
        switch (alertType) {
            case LEAVE_TRIP_ALERT:
                return "Leave";

            case DELETE_TRIP_ALERT:
                return "Delete";

            case REMOVE_MEMBER_ALERT:
                return "Remove";

            case REMOVE_INVITEE_ALERT:
                return "Remove";

            default:
                return "";
        }
    }

    private void setUIForCreator(boolean isCreator, Menu menu) {
        // TODO: add if clause for if just spectator (not actually in trip)
        if (isCreator) {
            menu.findItem(R.id.trip_action_leave_trip).setVisible(false);
        } else {
            // Just a member
            // Shouldn't be able to delete the trip
            menu.findItem(R.id.trip_action_delete_trip).setVisible(false);

            // Shouldn't be able to add people
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mFAB.getLayoutParams();
            params.setAnchorId(View.NO_ID);
            mFAB.setLayoutParams(params);
            mFAB.setVisibility(View.GONE);

            // Shouldn't be able to edit trip
            menu.findItem(R.id.trip_action_edit).setVisible(false);
        }
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
     * <p/>
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
     *
     * @param trip current trip, needed to populate the CreateTripActivity fields
     */
    public void editTripIntent(Trip trip) {
        Intent intent = new Intent(this, CreateTripActivity.class);
        intent.putExtra(this.getString(R.string.intent_key_trip), trip);
        intent.putExtra(getString(R.string.edit_trip), true);
        startActivityForResult(intent, Constants.REQUEST_CODE_CREATE_TRIP_ACTIVITY);
    }
}
