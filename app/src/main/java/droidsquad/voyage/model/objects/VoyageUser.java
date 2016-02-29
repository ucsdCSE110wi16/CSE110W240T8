package droidsquad.voyage.model.objects;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.interceptors.ParseLogInterceptor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import droidsquad.voyage.util.Constants;
import droidsquad.voyage.view.activity.MainNavDrawerActivity;

public class VoyageUser {
    private static final String TAG = VoyageUser.class.getSimpleName();

    public static void refreshInfoFromFB() {
        Log.d(TAG, "Refreshing the user's info from FB.");
        final ParseUser currentUser = ParseUser.getCurrentUser();
        AccessToken token = AccessToken.getCurrentAccessToken();

        GraphRequest request = GraphRequest.newMeRequest(
                token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d(TAG, "Info recevied from FB: " + object.toString());

                        try {
                            // Public profile. These fields are guaranteed to be in the JSON
                            currentUser.put("firstName", object.get("first_name").toString());
                            currentUser.put("lastName", object.get("last_name").toString());
                            currentUser.put("gender", object.get("gender").toString());
                            currentUser.put("fbId", object.get(("id")).toString());

                            Log.d(TAG, "Info saved.");
                        } catch (JSONException e) {
                            Log.d(TAG, "JSONException occurred: " + e.getMessage());
                        }

                        currentUser.saveInBackground();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,gender,id");
        request.setParameters(parameters);
        request.executeAsync();

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
                    }

                    if (err != null && err.getCode() == -1) {
                        Snackbar snackbar = Snackbar.make(view, Constants.ERROR_NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG)
                                .setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        attemptFBLogin(activity, view);
                                    }
                                });
                        snackbar.show();
                    }
                } else {
                    Log.d(TAG, (user.isNew()) ? "Signing up new user." : "User logged in through Facebook!");

                    // Setup installation object for push notifications
                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    installation.put("userId", user.getObjectId());
                    installation.saveInBackground();

                    VoyageUser.refreshInfoFromFB();
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

    public void logOut() {
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
}
