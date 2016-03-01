package droidsquad.voyage.model.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import droidsquad.voyage.model.objects.User;

/**
 * This class contains static methods for retrieving data asynchronously from Facebook
 * through Facebook Graph API.
 */
public class FacebookAPI {
    private static final String TAG = FacebookAPI.class.getSimpleName();
    public static final String PICTURE_URL_FORMAT = "https://graph.facebook.com/%s/picture?type=%s";

    /**
     * Get the latest information about the currently logged in user on facebook
     *
     * @param callback Called with the information on success
     */
    public static void requestFBInfo(final FBUserInfoCallback callback) {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d(TAG, "Info received from FB: " + object.toString());

                        try {
                            User user = new User();
                            user.firstName = object.getString("first_name");
                            user.lastName = object.getString("last_name");
                            user.gender = object.getString("gender");
                            user.fbId = object.getString("id");

                            callback.onCompleted(user);
                        } catch (JSONException e) {
                            Log.d(TAG, "JSONException occurred: " + e.getMessage());
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,gender,id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    /**
     * Get the facebook friends of the current user asynchronously
     *
     * @param callback Called with an array containing all the friends as User
     */
    public static void requestFBFriends(final FBFriendsArrayCallback callback) {
        GraphRequest request = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray objects, GraphResponse response) {
                        List<User> friends = new ArrayList<>();

                        // Parse the JSONObjects into User objects
                        try {
                            for (int i = 0; i < objects.length(); i++) {
                                JSONObject friend = objects.getJSONObject(i);
                                User user = new User();
                                user.firstName = friend.getString("first_name");
                                user.lastName = friend.getString("last_name");
                                user.gender = friend.getString("gender");
                                user.fbId = friend.getString("id");
                                friends.add(user);
                            }
                        } catch (JSONException e) {
                            Log.d(TAG, "Exception occurred while parsing friends JSON response", e);
                        }

                        // Call the callback with the results
                        callback.onCompleted(friends);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture.type(normal){url}");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public static Bitmap getProfilePic(String id, String type) {
        Bitmap bitmap = null;

        try {
            URL imageURL = new URL(buildProfilePicURL(id, type));
            bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            Log.d(TAG, "Malformed Exception occurred: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException occurred: " + e.getMessage());
        }

        return bitmap;
    }

    public static void getProfilePicAsync(final String id,
                                          final String type, final ProfilePicCallback callback) {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                return getProfilePic(id, type);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null)
                    callback.onCompleted(bitmap);
            }
        }.execute();
    }

    /**
     * Loads the facebook profile picture of the user with the given id into the given view
     *
     * @param imageView The ImageView to load the picture into
     * @param id User id to get the picture from
     * @param type Type of the picture to get from facebook {small, large, square...}
     */
    public static void loadProfilePicIntoView(final ImageView imageView,
                                              final String id, final String type) {
        getProfilePicAsync(id, type, new ProfilePicCallback() {
            @Override
            public void onCompleted(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    public static String buildProfilePicURL(String id, String type) {
        return String.format(PICTURE_URL_FORMAT, id, type);
    }

    /**
     * Interfaces for callback
     */

    public interface FBUserInfoCallback {
        void onCompleted(User user);
    }

    public interface FBFriendsArrayCallback {
        void onCompleted(List<User> friends);
    }

    public interface ProfilePicCallback {
        void onCompleted(Bitmap bitmap);
    }
}
