package droidsquad.voyage.model.parseModels;

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

import droidsquad.voyage.model.objects.VoyageUser;
import droidsquad.voyage.util.Constants;

public class ParseNotificationModel extends ParseModel {
    private final static String TAG = ParseNotificationModel.class.getSimpleName();

    protected interface Field {
        String TITLE = "title";
        String ALERT = "alert";
        String FB_ID = "fbId";
        String TRIP_ID = "tripId";
        String TYPE = "type";
    }

    /**
     * Send a request notification to each of the parseUsers in the list
     *
     * @param parseTrip Trip from which the request originated
     * @param parseUsers Users to send the notification to
     */
    public static void sendRequestNotifications(ParseObject parseTrip, List<ParseUser> parseUsers) {
        List<String> ids = new ArrayList<>();

        for (ParseUser user : parseUsers) {
            ids.add(user.getObjectId());
        }

        ParseUser currentUser = ParseUser.getCurrentUser();
        JSONObject data = new JSONObject();

        try {
            data.put(Field.TITLE, VoyageUser.getFullName());
            data.put(Field.ALERT, "invited you to join " + parseTrip.get("name"));
            data.put(Field.FB_ID, currentUser.get("fbId"));
            data.put(Field.TRIP_ID, parseTrip.getObjectId());
            data.put(Field.TYPE, Constants.NOTIFICATION_INVITATION);
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
                } else {
                    Log.d(TAG, "ParseException occurred while sending a request notification", e);
                }
            }
        });
    }
}
