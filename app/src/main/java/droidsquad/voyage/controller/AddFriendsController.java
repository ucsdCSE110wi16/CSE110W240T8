package droidsquad.voyage.controller;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import droidsquad.voyage.activity.AddFriendsActivity;
import droidsquad.voyage.model.FBFriendsAdapter;
import droidsquad.voyage.model.FacebookAPI;
import droidsquad.voyage.model.FacebookUser;
import droidsquad.voyage.model.SelectedFBFriendsAdapter;

public class AddFriendsController {
    private AddFriendsActivity mActivity;
    private boolean isFriendsPopulated;
    private FacebookUser[] friends;
    private String query;
    private FBFriendsAdapter mResultsAdapter;
    private SelectedFBFriendsAdapter mSelectedFriendsAdapter;

    public static final String TAG = AddFriendsController.class.getSimpleName();

    public AddFriendsController(AddFriendsActivity activity, FBFriendsAdapter resultsAdapter,
                                SelectedFBFriendsAdapter selectedFriendsAdapter) {
        mActivity = activity;
        mResultsAdapter = resultsAdapter;
        mResultsAdapter.setOnClickListener(new FBFriendsAdapter.OnClickListener() {
            @Override
            public void onClick(FacebookUser user) {
                mSelectedFriendsAdapter.addFriend(user);
                updateAdapter(query);
            }
        });

        mSelectedFriendsAdapter = selectedFriendsAdapter;
        mSelectedFriendsAdapter.setOnItemRemovedListener(new SelectedFBFriendsAdapter.OnItemRemovedListener() {
            @Override
            public void onRemoved() {
                updateAdapter(query);
            }
        });

        isFriendsPopulated = false;
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
            public void onCompleted(FacebookUser[] queriedFriends) {
                Log.d(TAG, "Friends Arrays received with size: " + queriedFriends.length);

                // Sort the entries alphabetically by name and store in a member variable
                Arrays.sort(queriedFriends, new Comparator<FacebookUser>() {
                    @Override
                    public int compare(FacebookUser lhs, FacebookUser rhs) {
                        return lhs.name.compareTo(rhs.name);
                    }
                });

                friends = queriedFriends;
                isFriendsPopulated = true;
                if (query != null) updateAdapter(query);
            }
        });
    }

    /**
     * If friends haven't yet been loaded save the query for later, else display the results
     *
     * @param queryString The string the user typed in the search box
     */
    public void onQueryTextChange(String queryString) {
        query = queryString;

        if (isFriendsPopulated) {
            updateAdapter(queryString);
        }
    }

    /**
     * TODO: Optimize the search indexing algorithm
     *
     * @param queryString The query string to search for friends
     */
    private void updateAdapter(String queryString) {
        // Get the friends according to the query
        ArrayList<FacebookUser> queriedFriends = new ArrayList<>();
        if (!queryString.isEmpty()) {
            for (FacebookUser friend : friends) {
                if (friend.name.toLowerCase().contains(queryString.toLowerCase())
                        && !mSelectedFriendsAdapter.mSelectedUsers.contains(friend)) {
                    queriedFriends.add(friend);
                }
            }
        }

        mResultsAdapter.updateResults(queriedFriends);
    }

    private void addFriendsToTrip() {
        // TODO: Add user in the pending array of the trip and send notification
    }

}
