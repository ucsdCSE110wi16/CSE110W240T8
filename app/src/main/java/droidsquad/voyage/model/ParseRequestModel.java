package droidsquad.voyage.model;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import droidsquad.voyage.model.api.FacebookAPI;
import droidsquad.voyage.model.objects.Request;
import droidsquad.voyage.util.Constants;

public class ParseRequestModel {
    public static final String TAG = ParseRequestModel.class.getSimpleName();

    /**
     * Get all the trip requests sent to the current user
     *
     * @param callback Called with the list of Request objects
     */
    public static void fetchRequests(final OnRequestsReceivedCallback callback) {
        // Get all trips for which the user has been invited
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Trip");
        query.whereEqualTo("invitees", ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> trips, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "ParseException occurred. Code = " + e.getCode() +
                            " message = " + e.getMessage());
                    callback.onFailure(getParseErrorString(e.getCode()));
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
                                        final OnRequestsReceivedCallback callback) {
        List<String> admins = new ArrayList<>();
        final List<Request> requests = new ArrayList<>();

        // Turn each ParseObject into a RequestObject for passing to the adapter
        for (ParseObject trip : trips) {
            Request request = new Request();
            request.tripName = (String) trip.get("name");
            request.hostId = (String) trip.get("creatorId");
            request.tripId = trip.getObjectId();

            admins.add((String) trip.get("creatorId"));
            requests.add(request);
        }
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereContainedIn("objectId", admins);
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "ParseException occurred. Code = " + e.getCode() +
                            " message = " + e.getMessage());

                    callback.onFailure(getParseErrorString(e.getCode()));
                    return;
                }

                for (ParseUser user : users) {
                    for (Request request : requests) {
                        if (user.getObjectId().equals(request.hostId)) {
                            request.hostName = user.get("firstName") + " " +
                                    user.get("lastName");

                            request.hostPicURL = FacebookAPI.buildProfilePicURL(
                                    user.getString("fbId"), "normal");
                        }
                    }
                }

                callback.onSuccess(requests);
            }
        });
    }

    public static void acceptRequest(final String tripId, final OnResultCallback callback) {
        ParseTripModel.removeUserFromRelation(tripId, ParseUser.getCurrentUser(),
                Constants.PARSE_RELATION_INVITEES, new ParseTripModel.TripASyncTaskCallback() {
                    @Override
                    public void onSuccess() {
                        addToMem(tripId, callback);
                    }

                    @Override
                    public void onFailure(String error) {
                        callback.onFailure(error);
                    }
                });
    }

    private static void addToMem(String tripId, final OnResultCallback callback) {
        ParseTripModel.addUserToRelation(tripId, ParseUser.getCurrentUser(),
                Constants.PARSE_RELATION_MEMBERS, new ParseTripModel.TripASyncTaskCallback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                    }

                    @Override
                    public void onFailure(String error) {
                        callback.onFailure(error);
                    }
                });
    }

    public static void declineRequest(String tripId, final OnResultCallback callback) {
        ParseTripModel.removeUserFromRelation(tripId, ParseUser.getCurrentUser(),
                Constants.PARSE_RELATION_INVITEES, new ParseTripModel.TripASyncTaskCallback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                    }

                    @Override
                    public void onFailure(String error) {
                        callback.onFailure(error);
                    }
                });
    }


    private static String getParseErrorString(int code) {
        switch (code) {
            case 100:
                return Constants.ERROR_NO_INTERNET_CONNECTION;
            default:
                return Constants.ERROR_UNKNOWN;
        }
    }

    public interface OnRequestsReceivedCallback {
        void onSuccess(List<Request> requests);
        void onFailure(String error);
    }

    public interface OnResultCallback {
        void onSuccess();
        void onFailure(String error);
    }
}
