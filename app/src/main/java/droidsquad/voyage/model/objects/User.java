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
    public String id;
    public String fbId;
    public String firstName;
    public String lastName;
    public String gender;

    public User() {
        // No args constructor
    }

    public User(String fbId, String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.fbId = fbId;
    }

    public User(String id, String fbId, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fbId = fbId;
    }

    /**
     * Loads this user's profile picture into the ImageView
     *
     * @param context   Any context
     * @param imageView The imageView to load the picture into
     */
    public void loadProfilePicInto(Context context, ImageView imageView) {
        Glide.with(context)
                .load(getPictureURL())
                .asBitmap()
                .placeholder(R.drawable.ic_account_circle_gray)
                .into(imageView);
    }

    @Override
    public boolean equals(Object o) {
        return ((o instanceof User) &&
                (id != null)) ? id.equals(((User) o).id) : fbId.equals(((User) o).fbId);
    }

    @Override
    public String toString() {
        return "User: " + getFullName();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getPictureURL() {
        return FacebookAPI.buildProfilePicURL(fbId, "normal");
    }
}