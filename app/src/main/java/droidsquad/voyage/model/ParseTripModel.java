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
import droidsquad.voyage.model.objects.User;
import droidsquad.voyage.model.objects.VoyageUser;
import droidsquad.voyage.util.Constants;

public class ParseTripModel {
    private static final String TAG = ParseUser.class.getSimpleName();
    public static final String USER_OBJECT = "User";
    public static final String TRIP_OBJECT = "Trip";

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
        parseTrip.put("createdBy", ParseUser.getCurrentUser());

        parseTrip.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    trip.setId(parseTrip.getObjectId());
                    Log.d(TAG, "Trip saved successfully, Trip: " + trip);
                } else {
                    Log.d(TAG, "Failed to save trip: " + e.getMessage());
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

                setTripFieldsIntoParseObject(trip, parseTrip);

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
                Log.d(TAG, "Error while updating trip: " + error);
            }
        });
    }

    /**
     * Gets all the trips from the database that this user is part of
     *
     * @param callback Called with the trips as an argument
     */
    public static void getTrips(final ParseTripCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TRIP_OBJECT);
        query.whereEqualTo("members", ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Retrieved " + objects.size() + " trips");
                    getAllMyTrips(objects, callback);
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
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
     *  @param tripObj  Parse Obj id of the trip
     * @param invitees    ArrayList of Facebook IDs
     * @param callback Callback that defines success and failure
     */
    public static void saveInvitees(final Trip tripObj, final List<User> invitees, final TripASyncTaskCallback callback) {
        getParseTrip(tripObj.getId(), new ParseTripReceivedCallback() {
            @Override
            public void onSuccess(final ParseObject parseTrip) {
                ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                userQuery.whereContainedIn("fbId", invitees);
                userQuery.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(final List<ParseUser> objects, ParseException e) {
                        if (e != null) {
                            Log.d(TAG, "ParseExceptionOccurred. Code: " + e.getCode()
                                    + " Message: " + e.getMessage());
                            callback.onFailure(getParseErrorString(e.getCode()));
                            return;
                        }

                        ParseRelation<ParseUser> relation = parseTrip.getRelation("invitees");
                        for (ParseUser user : objects) {
                            tripObj.addInvitee(user.getObjectId(), user.getString("firstName"),
                                    user.getString("lastName"), user.getString("fbId"));

                            relation.add(user);
                        }

                        parseTrip.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.d(TAG, "ParseExceptionOccurred. Code: " + e.getCode()
                                            + " Message: " + e.getMessage());
                                    callback.onFailure(getParseErrorString(e.getCode()));
                                    return;
                                }

                                ParseNotificationModel.sendRequestNotifications(parseTrip, objects);
                                callback.onSuccess();
                            }
                        });
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
            return;
        }

        final int[] tripMembersSet = {0};

        for (ParseObject parseTrip : trips) {
            allMyTrips.add(getTripFromParseObject(parseTrip));

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

    private static Trip getTripFromParseObject(ParseObject parseTrip) {
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
        return trip;
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
                    trip.addMember(member.getObjectId(), member.getString("firstName"),
                            member.getString("lastName"), member.getString("fbId"));
                }

                ParseRelation<ParseUser> inviteesRelation = parseTrip.getRelation("invitees");
                inviteesRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> invitees, ParseException e) {
                        if (e != null || invitees == null) {
                            return;
                        }

                        for (ParseUser invitee : invitees) {
                            trip.addInvitee(invitee.getObjectId(), invitee.getString("firstName"),
                                    invitee.getString("lastName"), invitee.getString("fbId"));
                        }

                        callback.onMemSaved();
                    }
                });
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

    /**
     * Get a ParseObject from the Trip
     *
     * @param trip Trip to turn into a ParseObject
     * @return The ParseObject with the Trip's fields
     */
    private static ParseObject getParseObjectFromTrip(Trip trip) {
        ParseObject parseTrip = new ParseObject("Trip");
        setTripFieldsIntoParseObject(trip, parseTrip);
        return parseTrip;
    }

    /**
     * Sets all the Trip fields into the given ParseObject
     *
     * @param trip The trip to get the fields from
     * @param parseTrip The parse object to set the fields to
     */
    private static void setTripFieldsIntoParseObject(Trip trip, ParseObject parseTrip) {
        parseTrip.put("name", trip.getName());
        parseTrip.put("creatorId", trip.getCreatorId());
        parseTrip.put("origin", trip.getOrigin());
        parseTrip.put("destination", trip.getDestination());
        parseTrip.put("private", trip.isPrivate());
        parseTrip.put("dateFrom", trip.getDateFrom());
        parseTrip.put("dateTo", trip.getDateTo());
        parseTrip.put("transportation", trip.getTransportation());
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
