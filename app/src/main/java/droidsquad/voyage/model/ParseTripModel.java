package droidsquad.voyage.model;

import android.util.Log;

import com.parse.DeleteCallback;
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

    /***************************************************************************************
     *                         PUBLIC METHODS (To be used in other files)
     ***************************************************************************************/

    /**
     * Save this trip to the database
     *
     * @param trip Trip to be saved
     */
    public static void saveTrip(final Trip trip) {
        Log.d(TAG, "Attempting to save trip to parse.\nTrip: " + trip.toString());

        final ParseObject parseTrip = getParseObjectFromTrip(trip);

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
                    Log.d(TAG, "FAILED", e);
                }
            }
        });
    }

    public static void updateTrip(final Trip trip) {
        Log.d(TAG, "Attempting to update trip to parse.\nTrip: " + trip.toString());
        String oldTripId = trip.getId();

        getParseTrip(oldTripId, new ParseTripReceivedCallback() {
            @Override
            public void onSuccess(final ParseObject parseTrip) {
                Log.d(TAG, "Trip queried");
                parseTrip.put("name", trip.getName());
                parseTrip.put("creatorId", trip.getCreatorId());
                parseTrip.put("origin", trip.getOrigin());
                parseTrip.put("destination", trip.getDestination());
                parseTrip.put("private", trip.isPrivate());
                parseTrip.put("dateFrom", trip.getDateFrom());
                parseTrip.put("dateTo", trip.getDateTo());
                parseTrip.put("transportation", trip.getTransportation());

                parseTrip.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d(TAG, "SUCCESS");
                            trip.setId(parseTrip.getObjectId());
                        } else {
                            Log.d(TAG, "FAILED", e);
                        }
                    }
                });
            }

            @Override
            public void onFailure(String error) {
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
        query.whereEqualTo("members", ParseUser.getCurrentUser().getObjectId());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                getAllMyTrips(objects, callback);
            }
        });
    }

    public static void searchAllPublicTrips(final ParseTripCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Trip");
        query.whereEqualTo("private", false);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                getAllMyTrips(objects, callback);
            }
        });
    }

    /**
     * Save the invitees to Parse
     *
     * @param tripId   Parse Obj id of the trip
     * @param fbIDs    ArrayList of Facebook IDs
     * @param callback Callback that defines success and failure
     */
    public static void saveInvitees(String tripId, final ArrayList<String> fbIDs, final TripASyncTaskCallback callback) {
        getParseTrip(tripId, new ParseTripReceivedCallback() {
            @Override
            public void onSuccess(ParseObject trip) {
                getFBUsers(trip, fbIDs, callback);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Gets all current invitees of a trip from Parse
     *
     * @param trip     Trip object to set the members in
     * @param callback Callback that defines success and failure
     */
    public static void setAllInvitees(final Trip trip, final TripASyncTaskCallback callback) {
        getParseTrip(trip.getId(), new ParseTripReceivedCallback() {
            @Override
            public void onSuccess(ParseObject parseTrip) {
                ParseRelation<ParseUser> memberRelation = parseTrip.getRelation("invitees");
                memberRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> tripMembers, ParseException e) {
                        if (e != null) {
                            Log.d(TAG, "ParseExceptionOccurred. Code: " + e.getCode()
                                    + " Message: " + e.getMessage());
                            callback.onFailure(getParseErrorString(e.getCode()));
                            return;
                        }

                        for (ParseUser member : tripMembers) {
                            String name = member.get("firstName") + " " + member.get("lastName");
                            String objectId = member.getObjectId();
                            String fbId = (String) member.get("fbId");

                            trip.addInvitee(name, objectId, fbId);
                        }

                        callback.onSuccess();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public static void deleteTrip(String tripId, final TripASyncTaskCallback callback) {
        getParseTrip(tripId, new ParseTripReceivedCallback() {
            @Override
            public void onSuccess(ParseObject trip) {
                trip.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d(TAG, "ParseExceptionOccurred. Code: " + e.getCode()
                                    + " Message: " + e.getMessage());
                            callback.onFailure(getParseErrorString(e.getCode()));
                            return;
                        }

                        callback.onSuccess();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public static void removeUserFromRelation(final String tripId, String userId,
                                              final String relation, final TripASyncTaskCallback callback) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(userId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "ParseExceptionOccurred. Code: " + e.getCode()
                            + " Message: " + e.getMessage());
                    callback.onFailure(getParseErrorString(e.getCode()));
                    return;
                }

                removeUserFromRelation(tripId, parseUser, relation, callback);
            }
        });
    }

    public static void removeUserFromRelation(String tripId, final ParseUser parseUser,
                                              final String relation, final TripASyncTaskCallback callback) {
        getParseTrip(tripId, new ParseTripReceivedCallback() {
            @Override
            public void onSuccess(ParseObject trip) {
                ParseRelation<ParseUser> membersRelation = trip.getRelation(relation);
                membersRelation.remove(parseUser);

                trip.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d(TAG, "ParseExceptionOccurred. Code: " + e.getCode()
                                    + " Message: " + e.getMessage());
                            callback.onFailure(getParseErrorString(e.getCode()));
                            return;
                        }

                        callback.onSuccess();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public static void addUserToRelation(final String tripId, String userId,
                                         final String relation, final TripASyncTaskCallback callback) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(userId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "ParseExceptionOccurred. Code: " + e.getCode()
                            + " Message: " + e.getMessage());
                    callback.onFailure(getParseErrorString(e.getCode()));
                    return;
                }

                addUserToRelation(tripId, parseUser, relation, callback);
            }
        });
    }

    public static void addUserToRelation(String tripId, final ParseUser parseUser,
                                         final String relation, final TripASyncTaskCallback callback) {
        getParseTrip(tripId, new ParseTripReceivedCallback() {
            @Override
            public void onSuccess(ParseObject trip) {
                ParseRelation<ParseUser> membersRelation = trip.getRelation(relation);
                membersRelation.add(parseUser);

                trip.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d(TAG, "ParseExceptionOccurred. Code: " + e.getCode()
                                    + " Message: " + e.getMessage());
                            callback.onFailure(getParseErrorString(e.getCode()));
                            return;
                        }

                        callback.onSuccess();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    /***************************************************************************************
     *                         PRIVATE METHODS (Helper methods)
     ***************************************************************************************/

    /**
     * Helper method for getting all the trips from the database
     *
     * @param trips    List to put the trips inside
     * @param callback Called when all trips are retrieved from the database
     */
    private static void getAllMyTrips(final List<ParseObject> trips, final ParseTripCallback callback) {
        final ArrayList<Trip> allMyTrips = new ArrayList<>();

        if (trips == null) {
            Log.d(TAG, "0 Trips received");
            return;
        }

        final int[] tripMembersSet = {0};

        for (ParseObject parseTrip : trips) {
            final Trip trip = new Trip();

            trip.setId(parseTrip.getObjectId());
            trip.setCreatorId(parseTrip.getString("creatorId"));

            trip.setName(parseTrip.getString("name"));
            trip.setTransportation(parseTrip.getString("transportation"));
            trip.setPrivate(parseTrip.getBoolean("private"));

            trip.setOrigin(parseTrip.getJSONObject("origin"));
            trip.setDestination(parseTrip.getJSONObject("destination"));

            trip.setDateFrom(parseTrip.getDate("dateFrom"));
            trip.setDateTo(parseTrip.getDate("dateTo"));

            allMyTrips.add(trip);

            setAllMembers(parseTrip, trip, new SavedMembersCallback() {
                @Override
                public void onMemSaved() {
                    tripMembersSet[0]++;

                    if (tripMembersSet[0] == trips.size()) {
                        Log.d(TAG, trips.size() + " trips successfully retrieved");
                        callback.onCompleted(allMyTrips);
                    }
                }
            });
        }
    }


    private static void setAllMembers(final ParseObject parseTrip, final Trip trip, final SavedMembersCallback callback) {
        ParseRelation<ParseUser> memRelation = parseTrip.getRelation("members");
        memRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> members, ParseException e) {
                if (e != null || members == null) {
                    return;
                }

                for (ParseUser member : members) {
                    trip.addMember(member.get("firstName") + " " + member.get("lastName"),
                            member.getObjectId(), (String) member.get("fbId"));
                }

                ParseRelation<ParseUser> inviteesRelation = parseTrip.getRelation("invitees");
                inviteesRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> invitees, ParseException e) {
                        if (e != null || invitees == null) {
                            return;
                        }

                        for (ParseUser invitee : invitees) {
                            trip.addInvitee(invitee.get("firstName") + " " + invitee.get("lastName"),
                                    invitee.getObjectId(), (String) invitee.get("fbId"));
                        }

                        callback.onMemSaved();
                    }
                });
            }
        });
    }


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
                    callback.onFailure(getParseErrorString(e.getCode()));
                }
            }
        });
    }


    private static void setUpInviteeRelations(final List<ParseUser> objects, final ParseObject parseTrip, final TripASyncTaskCallback callback) {
        ParseRelation<ParseUser> relation = parseTrip.getRelation("invitees");

        for (ParseUser user : objects) {
            relation.add(user);
        }

        parseTrip.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseNotificationModel.sendRequestNotifications(parseTrip, objects);
                    callback.onSuccess();
                } else {
                    Log.d(TAG, "ParseExceptionOccurred. Code: " + e.getCode()
                            + " Message: " + e.getMessage());
                    callback.onFailure(getParseErrorString(e.getCode()));
                }
            }
        });
    }


    private static void getParseTrip(String tripId, final ParseTripReceivedCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Trip");
        query.getInBackground(tripId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseTrip, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "ParseExceptionOccurred. Code: " + e.getCode()
                            + " Message: " + e.getMessage());
                    callback.onFailure(getParseErrorString(e.getCode()));
                    return;
                }

                callback.onSuccess(parseTrip);
            }
        });
    }

    private static ParseObject getParseObjectFromTrip(Trip trip) {
        final ParseObject parseTrip = new ParseObject("Trip");
        parseTrip.put("name", trip.getName());
        parseTrip.put("creatorId", trip.getCreatorId());
        parseTrip.put("origin", trip.getOrigin());
        parseTrip.put("destination", trip.getDestination());
        parseTrip.put("private", trip.isPrivate());
        parseTrip.put("dateFrom", trip.getDateFrom());
        parseTrip.put("dateTo", trip.getDateTo());
        parseTrip.put("transportation", trip.getTransportation());

        return parseTrip;
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
     * Interface to implement a callback that is accepted in methods of this class
     */
    public interface ParseTripCallback {
        void onCompleted(ArrayList<Trip> trip);
    }

    public interface TripASyncTaskCallback {
        void onSuccess();

        void onFailure(String error);
    }

    private interface ParseTripReceivedCallback {
        void onSuccess(ParseObject trip);

        void onFailure(String error);
    }

    private interface SavedMembersCallback {
        void onMemSaved();
    }
}
