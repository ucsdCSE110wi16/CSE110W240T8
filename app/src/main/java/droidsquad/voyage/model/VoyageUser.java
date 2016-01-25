package droidsquad.voyage.model;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        parameters.putString("fields", "first_name,last_name,email,birthday,gender");
        request.setParameters(parameters);
        request.executeAsync();

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
}
