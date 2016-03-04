package droidsquad.voyage.model.parseModels;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SendCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import droidsquad.voyage.model.objects.VoyageUser;
import droidsquad.voyage.util.Constants;

public class ParseNotificationModel extends ParseModel {
    private final static String TAG = ParseNotificationModel.class.getSimpleName();

    public interface Field {
        String TITLE = "title";
        String ALERT = "alert";
        String SENDER_ID = "senderId";
        String SENDER_PROFILE_PIC = "senderProfilePic";
        String TRIP_ID = "tripId";
        String TYPE = "type";
    }

    /**
     * Send a request notification to each of the parseUsers in the list
     *  @param parseTrip Trip from which the request originated
     * @param parseMembers Users to send the notification to
     */
    public static void sendRequestNotifications(ParseObject parseTrip, List<ParseObject> parseMembers) {
        List<String> ids = new ArrayList<>();

        for (ParseObject member : parseMembers) {
            ids.add(member.getParseUser(ParseMemberModel.Field.USER).getObjectId());
        }

        JSONObject data = new JSONObject();
        try {
            data.put(Field.TITLE, VoyageUser.getFullName());
            data.put(Field.ALERT, "invited you to join " + parseTrip.get("name"));
            data.put(Field.SENDER_ID, VoyageUser.getId());
            data.put(Field.SENDER_PROFILE_PIC, VoyageUser.getProfilePicURL());
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
