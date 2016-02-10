package droidsquad.voyage.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.net.URL;

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
     * @return A Bitmap of this friend's profile picture
     */
    @Deprecated
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