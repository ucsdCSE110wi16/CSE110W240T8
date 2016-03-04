package droidsquad.voyage.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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

import droidsquad.voyage.R;
import droidsquad.voyage.controller.activityController.TripController;
import droidsquad.voyage.model.adapters.TripMembersAdapter;
import droidsquad.voyage.model.objects.Member;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.util.Constants;

public class TripActivity extends AppCompatActivity {
    private TripController mController;
    private TextView mTripLocationsTextView;
    private TextView mTripDatesTextView;
    private TextView mMembersLabelView;
    private TextView mInviteesLabelView;
    private ImageView mHeaderImageView;
    private FloatingActionButton mFAB;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private RecyclerView mMembersRecyclerView;
    private RecyclerView mInviteesRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        mController = new TripController(this);
        initUI();
        populateData();

        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TripActivity.this, AddFriendsActivity.class);
                intent.putExtra(getString(R.string.intent_key_trip), mController.trip);
                startActivityForResult(intent, Constants.REQUEST_CODE_ADD_FRIENDS_ACTIVITY);
            }
        });

        if (mController.isAdmin()) {
            TripMembersAdapter.onDeleteMemberListener listener = new TripMembersAdapter.onDeleteMemberListener() {
                @Override
                public void onDelete(Member member) {
                    showRemoveMemberDialog(member);
                }
            };

            mController.mMembersAdapter.setOnDeleteMemberListener(listener);
            mController.mInviteesAdapter.setOnDeleteMemberListener(listener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_CREATE_TRIP_ACTIVITY:
                if (resultCode == Constants.RESULT_CODE_TRIP_UPDATED) {
                    mController.trip = data.getParcelableExtra(getString(R.string.intent_key_trip));
                    populateData();
                    Snackbar.make(mMembersRecyclerView,
                            R.string.snackbar_trip_updated, Snackbar.LENGTH_SHORT).show();
                }
                break;

            case Constants.REQUEST_CODE_ADD_FRIENDS_ACTIVITY:
                if (resultCode == Constants.RESULT_CODE_INVITEES_ADDED) {
                    mController.trip = data.getParcelableExtra(getString(R.string.intent_key_trip));
                    populateData();
                    Snackbar.make(mMembersRecyclerView,
                            R.string.snackbar_invitees_added, Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trip_menu, menu);

        if (mController.isAdmin()) {
            menu.findItem(R.id.trip_action_leave_trip).setVisible(false);
        } else {
            // Shouldn't be able to delete the trip
            menu.findItem(R.id.trip_action_delete_trip).setVisible(false);

            // Shouldn't be able to edit trip
            menu.findItem(R.id.trip_action_edit).setVisible(false);
        }

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
                showDeleteTripDialog();
                return true;

            case R.id.trip_action_leave_trip:
                showLeaveTripDialog();
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
        mTripLocationsTextView = (TextView) findViewById(R.id.trip_locations);
        mTripDatesTextView = (TextView) findViewById(R.id.trip_dates);
        mHeaderImageView = (ImageView) findViewById(R.id.header_image);
        mMembersLabelView = (TextView) findViewById(R.id.members_label);
        mInviteesLabelView = (TextView) findViewById(R.id.invitees_label);
        mFAB = (FloatingActionButton) findViewById(R.id.fab);

        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbar.setExpandedTitleTypeface(Typeface.create("sans-serif", Typeface.BOLD));

        mMembersRecyclerView = (RecyclerView) findViewById(R.id.members_recycler_view);
        mMembersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mMembersRecyclerView.setAdapter(mController.mMembersAdapter);
        mMembersRecyclerView.setNestedScrollingEnabled(false);
        mMembersRecyclerView.setHasFixedSize(false);

        mInviteesRecyclerView = (RecyclerView) findViewById(R.id.invitees_recycler_view);
        mInviteesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mInviteesRecyclerView.setAdapter(mController.mInviteesAdapter);
        mInviteesRecyclerView.setNestedScrollingEnabled(false);
        mInviteesRecyclerView.setHasFixedSize(false);

        if (!mController.isAdmin()) {
            // Shouldn't be able to add people
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mFAB.getLayoutParams();
            params.setAnchorId(View.NO_ID);
            mFAB.setLayoutParams(params);
            mFAB.setVisibility(View.GONE);
        }

        // Set up toolbar and action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Populate the UI elements and adapters
     */
    private void populateData() {
        mTripDatesTextView.setText(mController.getDatesStringRepresentation());
        mTripLocationsTextView.setText(mController.getTransportationStringRepresentation());
        mTripLocationsTextView.setCompoundDrawablesWithIntrinsicBounds(mController.getTransportationIcon(), 0, 0, 0);
        mCollapsingToolbar.setTitle(mController.getTitle());

        // populate members and invitees list
        mController.updateMembersAdapter();
        mController.updateInviteesAdapter();
    }

    private void showRemoveMemberDialog(final Member member) {
        showAlertDialog(getString(R.string.kick_friend_alert, member.user.getFullName(), mController.trip.getName()),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mController.kickMember(member);
                    }
                });
    }

    private void showDeleteTripDialog() {
        showAlertDialog(getString(R.string.delete_trip_alert), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mController.deleteTrip();
            }
        });
    }

    private void showLeaveTripDialog() {
        showAlertDialog(getString(R.string.leave_trip_alert), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mController.leaveTrip();
            }
        });
    }

    private void showAlertDialog(String alertMessage, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(alertMessage);

        alertDialog.setPositiveButton("OK", listener);
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
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
     *
     * @param trip current trip, needed to populate the CreateTripActivity fields
     */
    public void startEditTripIntent(Trip trip) {
        Intent intent = new Intent(this, CreateTripActivity.class);
        intent.putExtra(this.getString(R.string.intent_key_trip), trip);
        intent.putExtra(getString(R.string.edit_trip), true);
        startActivityForResult(intent, Constants.REQUEST_CODE_CREATE_TRIP_ACTIVITY);
    }

    public TextView getMembersLabel() {
        return mMembersLabelView;
    }

    public TextView getInviteesLabel() {
        return mInviteesLabelView;
    }
}
