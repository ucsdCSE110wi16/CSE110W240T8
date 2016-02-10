package droidsquad.voyage.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class FBFriendsArrayAdapter extends ArrayAdapter<FBFriendsArrayAdapter.FBFriend> implements Filterable {
    private LayoutInflater inflater;
    private FBFriend friends[];
    private ArrayList<FBFriend> queriedFriends;

    public FBFriendsArrayAdapter(Context context) {
        super(context, 0);
        inflater = LayoutInflater.from(context);

        getFBFriends();

        // Sort the entries alphabetically by name and store in a member variable
        Arrays.sort(friends, new Comparator<FBFriend>() {
            @Override
            public int compare(FBFriend lhs, FBFriend rhs) {
                return lhs.name.compareTo(rhs.name);
            }
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return queriedFriends.size();
    }

    @Override
    public FBFriend getItem(int position) {
        return queriedFriends.get(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // Get the friends that match the constraint
                for (FBFriend friend : friends) {
                    if (friend.name.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        queriedFriends.add(friend);
                    }
                }

                FilterResults results = new FilterResults();
                results.count = queriedFriends.size();
                results.values = queriedFriends;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
    }

    /**
     * Get all the facebook friends of the current user and store
     * it in the member variable friends
     *
     * Fields retrieved are ID, Name and Picture
     */
    private void getFBFriends() {
        GraphRequest request = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray objects, GraphResponse response) {
                        // TODO: Parse the JSONArray into a List<FriendsAutoComplete>
                        friends = new FBFriend[objects.length()];

                        try {
                            for (int i = 0; i < objects.length(); i++) {
                                JSONObject friend = objects.getJSONObject(i);
                                String pictureURL = friend
                                        .getJSONObject("picture")
                                        .getJSONObject("data")
                                        .getString("url");

                                friends[i] = new FBFriend(
                                        (String) friend.get("id"),
                                        (String) friend.get("name"),
                                        pictureURL);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture{url}");
        request.setParameters(parameters);
        request.executeAndWait();
    }

    public class FBFriend {
        public String name;
        public String id;
        public String pictureURL;

        public FBFriend(String id, String name, String pictureURL) {
            this.id = id;
            this.name = name;
            this.pictureURL = pictureURL;
        }

        /**
         * Get the profile facebook profile picture of this user
         *
         * @return A Bitmap of this friend's profile picture
         */
        public Bitmap getPicture() {
            Bitmap bitmap = null;
            try {
                URL url = new URL(pictureURL);
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }
    }
}
