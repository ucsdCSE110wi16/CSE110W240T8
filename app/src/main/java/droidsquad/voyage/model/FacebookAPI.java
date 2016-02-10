package droidsquad.voyage.model;

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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class contains static methods for retrieving data asynchronously from Facebook
 * through Facebook Graph API.
 */
public class FacebookAPI {
    private static final String GRAPH_URL = "https://graph.facebook.com/";
    private static final String TAG = FacebookAPI.class.getSimpleName();

    /**
     * Get the facebook friends of the current user asynchronously
     *
     * @param callback Called with an array containing all the friends as FacebookUser
     */
    public static void requestFBFriends(final FBFriendsArrayCallback callback) {
        GraphRequest request = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray objects, GraphResponse response) {
                        Log.d(TAG, "Objects received: " + objects.toString());
                        FacebookUser friends[] = new FacebookUser[objects.length()];

                        // Parse the JSONObjects into FacebookUser objects
                        try {
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
                            Log.d(TAG, "Exception occurred while parsing friends JSON response");
                            e.printStackTrace();
                        }

                        // Call the callback with the results
                        callback.onCompleted(friends);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture.type(square){url}");
        request.setParameters(parameters);
        request.executeAsync();
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
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bitmap = null;

                try {
                    URL imageURL = new URL(GRAPH_URL + id + "/picture?type=" + type);
                    bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

//                    HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
//                    connection.setDoInput(true);
//                    connection.setInstanceFollowRedirects(true);
//                    connection.connect();
//                    InputStream inputStream = connection.getInputStream();
//                    //img_value.openConnection().setInstanceFollowRedirects(true).getInputStream()
//                    bitmap = BitmapFactory.decodeStream(inputStream);

                } catch (MalformedURLException e) {
                    Log.d(TAG, "Malformed Exception occurred: " + e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG, "IOException occurred: " + e.getMessage());
                }

                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null)
                    imageView.setImageBitmap(bitmap);
            }
        }.execute();
    }

    public interface FBFriendsArrayCallback {
        void onCompleted(FacebookUser[] friends);
    }
}