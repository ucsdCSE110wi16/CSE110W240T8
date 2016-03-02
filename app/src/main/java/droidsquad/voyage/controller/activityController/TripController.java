package droidsquad.voyage.controller.activityController;

import android.support.design.widget.Snackbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import droidsquad.voyage.R;
import droidsquad.voyage.model.adapters.FBFriendsAdapter;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.model.objects.User;
import droidsquad.voyage.model.objects.VoyageUser;
import droidsquad.voyage.model.parseModels.ParseTripModel;
import droidsquad.voyage.util.Constants;
import droidsquad.voyage.view.activity.TripActivity;

public class TripController {
    private static final String TAG = TripController.class.getSimpleName();

    private TripActivity mActivity;
    public FBFriendsAdapter mMemAdapter;
    public FBFriendsAdapter mInviteesAdapter;
    public Trip trip;

    public TripController(TripActivity instance) {
        this.mActivity = instance;

        trip = mActivity.getIntent().getParcelableExtra(
                mActivity.getString(R.string.intent_key_trip));

        mMemAdapter = new FBFriendsAdapter(mActivity, isCreator());
        mInviteesAdapter = new FBFriendsAdapter(mActivity, isCreator());
    }

    public void deleteTrip() {
        Log.d(TAG, "Deleting trip: " + trip.getName());
        ParseTripModel.deleteTrip(trip.getId(), new ParseTripModel.ParseResponseCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Trip Successfully deleted");
                mActivity.setResult(Constants.RESULT_CODE_TRIP_DELETED);
                mActivity.finish();
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Couldn\'t delete the trip. Error: " + error);
                Snackbar.make(mActivity.findViewById(android.R.id.content), error,
                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void leaveTrip() {
        Log.d(TAG, "Leaving trip: " + trip.getName());
        ParseTripModel.removeUserFromTrip(trip.getId(), VoyageUser.getId(), new ParseTripModel.ParseResponseCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Successfully Left trip");
                mActivity.setResult(Constants.RESULT_CODE_TRIP_LEFT);
                mActivity.finish();
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Couldn\'t leave the trip. Error: " + error);
                Snackbar.make(mActivity.findViewById(android.R.id.content), error,
                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void editTrip() {
        mActivity.editTripIntent(this.trip);
    }



    public void updateMembersAdapter() {
        updateFBFriendsAdapter(trip.getMembersAsUsers(), mMemAdapter);
    }

    public void updateInviteesAdapter() {
        updateFBFriendsAdapter(trip.getInviteesAsUsers(), mInviteesAdapter);
    }

    private void updateFBFriendsAdapter(List<User> users, FBFriendsAdapter adapter) {
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
        kickUser(user, trip.getMembersAsUsers(), mMemAdapter,
                mActivity.getString(R.string.snackbar_member_removed));
    }

    public void kickInvitee(final User user) {
        kickUser(user, trip.getInviteesAsUsers(), mInviteesAdapter,
                mActivity.getString(R.string.snackbar_invitee_removed));
    }

    private void kickUser(final User user, final List<User> users,
                         final FBFriendsAdapter adapter, final String snackBarMessage) {
        String userId = "";
        for (User member : users) {
            if (member.fbId.endsWith(user.id)) {
                userId = member.id;
                break;
            }
        }

        // Remove user from member
        ParseTripModel.removeUserFromTrip(trip.getId(), userId, new ParseTripModel.ParseResponseCallback() {
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

                Snackbar.make(mActivity.findViewById(android.R.id.content),
                        snackBarMessage, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Snackbar.make(mActivity.findViewById(android.R.id.content),
                        error, Snackbar.LENGTH_SHORT).show();
            }
        });
    }


    public boolean isCreator() {
        return trip.getCreatorId().equals(VoyageUser.getId());
    }

    public CharSequence getTitle() {
        return trip.getName();
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
}
