package droidsquad.voyage.controller.activityController;

import android.support.design.widget.Snackbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import droidsquad.voyage.R;
import droidsquad.voyage.model.adapters.TripMembersAdapter;
import droidsquad.voyage.model.objects.Member;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.model.objects.VoyageUser;
import droidsquad.voyage.model.parseModels.ParseTripModel;
import droidsquad.voyage.util.Constants;
import droidsquad.voyage.view.activity.TripActivity;

public class TripController {
    private static final String TAG = TripController.class.getSimpleName();

    private TripActivity mActivity;
    public TripMembersAdapter mMembersAdapter;
    public TripMembersAdapter mInviteesAdapter;
    public Trip trip;

    public TripController(TripActivity instance) {
        this.mActivity = instance;

        trip = mActivity.getIntent().getParcelableExtra(
                mActivity.getString(R.string.intent_key_trip));

        mMembersAdapter = new TripMembersAdapter(mActivity);
        mInviteesAdapter = new TripMembersAdapter(mActivity);
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
        ParseTripModel.removeMemberFromTrip(trip.getId(), trip.getMemberWithUserId(VoyageUser.getId()).id,
                new ParseTripModel.ParseResponseCallback() {
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
        mActivity.startEditTripIntent(trip);
    }

    public void updateMembersAdapter() {
        List<Member> members = new ArrayList<>(trip.getMembers());
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).user.equals(VoyageUser.currentUser())) {
                members.remove(i);
                break;
            }
        }

        mMembersAdapter.updateMembers(members);
    }

    public void updateInviteesAdapter() {
        mInviteesAdapter.updateMembers(trip.getInvitees());
    }

    public void kickMember(final Member member) {
        final TripMembersAdapter adapter = (member.pendingRequest) ? mInviteesAdapter : mMembersAdapter;

        // Remove user from member
        ParseTripModel.removeMemberFromTrip(trip.getId(), member.id, new ParseTripModel.ParseResponseCallback() {
            @Override
            public void onSuccess() {
                adapter.removeMember(member);
                trip.removeMember(member);
                Snackbar.make(mActivity.findViewById(android.R.id.content),
                        (member.pendingRequest ? R.string.snackbar_invitee_removed : R.string.snackbar_member_removed),
                        Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Snackbar.make(mActivity.findViewById(android.R.id.content),
                        error, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isAdmin() {
        return trip.getAdmin().equals(VoyageUser.currentUser());
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
