package droidsquad.voyage.model;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import droidsquad.voyage.model.objects.Request;
import droidsquad.voyage.util.Constants;

public class ParseRequestModel {
    public static final String TAG = ParseRequestModel.class.getSimpleName();

    /**
     * Get all the trip requests sent to the current user
     *
     * @param callback Called with the list of Request objects
     */
    public static void fetchRequests(final OnRequestsRetrievedCallback callback) {
        // Get all trips for which the user has been invited
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Trip");
        query.whereEqualTo("invitees", ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> trips, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "ParseException occurred. Code = " + e.getCode() +
                            " message = " + e.getMessage());

                    callback.onFailure(e.getMessage());
                    return;
                }

                getRequestsFromTrips(trips, callback);
            }
        });

    }

    /**
     * Helper method to turn ParseTrip objects into Request objects
     *
     * @param trips The parse trips objects to be turned into Request objects
     * @param callback Called with a list of the parsed Request objects
     */
    private static void getRequestsFromTrips(List<ParseObject> trips,
                                        final OnRequestsRetrievedCallback callback) {
        List<String> admins = new ArrayList<>();
        final List<Request> requests = new ArrayList<>();

        // Turn each ParseObject into a RequestObject for passing to the adapter
        for (ParseObject trip : trips) {
            Request request = new Request();
            request.tripName = (String) trip.get("name");
            request.hostId = (String) trip.get("creatorId");

            admins.add((String) trip.get("creatorId"));
            requests.add(request);
        }

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("objectId", admins);
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "ParseException occurred. Code = " + e.getCode() +
                            " message = " + e.getMessage());

                    callback.onFailure(e.getMessage());
                    return;
                }

                for (ParseUser user : users) {
                    for (Request request : requests) {
                        if (user.getObjectId().equals(request.hostId)) {
                            request.hostName = (String) user.get("firstName") +
                                    user.get("lastName");

                            request.hostPicURL = String.format(
                                    Constants.FB_PICTURE_URL, user.get("fbId"), "square");
                        }
                    }
                }

                callback.onSuccess(requests);
            }
        });
    }

    public interface OnRequestsRetrievedCallback {
        void onSuccess(List<Request> requests);
        void onFailure(String error);
    }
}
