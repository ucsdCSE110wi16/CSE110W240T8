package droidsquad.voyage.controller.activityController;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import droidsquad.voyage.model.objects.User;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.model.objects.VoyageUser;
import droidsquad.voyage.util.Constants;
import droidsquad.voyage.view.activity.TripActivity;

public class TripController {
    private TripActivity mActivity;
    public Trip trip;

    private static final String TAG = TripController.class.getSimpleName();
    public FBFriendsAdapter mMemAdapter;
    public FBFriendsAdapter mInviteesAdapter;

    public TripController(TripActivity instance) {
        this.mActivity = instance;

        trip = mActivity.getIntent().getParcelableExtra(
                mActivity.getString(R.string.intent_key_trip));

        mMemAdapter = new FBFriendsAdapter(mActivity, isCreator());
        mInviteesAdapter = new FBFriendsAdapter(mActivity, isCreator());
    }

    public void setGooglePlacePhoto(final ImageView imageView) {
        try {
            Log.d(TAG, "Attempting to get photo from Google Places");

            String placeID = trip.getDestination().getString("placeId");

            final GooglePlacesAPI googlePlacesAPI = new GooglePlacesAPI(mActivity);
            googlePlacesAPI.getPlaceImage(placeID, imageView.getWidth(), imageView.getHeight(),
                    new ResultCallback<PlacePhotoResult>() {
                        @Override
                        public void onResult(@NonNull PlacePhotoResult placePhotoResult) {
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

    public void editTrip() {
        mActivity.editTripIntent(this.trip);
    }

    public CharSequence getTitle() {
        return trip.getName();
    }

    public boolean isCreator() {
        return trip.getCreatorId().equals(VoyageUser.getId());
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

    public void updateMembersAdapter() {
        updateFBFriendsAdapter(trip.getAllMembers(), mMemAdapter);
    }

    public void updateInviteesAdapter() {
        updateFBFriendsAdapter(trip.getInvitees(), mInviteesAdapter);
    }

    private void updateFBFriendsAdapter(ArrayList<User> users, FBFriendsAdapter adapter) {
        ArrayList<User> friends = new ArrayList<>();

        for (User member : users) {
            // Shouldn't show the option to remove myself
            if (isCreator() && member.equals(VoyageUser.currentUser())) {
                continue;
            }

            friends.add(member);
        }

        adapter.updateResults(friends);
    }


    public void kickMember(final User user) {
        kickUser(user, trip.getAllMembers(), mMemAdapter, Constants.PARSE_RELATION_MEMBERS,
                mActivity.getString(R.string.snackbar_member_removed));
    }

    public void kickInvitee(final User user) {
        kickUser(user, trip.getInvitees(), mInviteesAdapter, Constants.PARSE_RELATION_INVITEES,
                mActivity.getString(R.string.snackbar_invitee_removed));
    }

    public void kickUser(final User user, final ArrayList<User> users, final FBFriendsAdapter adapter,
                         String relation, final String snackBarMessage) {
        String userId = "";
        for (User member : users) {
            if (member.fbId.endsWith(user.id)) {
                userId = member.id;
                break;
            }
        }

        // Remove user from member
        ParseTripModel.removeUserFromRelation(trip.getId(), userId, relation,
                new ParseTripModel.TripASyncTaskCallback() {
                    @Override
                    public void onSuccess() {
                        adapter.removeFriend(user);

                        // Successfully remove the user from the trip object now
                        for (int i = 0; i < users.size(); i++) {
                            if (users.get(i).fbId.endsWith(user.id)) {
                                users.remove(i);
                                break;
                            }
                        }

                        Snackbar snackbar = Snackbar.make(mActivity.findViewById(android.R.id.content),
                                snackBarMessage, Snackbar.LENGTH_SHORT);
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

    public String getTransportationStringRepresentation() {
        return trip.getSimpleCitiesStringRepresentation();
    }

    public String getDatesStringRepresentation() {
        return trip.getSimpleDatesStringRepresentation();
    }

    public int getTransportationIcon() {
        return trip.getTransportationIconId();
    }

    public Date getDateFrom() {
        return trip.getDateFrom();
    }

    public Date getDateTo() {
        return trip.getDateTo();
    }
}
