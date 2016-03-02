package droidsquad.voyage.controller.activityController;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import droidsquad.voyage.R;
import droidsquad.voyage.model.parseModels.ParseTripModel;
import droidsquad.voyage.model.adapters.FBFriendsAdapter;
import droidsquad.voyage.model.adapters.SelectedFBFriendsAdapter;
import droidsquad.voyage.model.api.FacebookAPI;
import droidsquad.voyage.model.objects.User;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.util.Constants;
import droidsquad.voyage.view.activity.AddFriendsActivity;

public class AddFriendsController {
    private AddFriendsActivity mActivity;
    private FBFriendsAdapter mResultsAdapter;
    private SelectedFBFriendsAdapter mSelectedFriendsAdapter;
    private List<User> friends;
    private Trip mTrip;

    public static final String TAG = AddFriendsController.class.getSimpleName();

    public AddFriendsController(AddFriendsActivity activity) {
        mActivity = activity;
        mTrip = activity.getIntent().getParcelableExtra(activity.getString(R.string.intent_key_trip));
        mResultsAdapter = new FBFriendsAdapter(activity, false);
        mSelectedFriendsAdapter = new SelectedFBFriendsAdapter(activity);

        mResultsAdapter.setOnClickListener(new FBFriendsAdapter.OnClickListener() {
            @Override
            public void onClick(User user) {
                mSelectedFriendsAdapter.addFriend(user);
                updateAdapter(mActivity.getQuery());
            }
        });

        mSelectedFriendsAdapter.setOnItemRemovedListener(
                new SelectedFBFriendsAdapter.OnItemRemovedListener() {
                    @Override
                    public void onRemoved() {
                        updateAdapter(mActivity.getQuery());
                    }
                });

        loadFBFriends();
    }

    /**
     * Load all the facebook friends of the current user in the member variable friends
     * Fields retrieved are ID, Name and Picture
     */
    private void loadFBFriends() {
        FacebookAPI.requestFBFriends(new FacebookAPI.FBFriendsArrayCallback() {
            @Override
            public void onCompleted(List<User> queriedFriends) {
                Log.d(TAG, "Friends Array received with size: " + queriedFriends.size());

                // Sort the entries alphabetically and store in the member variable
                Collections.sort(queriedFriends, new Comparator<User>() {
                    @Override
                    public int compare(User lhs, User rhs) {
                        return lhs.getFullName().compareTo(rhs.getFullName());
                    }
                });

                // Get rid of current members and invitees
                HashSet<String> allMembersAndInviteesFbIds = new HashSet<>();

                for (User members : mTrip.getMembersAsUsers()) {
                    allMembersAndInviteesFbIds.add(members.fbId);
                }

                for (User invitees : mTrip.getInviteesAsUsers()) {
                    allMembersAndInviteesFbIds.add(invitees.fbId);
                }

                for (int i = 0; i < queriedFriends.size(); i++) {
                    if (allMembersAndInviteesFbIds.contains(queriedFriends.get(i).fbId)) {
                        queriedFriends.remove(i);
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
        query = query.toLowerCase();
        ArrayList<User> queriedFriends = new ArrayList<>();

        if (!query.isEmpty()) {
            for (User friend : friends) {
                if (friend.getFullName().toLowerCase().contains(query)
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

        final List<User> invitees = mSelectedFriendsAdapter.mSelectedUsers;

        // No invitees to add
        if (invitees.isEmpty()) {
            mActivity.finish();
        }

        ParseTripModel.saveInvitees(mTrip, invitees, new ParseTripModel.ParseResponseCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Successfully added " + invitees.size() + " friends");
                Intent intent = new Intent();
                intent.putExtra(mActivity.getString(R.string.intent_key_trip), mTrip);
                mActivity.setResult(Constants.RESULT_CODE_INVITEES_ADDED, intent);
                mActivity.finish();
            }

            @Override
            public void onFailure(String error) {
                mActivity.showProgress(false);
                Snackbar.make(mActivity.findViewById(R.id.selected_friends_card_view),
                        error, Snackbar.LENGTH_SHORT).show();
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
