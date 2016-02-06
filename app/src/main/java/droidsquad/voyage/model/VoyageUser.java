package droidsquad.voyage.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import droidsquad.voyage.R;
import droidsquad.voyage.activity.CreateTripActivity;
import droidsquad.voyage.activity.LoginActivity;
import droidsquad.voyage.activity.TripListActivity;

/**
 * Created by Raghav on 1/24/2016.
 */
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
                    public void onCompleted( JSONObject object, GraphResponse response) {
                        Log.d(TAG, "Info recevied from FB: " + object.toString());

                        try {
                            // Public profile. These fields are guaranteed to be in the JSON
                            currentUser.put("firstName", object.get("first_name").toString());
                            currentUser.put("lastName", object.get("last_name").toString());
                            currentUser.put("gender", object.get("gender").toString());
                            currentUser.put("fbId", object.get(("id")).toString());

                            // Check if the user gave his/her email
                            if (object.has("email")) {
                                // if the user signed up for facebook using his phone number then
                                // this won't be a valid email. But it might be a valid phone number
                                String emailOrMobile = object.get("email").toString();
                                if (isEmailValid(emailOrMobile)) {
                                    currentUser.put("email", emailOrMobile);
                                } else if (isMobileNumValid(emailOrMobile)) {
                                    currentUser.put("mobile", emailOrMobile);
                                }
                            }

                            // Check if the user gave his birthday
                            if (object.has("birthday")) {
                                String birthday = object.get("birthday").toString();
                                // The answer comes in the format "03\/28\/1996"

                                int month = Integer.parseInt(birthday.substring(0, 2));
                                int date = Integer.parseInt(birthday.substring(3, 5));
                                int year = Integer.parseInt(birthday.substring(6));

                                JSONObject dOB = new JSONObject();
                                dOB.put("month", (month - 1));
                                dOB.put("day", date);
                                dOB.put("year", year);

                                currentUser.put("dateOfBirth", dOB.toString());
                            }

                            Log.d(TAG, "Info saved.");
                        } catch (JSONException e) {
                            Log.d(TAG, "JSONException occurred: " + e.getMessage());
                        }

                        currentUser.saveInBackground();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,birthday,gender,id");
        request.setParameters(parameters);
        request.executeAsync();

    }

    public static void attempFBLogin(final Activity activity, final View view) {
        Log.d(TAG, "Logging in user with Facebook.");
        Collection<String> permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("email");
        permissions.add("user_birthday");
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
                        Snackbar snackbar = Snackbar.make(view, R.string.error_no_internet_connection, Snackbar.LENGTH_LONG)
                                .setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        attempFBLogin(activity, view);
                                    }
                                });
                        snackbar.show();
                    }
                } else if (user.isNew()) {
                    Log.d(TAG, "Signing up new user.");
                    VoyageUser.refreshInfoFromFB();
                    Intent intent = new Intent(activity, TripListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                } else {
                    Log.d(TAG, "User logged in through Facebook!");
                    VoyageUser.refreshInfoFromFB();
                    Intent intent = new Intent(activity, TripListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                }
            }
        });
    }

    public static void setProfilePicAsync(final ImageView imageView) {
        AsyncTask<Void, Void, Bitmap> task = new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bitmap=null;
                String id = ParseUser.getCurrentUser().get("fbId").toString();
                final String nomimg = "https://graph.facebook.com/"+id+"/picture?type=large";
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
        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser == null)
            return;

        currentUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if(e == null) {
                    // TODO: maybe add a loading animation
                }
                else {
                    // TODO: handle if logout fails (error message)
                }
            }
        });
    }
}
