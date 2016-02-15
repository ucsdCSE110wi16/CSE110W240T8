package droidsquad.voyage.model;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.util.Constants;

public class ParseTripModel {
    public static final String USER_OBJECT = "User";
    public static final String TRIP_OBJECT = "Trip";
    private static final String TAG = ParseUser.class.getSimpleName();
    // TODO: add more key/value final Strings

    /**
     * Save this trip to the database
     *
     * @param trip Trip to be saved
     */
    public static void saveTrip(final Trip trip) {
        Log.d(TAG, "Attempting to save trip to parse.\nTrip: " + trip.toString());

        final ParseObject parseTrip = new ParseObject("Trip");
        parseTrip.put("name", trip.getName());
        parseTrip.put("creatorId", trip.getCreatorId());
        parseTrip.put("origin", trip.getOrigin());
        parseTrip.put("destination", trip.getDestination());
        parseTrip.put("private", trip.isPrivate());
        parseTrip.put("dateFrom", trip.getDateFrom());
        parseTrip.put("dateTo", trip.getDateTo());
        parseTrip.put("transportation", trip.getTransportation());

        //TODO: Initialize UserGroup stuff for new trips?
        ParseRelation<ParseUser> relation = parseTrip.getRelation("members");
        relation.add(ParseUser.getCurrentUser());

        parseTrip.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "SUCCESS");
                    trip.setId(parseTrip.getObjectId());
                } else {
                    Log.d(TAG, "FAILED");
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Gets all the trips from the database that this user is part of
     *
     * @param callback Called with the trips as an argument
     */
    public static void searchForAllTrips(final ParseTripCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Trip");
        query.whereEqualTo("creatorId", ParseUser.getCurrentUser().getObjectId());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                getAllMyTrips(objects, callback);
            }
        });
    }

    /**
     * Helper method for getting all the trips from the database
     *
     * @param trips List to put the trips inside
     * @param callback Called when all trips are retrieved from the database
     */
    private static void getAllMyTrips(List<ParseObject> trips, ParseTripCallback callback) {
        ArrayList<Trip> allMyTrips = new ArrayList<>();
        if (trips == null)
            return;

        for (ParseObject parseTrip : trips) {
            Trip trip = new Trip();

            trip.setId(parseTrip.getObjectId());
            trip.setCreatorId(parseTrip.getString("creatorId"));

            trip.setName(parseTrip.getString("name"));
            trip.setTransportation(parseTrip.getString("transportation"));
            trip.setPrivate(parseTrip.getBoolean("private"));

            trip.setOrigin(parseTrip.getJSONObject("origin"));
            trip.setDestination(parseTrip.getJSONObject("destination"));

            trip.setDateFrom(parseTrip.getDate("dateFrom"));
            trip.setDateTo(parseTrip.getDate("dateTo"));

            getAllMembers(trip);
            allMyTrips.add(trip);
        }

        callback.onCompleted(allMyTrips);
    }

    public static String getUser() {
        return ParseUser.getCurrentUser().getObjectId();
    }

    public static void saveInvitees(String tripObjId, final ArrayList<String> fbIDs, final TripASyncTaskCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Trip");
        query.getInBackground(tripObjId, new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject parseTrip, ParseException e) {
                if (e == null) {
                    getFBUsers(parseTrip, fbIDs, callback);
                } else {
                    Log.d(TAG, "ParseExceptionOccurred. Code: " + e.getCode()
                            + " Message: " + e.getMessage());

                    callback.onFaliure(getParseErrorString(e.getCode()));
                }
            }
        });
    }

    /**
     * Gets the associated FB Users from Parse to set up Invitee Relations
     * @param parseTrip
     * @param fbIDs
     * @param callback
     */
    private static void getFBUsers(final ParseObject parseTrip, ArrayList<String> fbIDs, final TripASyncTaskCallback callback) {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereContainedIn("fbId", fbIDs);
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    setUpInviteeRelations(objects, parseTrip, callback);
                } else {
                    Log.d(TAG, "ParseExceptionOccurred. Code: " + e.getCode()
                            + " Message: " + e.getMessage());
                    callback.onFaliure(getParseErrorString(e.getCode()));
                }
            }
        });
    }

    /**
     * Add members to the Invitee Relation of a trip in Parse
     * @param objects
     * @param parseTrip
     * @param callback
     */
    private static void setUpInviteeRelations(List<ParseUser> objects, ParseObject parseTrip, final TripASyncTaskCallback callback) {
        ParseRelation<ParseUser> relation = parseTrip.getRelation("invitees");
        for (ParseUser user: objects) {
            relation.add(user);
        }
        parseTrip.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    callback.onSuccess();
                } else {
                    Log.d(TAG, "ParseExceptionOccurred. Code: " + e.getCode()
                            + " Message: " + e.getMessage());
                    callback.onFaliure(getParseErrorString(e.getCode()));
                }
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

    /**
     * Gets all current members of a trip from Parse
     * @param trip
     */
    public static void getAllMembers(final Trip trip) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Trip");
        query.whereEqualTo("objectId", trip.getId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for(ParseObject tripObj : objects) {
                    ParseRelation<ParseObject> relation = tripObj.getRelation("members");
                    ParseQuery<ParseObject> query = relation.getQuery();
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            ArrayList<String> members = new ArrayList<String>();
                            for(ParseObject member: objects) {
                                //Log.d(TAG, "Trip User Added: " + (String)member.get("fbId"));
                                members.add((String)member.get("fbId"));
                            }
                            trip.setAllParticipants(members);
                        }
                    });
                }

            }
        });
    }

    /**
     * Interface to implement a callback that is accepted in methods of this class
     */
    public interface ParseTripCallback {
        void onCompleted(ArrayList<Trip> trip);
    }

    public interface TripASyncTaskCallback {
        void onSuccess ();
        void onFaliure(String error);
    }
}
