package droidsquad.voyage.controller.activityController;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.parse.ParseUser;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;

import droidsquad.voyage.R;
import droidsquad.voyage.model.ParseTripModel;
import droidsquad.voyage.model.adapters.FBFriendsAdapter;
import droidsquad.voyage.model.api.GooglePlacesAPI;
import droidsquad.voyage.model.objects.FacebookUser;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.util.Constants;
import droidsquad.voyage.view.activity.AddFriendsActivity;
import droidsquad.voyage.view.activity.TripActivity;

public class TripController {
    private TripActivity mActivity;
    public Trip trip;

    private static final String TAG = TripController.class.getSimpleName();
    public FBFriendsAdapter mMemAdapter;

    public TripController(TripActivity instance) {
        this.mActivity = instance;

        trip = mActivity.getIntent().getParcelableExtra(
                mActivity.getString(R.string.intent_key_trip));

        mMemAdapter = new FBFriendsAdapter(mActivity, isCreator());
    }

    public void setGooglePlacePhoto(final ImageView imageView) {
        try {
            Log.d(TAG, "Attempting to get photo from Google Places");

            String placeID = trip.getDestination().getString("placeId");

            final GooglePlacesAPI googlePlacesAPI = new GooglePlacesAPI(mActivity);
            googlePlacesAPI.getPlaceImage(placeID, imageView.getWidth(), imageView.getHeight(),
                    new ResultCallback<PlacePhotoResult>() {
                        @Override
                        public void onResult(PlacePhotoResult placePhotoResult) {
                            if (!placePhotoResult.getStatus().isSuccess()) {
                                Log.d(TAG, "Couldn\'t retrieve the photo successfully.");
                                return;
                            }
                            Log.d(TAG, "Successfully retrieved photo from photo bundle.");

                            imageView.setImageBitmap(placePhotoResult.getBitmap());
                            mActivity.setColors();
                            googlePlacesAPI.disconnectGoogleAPIClient();
                        }
                    });
        } catch (JSONException e) {
            Log.d(TAG, "JSONException occurred: " + e.getMessage());
        }
    }

    /**
     * @return The id of the icon corresponding to the type of transportation
     */
    public int getDrawableId() {
        switch (trip.getTransportation()) {
            case "Bus":
                return R.drawable.ic_bus;
            case "Car":
                return R.drawable.ic_car;
            default:
                return R.drawable.ic_flight;
        }
    }

    public Date getDateFrom() {
        return trip.getDateFrom();
    }

    public Date getDateTo() {
        return trip.getDateTo();
    }

    public String getOrigin() {
        String origin = "";
        try {
            origin = trip.getOrigin().get("city").toString();
        } catch (JSONException e) {
            Log.d(TAG, "JSON exception occurred: " + e.getMessage());
        }

        return origin;
    }

    public String getDestination() {
        String destination = "";
        try {
            destination = trip.getDestination().get("city").toString();
        } catch (JSONException e) {
            Log.d(TAG, "JSON exception occurred: " + e.getMessage());
        }

        return destination;
    }

    public void launchAddFriends() {
        Intent intent = new Intent(mActivity, AddFriendsActivity.class);
        intent.putExtra(mActivity.getString(R.string.intent_key_trip), trip);
        mActivity.startActivity(intent);
    }

    public CharSequence getTitle() {
        return trip.getName();
    }

    public boolean isCreator() {
        return trip.getCreatorId().equals(ParseUser.getCurrentUser().getObjectId());
    }

    public void deleteTrip() {
        Log.d(TAG, "Deleting trip: " + trip.getName());
        ParseTripModel.deleteTrip(trip.getId(), new ParseTripModel.TripASyncTaskCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Successfully deleted");
                mActivity.setResult(Constants.RESULT_CODE_TRIP_DELETED);
                mActivity.finish();
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Couldn\'t delete the trip. Error: " + error);
                Snackbar snackbar = Snackbar.make(
                        mActivity.findViewById(android.R.id.content), error,
                        Snackbar.LENGTH_SHORT);

                snackbar.show();
            }
        });
    }

    public void leaveTrip() {
        Log.d(TAG, "Leaving trip: " + trip.getName());
        ParseTripModel.removeUserFromRelation(trip.getId(), ParseUser.getCurrentUser(),
                Constants.PARSE_RELATION_INVITEES, new ParseTripModel.TripASyncTaskCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Successfully Left trip");
                        mActivity.setResult(Constants.RESULT_CODE_TRIP_LEFT);
                        mActivity.finish();
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.d(TAG, "Couldn\'t leave the trip. Error: " + error);
                        Snackbar snackbar = Snackbar.make(
                                mActivity.findViewById(android.R.id.content), error,
                                Snackbar.LENGTH_SHORT);

                        snackbar.show();
                    }
                });
    }

    public void setMembers(final RecyclerView recyclerView) {
        recyclerView.setAdapter(mMemAdapter);

        if (trip.membersAreSet) {
            updateMembersAdapter();
        } else {
            ParseTripModel.setAllMembers(trip, new ParseTripModel.TripASyncTaskCallback() {
                @Override
                public void onSuccess() {
                    updateMembersAdapter();
                }

                @Override
                public void onFailure(String error) {}
            });
        }
    }

    private void updateMembersAdapter() {
        ArrayList<FacebookUser> fbUsers = new ArrayList<>();
        ArrayList<Trip.TripMember> members = trip.getAllParticipants();
        String currentUserId = ParseUser.getCurrentUser().getObjectId();

        for (Trip.TripMember member : members) {
            // Shouldn't show the option to remove myself
            if (isCreator() && member.objectId.equals(currentUserId)) {
                continue;
            }
            fbUsers.add(new FacebookUser(member.fbId, member.name,
                    String.format(Constants.FB_PICTURE_URL, member.fbId, "normal")));
        }

        mMemAdapter.updateResults(fbUsers);
    }

    public void kickMember(final FacebookUser fbUser) {
        ArrayList<Trip.TripMember> tripMembers = trip.getAllParticipants();
        String userId = "";
        for (Trip.TripMember member : tripMembers) {
            if (member.fbId.endsWith(fbUser.id)) {
                userId = member.objectId;
                break;
            }
        }

        // Remove user from member
        ParseTripModel.removeUserFromRelation(trip.getId(), userId, Constants.PARSE_RELATION_MEMBERS,
                new ParseTripModel.TripASyncTaskCallback() {
                    @Override
                    public void onSuccess() {
                        mMemAdapter.removeFriend(fbUser);
                        Snackbar snackbar = Snackbar.make(mActivity.findViewById(android.R.id.content),
                                R.string.snackbar_member_removed, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }

                    @Override
                    public void onFailure(String error) {
                        Snackbar snackbar = Snackbar.make(mActivity.findViewById(android.R.id.content),
                                error, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });
    }
}
