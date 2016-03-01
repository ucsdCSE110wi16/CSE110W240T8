package droidsquad.voyage.model.objects;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import droidsquad.voyage.R;
import droidsquad.voyage.model.api.FacebookAPI;
import droidsquad.voyage.util.Constants;
import droidsquad.voyage.view.activity.MainNavDrawerActivity;

/**
 * This class encapsulates everything related to the currently logged in user.
 * It uses the singleton pattern to always have at any moment an instance of hte
 * current user available through a static getter #currentUser
 */
public class VoyageUser {
    private static final String TAG = VoyageUser.class.getSimpleName();
    private volatile static User currentUser;

    private VoyageUser() {}

    public static User currentUser() {
        if (currentUser == null) {
            synchronized (User.class) {
                if (currentUser == null) {
                    currentUser = new User(getId(), getFbId(), getFirstName(), getLastName());
                }
            }
        }
        return currentUser;
    }

    /**
     * Queries Facebook for the latest user info and stores in the ParseUser currentUser
     */
    public static void refreshInfoFromFB() {
        Log.d(TAG, "Refreshing the user's info from FB.");
        FacebookAPI.requestFBInfo(new FacebookAPI.FBUserInfoCallback() {
            @Override
            public void onCompleted(User user) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                currentUser.put("firstName", user.firstName);
                currentUser.put("lastName", user.lastName);
                currentUser.put("gender", user.gender);
                currentUser.put("fbId", user.fbId);
                currentUser.saveInBackground();

                Log.d(TAG, "User's information saved successfully to Parse");
            }
        });
    }

    public static void attemptFBLogin(final Activity activity, final View view) {
        Log.d(TAG, "Logging in user with Facebook.");

        Collection<String> permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("user_friends");

        ParseFacebookUtils.logInWithReadPermissionsInBackground(activity, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, com.parse.ParseException err) {
                if (user == null) {
                    Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");

                    if (err != null) {
                        Log.d(TAG, "ParseException occurred. Code: " + err.getCode()
                                + " Message: " + err.getMessage());

                        if (err.getCode() == -1) {
                            Snackbar.make(view, Constants.ERROR_NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG)
                                    .setAction("RETRY", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            attemptFBLogin(activity, view);
                                        }
                                    }).show();
                        }
                    }
                } else {
                    Log.d(TAG, (user.isNew()) ? "Signing up new user." : "User logged in through Facebook!");

                    // Setup installation object for push notifications
                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    installation.put("userId", user.getObjectId());
                    installation.saveInBackground();

                    refreshInfoFromFB();

                    Intent intent = new Intent(activity, MainNavDrawerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                }
            }
        });
    }

    public static boolean isEmailValid(String email) {
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+"); // matching email with regex
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean isMobileNumValid(String mobileNum) {
        /*
         *   matching phone number with regex
         *   Examples: Matches following phone numbers:
         *   (123)456-7890, 123-456-7890, 1234567890, (123)-456-7890
         */
        Pattern p = Pattern.compile("^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$");
        Matcher m = p.matcher(mobileNum);
        return m.matches();
    }

    public static void logOut() {
        Log.d(TAG, "Logging user out.");

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null)
            return;

        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    // TODO: maybe add a loading animation
                    Log.d(TAG, "User successfully logged out.");
                    ParseInstallation.getCurrentInstallation().remove("userId");
                    ParseInstallation.getCurrentInstallation().saveInBackground();
                } else {
                    // TODO: handle if logout fails (error message)
                    Log.d(TAG, "ParseException occurred. Code: "
                            + e.getCode() + "Message: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Load this user's profile picture into the View provided
     *
     * @param context Just any context
     * @param imageView The imageView to load pic into
     */
    public static void loadProfilePicInto(Context context, ImageView imageView) {
        Log.d(TAG, "Loading profile picture into imageView");
        Glide.with(context)
                .load(getProfilePicURL())
                .asBitmap()
                .placeholder(R.drawable.ic_account_circle_gray)
                .into(imageView);
    }

    public static String getId() {
        return ParseUser.getCurrentUser().getObjectId();
    }

    public static String getFirstName() {
        return ParseUser.getCurrentUser().getString("firstName");
    }

    public static String getLastName() {
        return ParseUser.getCurrentUser().getString("lastName");
    }

    public static String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public static String getFbId() {
        return ParseUser.getCurrentUser().getString("fbId");
    }

    public static String getProfilePicURL() {
        return FacebookAPI.buildProfilePicURL(getId(), "square");
    }

    public static boolean isLoggedIn() {
        return ParseUser.getCurrentUser() != null;
    }
}
