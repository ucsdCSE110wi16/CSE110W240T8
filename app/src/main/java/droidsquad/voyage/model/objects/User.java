package droidsquad.voyage.model.objects;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.Serializable;

import droidsquad.voyage.R;
import droidsquad.voyage.model.api.FacebookAPI;

/**
 * This class represents a Facebook User. It contains member variables for storing
 * information retrieved from Facebook Graph API
 */
public class User implements Serializable {
    public String firstName;
    public String lastName;
    public String id;
    public String fbId;
    public String gender;
    public String pictureURL;

    public User() {
        // No args constructor
    }

    public User(String fbId, String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.fbId = fbId;
        this.pictureURL = FacebookAPI.buildProfilePicURL(fbId, "normal");
    }

    public User(String id, String fbId, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fbId = fbId;
        this.pictureURL = FacebookAPI.buildProfilePicURL(fbId, "normal");
    }

    /**
     * Loads this user's profile picture into the ImageView
     *
     * @param context   Any context
     * @param imageView The imageView to load the picture into
     */
    public void loadProfilePicInto(Context context, ImageView imageView) {
        Glide.with(context)
                .load(pictureURL)
                .asBitmap()
                .placeholder(R.drawable.ic_account_circle_gray)
                .into(imageView);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof User && id.equals(((User) o).id);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}