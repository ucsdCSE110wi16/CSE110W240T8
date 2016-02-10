package droidsquad.voyage.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.net.URL;

/**
 * This class represents a Facebook User. It contains member variables for storing
 * information retrieved from Facebook Graph API
 */
public class FacebookUser {
    public String name;
    public String id;
    public String pictureURL;

    public FacebookUser(String id, String name, String pictureURL) {
        this.id = id;
        this.name = name;
        this.pictureURL = pictureURL;
    }

    /**
     * Get the profile facebook profile picture of this user
     *
     * @param callback Called with Bitmap once picture has finished downloading
     * @deprecated Use the FacebookAPI.getProfilePicAsync method instead
     */
    @Deprecated
    public void getPictureAsync(FBUserPictureCallback callback) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(pictureURL);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        callback.onCompleted(bitmap);
    }

    /**
     * Callback ot be used with getPictureAsync method
     */
    public interface FBUserPictureCallback {
        void onCompleted(Bitmap picture);
    }
}