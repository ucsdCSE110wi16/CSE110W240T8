package droidsquad.voyage.model.objects;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import droidsquad.voyage.R;
import droidsquad.voyage.model.api.FacebookAPI;

/**
 * This class represents a Facebook User. It contains member variables for storing
 * information retrieved from Facebook Graph API
 */
public class User implements Parcelable {
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

    protected User(Parcel in) {
        id = in.readString();
        fbId = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        gender = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(fbId);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(gender);
    }
}