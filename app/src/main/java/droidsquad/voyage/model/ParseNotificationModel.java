package droidsquad.voyage.model;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SendCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParseNotificationModel {
    private final static String TAG = ParseNotificationModel.class.getSimpleName();

    public static void sendRequestNotifications(ParseObject parseTrip, List<ParseUser> parseUsers) {
        List<String> ids = new ArrayList<>();

        for (ParseUser user : parseUsers) {
            ids.add(user.getObjectId());
        }

        ParseUser currentUser = ParseUser.getCurrentUser();
        JSONObject data = new JSONObject();
        try {
            data.put("title", currentUser.get("firstName") + " " + currentUser.get("lastName"));
            data.put("alert", "invited you to join " + parseTrip.get("name"));
            data.put("fbId", currentUser.get("fbId"));
            data.put("type", "tripInvitation");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ParseQuery<ParseInstallation> parseQuery = ParseInstallation.getQuery();
        parseQuery.whereContainedIn("userId", ids);

        ParsePush push = new ParsePush();
        push.setQuery(parseQuery);
        push.setData(data);
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Invitation successfully sent");
                }
            }
        });
    }
}
