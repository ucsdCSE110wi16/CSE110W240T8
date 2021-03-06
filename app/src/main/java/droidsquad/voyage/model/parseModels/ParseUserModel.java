package droidsquad.voyage.model.parseModels;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import droidsquad.voyage.model.objects.User;

public class ParseUserModel extends ParseModel {
    // Class for providing a namespace for the Fields of the User class in Parse database
    protected interface Field {
        String ID = "objectId";
        String FIRST_NAME = "firstName";
        String LAST_NAME = "lastName";
        String GENDER = "gender";
        String Facebook_ID = "fbId";
    }

    /**
     * Get the User object from the ParseUser object
     *
     * @param parseUser ParseUser to extract the User object from
     * @return The User from ParseUser
     */
    public static User getUserFromParseUser(ParseUser parseUser) {
        try {
            parseUser.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new User(
                parseUser.getObjectId(),
                parseUser.getString(Field.Facebook_ID),
                parseUser.getString(Field.FIRST_NAME),
                parseUser.getString(Field.LAST_NAME));
    }

    public static void getParseUserFromUser(User user, final ParseUserCallback callback) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(user.id, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    callback.onSuccess(parseUser);
                } else {
                    callback.onFailure(e.getMessage());
                }
            }
        });
    }

    public interface ParseUserCallback {
        void onSuccess(ParseUser parseUser);

        void onFailure(String error);
    }
}
