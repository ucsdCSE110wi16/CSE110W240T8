package droidsquad.voyage.controller.activityController;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import droidsquad.voyage.R;
import droidsquad.voyage.model.ParseTripModel;
import droidsquad.voyage.model.adapters.FBFriendsAdapter;
import droidsquad.voyage.model.adapters.SelectedFBFriendsAdapter;
import droidsquad.voyage.model.api.FacebookAPI;
import droidsquad.voyage.model.objects.FacebookUser;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.util.Constants;
import droidsquad.voyage.view.activity.AddFriendsActivity;

public class AddFriendsController {
    private AddFriendsActivity mActivity;
    private FBFriendsAdapter mResultsAdapter;
    private SelectedFBFriendsAdapter mSelectedFriendsAdapter;
    private List<FacebookUser> friends;
    private Trip mTrip;

    public static final String TAG = AddFriendsController.class.getSimpleName();

    public AddFriendsController(AddFriendsActivity activity) {
        mActivity = activity;

        mTrip = activity.getIntent().getParcelableExtra(
                activity.getString(R.string.intent_key_trip));

        mResultsAdapter = new FBFriendsAdapter(activity, false);
        mSelectedFriendsAdapter = new SelectedFBFriendsAdapter(activity);

        mResultsAdapter.setOnClickListener(new FBFriendsAdapter.OnClickListener() {
            @Override
            public void onClick(FacebookUser user) {
                mSelectedFriendsAdapter.addFriend(user);
                updateAdapter(mActivity.getQuery());
            }
        });

        mSelectedFriendsAdapter.setOnItemRemovedListener(new SelectedFBFriendsAdapter.OnItemRemovedListener() {
            @Override
            public void onRemoved() {
                updateAdapter(mActivity.getQuery());
            }
        });

        getFBFriends();
    }

    /**
     * Get all the facebook friends of the current user and store
     * it in the member variable friends
     *
     * Fields retrieved are ID, Name and Picture
     */
    private void getFBFriends() {
        FacebookAPI.requestFBFriends(new FacebookAPI.FBFriendsArrayCallback() {
            @Override
            public void onCompleted(List<FacebookUser> queriedFriends) {
                Log.d(TAG, "Friends Arrays received with size: " + queriedFriends.size());

                // Sort the entries alphabetically by name and store in a member variable
                Collections.sort(queriedFriends, new Comparator<FacebookUser>() {
                    @Override
                    public int compare(FacebookUser lhs, FacebookUser rhs) {
                        return lhs.name.compareTo(rhs.name);
                    }
                });

                // Get rid of current members and invitees
                List<Trip.TripMember> allMembersAndInvitees = new ArrayList<>();
                allMembersAndInvitees.addAll(mTrip.getAllMembers());
                allMembersAndInvitees.addAll(mTrip.getAllInvitees());
                for (Trip.TripMember member : allMembersAndInvitees) {
                    for (int i = 0; i < queriedFriends.size(); i++) {
                        if (queriedFriends.get(i).id.equals(member.fbId)) {
                            queriedFriends.remove(i);
                        }
                    }
                }

                friends = queriedFriends;
                updateAdapter(mActivity.getQuery());
            }
        });
    }

    /**
     * If friends haven't yet been loaded save the query for later, else display the results
     *
     * @param query The string the user typed in the search box
     */
    public void onQueryTextChange(String query) {
        updateAdapter(query);
    }

    /**
     * TODO: Optimize the search indexing algorithm
     *
     * @param query The query string to search for friends
     */
    private void updateAdapter(String query) {
        // Get the friends according to the query
        ArrayList<FacebookUser> queriedFriends = new ArrayList<>();

        if (!query.isEmpty()) {
            for (FacebookUser friend : friends) {
                if (friend.name.toLowerCase().contains(query.toLowerCase())
                        && !mSelectedFriendsAdapter.mSelectedUsers.contains(friend)) {
                    queriedFriends.add(friend);
                }
            }
        }

        mResultsAdapter.updateResults(queriedFriends);
    }

    public void addFriendsToTrip() {
        Log.d(TAG, "Adding " + mSelectedFriendsAdapter.mSelectedUsers.size() + " friends to Trip.");
        mActivity.showProgress(true);
        final ArrayList<String> fbIDs = new ArrayList<>();
        for (FacebookUser user : mSelectedFriendsAdapter.mSelectedUsers) {
            fbIDs.add(user.id);
        }

        // No invitees to add
        if (fbIDs.isEmpty()) {
            mActivity.finish();
        }

        ParseTripModel.saveInvitees(mTrip, fbIDs, new ParseTripModel.TripASyncTaskCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Successfully added " + fbIDs.size() + " friends");
                Intent intent = new Intent();
                intent.putExtra(mActivity.getString(R.string.intent_key_trip), mTrip);
                mActivity.setResult(Constants.RESULT_CODE_INVITEES_ADDED, intent);
                mActivity.finish();
            }

            @Override
            public void onFailure(String error) {
                mActivity.showProgress(false);
                Snackbar snackbar = Snackbar.make(mActivity.findViewById(R.id.selected_friends_card_view),
                        error, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });
    }

    public RecyclerView.Adapter getResultsAdapter() {
        return mResultsAdapter;
    }

    public RecyclerView.Adapter getSelectedFriendsAdapter() {
        return mSelectedFriendsAdapter;
    }
}
