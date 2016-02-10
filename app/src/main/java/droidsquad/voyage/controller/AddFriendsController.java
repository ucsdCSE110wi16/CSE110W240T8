package droidsquad.voyage.controller;

import android.util.Log;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import droidsquad.voyage.activity.AddFriendsActivity;
import droidsquad.voyage.model.FBFriendsAdapter;
import droidsquad.voyage.model.FacebookAPI;
import droidsquad.voyage.model.FacebookUser;

public class AddFriendsController {
    private AddFriendsActivity mActivity;
    private boolean isFriendsPopulated;
    private FacebookUser[] friends;
    private String query;
    private FBFriendsAdapter mAdapter;


    public static final String TAG = AddFriendsController.class.getSimpleName();

    public AddFriendsController(AddFriendsActivity activity, FBFriendsAdapter adapter) {
        mActivity = activity;
        mAdapter = adapter;
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
        FacebookAPI.requestFBFriends(new GraphRequest.GraphJSONArrayCallback() {

            @Override
            public void onCompleted(JSONArray objects, GraphResponse response) {
                // TODO: Parse the JSONArray into a List<FriendsAutoComplete>
                friends = new FacebookUser[objects.length()];

                try {
                    Log.d(TAG, "Objects received: " + objects.toString());

                    for (int i = 0; i < objects.length(); i++) {
                        JSONObject friend = objects.getJSONObject(i);
                        String pictureURL = friend
                                .getJSONObject("picture")
                                .getJSONObject("data")
                                .getString("url");

                        friends[i] = new FacebookUser(
                                (String) friend.get("id"),
                                (String) friend.get("name"),
                                pictureURL);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Sort the entries alphabetically by name and store in a member variable
                Arrays.sort(friends, new Comparator<FacebookUser>() {
                    @Override
                    public int compare(FacebookUser lhs, FacebookUser rhs) {
                        return lhs.name.compareTo(rhs.name);
                    }
                });

                isFriendsPopulated = true;
                if (query != null) {
                    updateAdapter(query);
                }
            }
        });
    }

    public void setQueryChange(String queryString) {
        if (!isFriendsPopulated) {
            query = queryString;
        } else {
            updateAdapter(queryString);
        }
    }


    private void updateAdapter(String queryString) {
        // Get the friends that match the constraint
        ArrayList<FacebookUser> queriedFriends = new ArrayList<>();
        if (!queryString.isEmpty()) {
            for (FacebookUser friend : friends) {
                if (friend.name.toLowerCase().startsWith(queryString.toLowerCase())) {
                    queriedFriends.add(friend);
                }
            }
        }

        mAdapter.updateResults(queriedFriends);
    }
}
