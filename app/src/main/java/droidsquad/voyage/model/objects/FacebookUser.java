package droidsquad.voyage.model.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.net.URL;

import droidsquad.voyage.R;

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
     * Loads this user's profile picture into the ImageView
     *
     * @param context
     * @param imageView
     */
    public void loadProfilePicInto(Context context, ImageView imageView) {
        Glide.with(context)
                .load(pictureURL)
                .asBitmap()
                .placeholder(R.drawable.ic_account_circle_gray)
                .into(imageView);
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