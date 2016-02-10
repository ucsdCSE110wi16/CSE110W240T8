package droidsquad.voyage.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.parse.ParseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FacebookAPI {
    private static final String TAG = FacebookAPI.class.getSimpleName();

    /**
     * Get the facebook friends of the current user asynchronously
     */
    public static void requestFBFriends(GraphRequest.GraphJSONArrayCallback callback) {
        GraphRequest request = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(), callback);
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture.type(square){url}");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public static void getProfilePicAsync(final ImageView imageView, final String id, final String type) {
        AsyncTask<Void, Void, Bitmap> task = new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bitmap=null;
                final String nomimg = "https://graph.facebook.com/"+id+"/picture?type=" + type;
                URL imageURL = null;

                try {
                    imageURL = new URL(nomimg);
                } catch (MalformedURLException e) {
                    Log.d(TAG, "Malformed Exception occurred: " + e.getMessage());
                }

                try {
                    HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
                    connection.setDoInput(true);
                    connection.setInstanceFollowRedirects( true );
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();
                    //img_value.openConnection().setInstanceFollowRedirects(true).getInputStream()
                    bitmap = BitmapFactory.decodeStream(inputStream);

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
        };

        task.execute();
    }
}
