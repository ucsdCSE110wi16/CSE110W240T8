package droidsquad.voyage.model.parseModels;

import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import droidsquad.voyage.model.objects.Member;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.model.objects.User;
import droidsquad.voyage.model.objects.VoyageUser;

public class ParseTripModel extends ParseModel {
    private static final String TAG = ParseTripModel.class.getSimpleName();
    protected static final String TRIP_CLASS = "Trip";

    // Interfaces for providing a namespace for the Fields of the Trip class in Parse database
    protected interface Field {
        String NAME = "name";
        String PRIVATE = "private";
        String CREATED_BY = "createdBy";
        String ORIGIN = "origin";
        String DESTINATION = "destination";
        String DATE_FROM = "dateFrom";
        String DATE_TO = "dateTo";
        String TRANSPORTATION = "transportation";
        String MEMBERS = "members";
    }

    /***************************************************************************************
     *                         PUBLIC METHODS (To be used in other files)
     ***************************************************************************************/

    /**
     * Save this trip to the database and add current user as the creator
     *
     * @param trip Trip to be saved
     */
    public static void saveNewTrip(final Trip trip) {
        saveNewTrip(trip, new ParseResponseCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Successfully saved trip: " + trip);
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Failed to save trip to Parse: " + error);
            }
        });
    }

    /**
     * Save this trip to the database and add current user as the creator
     *
     * @param trip     Trip to be saved
     * @param callback Called with response from Parse server
     */
    public static void saveNewTrip(final Trip trip, final ParseResponseCallback callback) {
        final ParseObject parseTrip = getParseObjectFromTrip(trip);
        parseTrip.put(Field.CREATED_BY, ParseUser.getCurrentUser());

        ParseMemberModel.getParseObjectFromMember(new Member(VoyageUser.currentUser(), false, System.currentTimeMillis()),
                new ParseModel.ParseObjectCallback() {
                    @Override
                    public void onSuccess(ParseObject parseMember) {
                        parseTrip.add(Field.MEMBERS, parseMember);
                        saveTrip(parseTrip, callback);
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.d(TAG, "Failed to save current user into the trip: " + trip);
                        callback.onFailure(error);
                    }
                });
    }

    /**
     * Update the database with the trip's new information
     *
     * @param trip The trip to be updated to the database
     */
    public static void updateTrip(final Trip trip) {
        updateTrip(trip, new ParseResponseCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Successfully updated the trip: " + trip);
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Failed to update the trip: " + error);
            }
        });
    }

    /**
     * Update the database with the trip's new information
     *
     * @param trip The trip to be updated to the database
     */
    public static void updateTrip(final Trip trip, final ParseResponseCallback callback) {
        getParseTrip(trip.getId(), new ParseObjectCallback() {
            @Override
            public void onSuccess(final ParseObject parseObject) {
                setTripFieldsIntoParseObject(trip, parseObject);
                saveTrip(parseObject, callback);
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Error while updating trip: " + error);
                callback.onFailure(error);
            }
        });
    }

    /**
     * Get all the trips for which the current user is a member or creator of
     *
     * @param callback Called with the retrieved Trips
     */
    public static void getTrips(TripListCallback callback) {
        ParseQuery<ParseObject> innerQuery = ParseQuery.getQuery(ParseMemberModel.MEMBER_CLASS);
        innerQuery.whereEqualTo(ParseMemberModel.Field.USER, ParseUser.getCurrentUser());
        innerQuery.whereEqualTo(ParseMemberModel.Field.PENDING_REQUEST, false);
        innerQuery.include(ParseMemberModel.Field.USER);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(TRIP_CLASS);
        query.whereMatchesQuery(Field.MEMBERS, innerQuery);
        query.include(Field.MEMBERS);

        getTrips(query, callback);
    }

    /**
     * Get all the public trips
     *
     * @param callback Called with the retrieved Trips
     */
    public static void getPublicTrips(TripListCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TRIP_CLASS);
        query.whereEqualTo(Field.PRIVATE, false);
        query.include(Field.MEMBERS);
        getTrips(query, callback);
    }

    /**
     * Get all the trips for which the current User is an invitee
     *
     * @param callback Called with the trips retrieved
     */
    public static void getTripsInvitedTo(TripListCallback callback) {
        ParseQuery<ParseObject> memberQuery = ParseQuery.getQuery(ParseMemberModel.MEMBER_CLASS);
        memberQuery.whereEqualTo(ParseMemberModel.Field.USER, ParseUser.getCurrentUser());
        memberQuery.whereEqualTo(ParseMemberModel.Field.PENDING_REQUEST, true);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(TRIP_CLASS);
        query.whereMatchesQuery(ParseTripModel.Field.MEMBERS, memberQuery);
        query.include(ParseTripModel.Field.MEMBERS);

        getTrips(query, callback);
    }

    /**
     * Get the public trips for which the current user's friends are part of
     *
     * @param friends  The friends to get the trips from
     * @param callback Called with the Trips or error on failure
     */
    public static void getTripsFromFriends(List<User> friends, TripListCallback callback) {
        List<String> ids = new ArrayList<>();
        for (User friend : friends) {
            ids.add(friend.id);
        }

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereContainedIn(ParseUserModel.Field.ID, ids);

        ParseQuery<ParseObject> memberQuery = new ParseQuery<>(ParseMemberModel.MEMBER_CLASS);
        memberQuery.whereMatchesQuery(ParseMemberModel.Field.USER, userQuery);
        memberQuery.include(ParseMemberModel.Field.USER);

        ParseQuery<ParseObject> query = new ParseQuery<>(TRIP_CLASS);
        query.whereEqualTo(Field.PRIVATE, false);
        query.whereMatchesQuery(Field.MEMBERS, memberQuery);
        query.addAscendingOrder(Field.DATE_FROM);
        query.setLimit(10);

        getTrips(query, callback);
    }

    /**
     * Save the invitees to Parse
     *
     * @param trip     Parse Obj id of the trip
     * @param invitees ArrayList of Facebook IDs
     * @param callback Callback that defines success and failure
     */
    public static void saveInvitees(final Trip trip, final List<User> invitees,
                                    final ParseResponseCallback callback) {
        getParseTrip(trip.getId(), new ParseObjectCallback() {
            @Override
            public void onSuccess(final ParseObject parseTrip) {
                ParseQuery<ParseUser> userQuery = ParseUser.getQuery();

                // Get all the facebook ids
                List<String> fbIds = new ArrayList<>();
                for (User user : invitees) {
                    fbIds.add(user.fbId);
                }

                userQuery.whereContainedIn(ParseUserModel.Field.Facebook_ID, fbIds);
                userQuery.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(final List<ParseUser> parseUsers, ParseException e) {
                        if (e == null) {
                            final List<ParseObject> parseMembers = ParseMemberModel.createMembersFromParseUsers(parseUsers);
                            parseTrip.addAll(ParseTripModel.Field.MEMBERS, parseMembers);

                            saveTrip(parseTrip, new ParseResponseCallback() {
                                @Override
                                public void onSuccess() {
                                    ParseNotificationModel.sendRequestNotifications(parseTrip, parseMembers);
                                    trip.addMembers(ParseMemberModel.getMembersFromParseObjects(parseMembers));
                                    callback.onSuccess();
                                }

                                @Override
                                public void onFailure(String error) {
                                    callback.onFailure(error);
                                }
                            });
                        } else {
                            Log.d(TAG, "ParseExceptionOccurred. Code: " + e.getCode()
                                    + " Message: " + e.getMessage());
                            callback.onFailure(getParseErrorString(e.getCode()));
                        }
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Delete the given trip from the Parse
     *
     * @param trip     Trip to be deleted
     * @param callback Called on success or failure
     */
    public static void deleteTrip(Trip trip, final ParseResponseCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TRIP_CLASS);
        query.include(Field.MEMBERS);
        getParseTrip(trip.getId(), new ParseObjectCallback() {
            @Override
            public void onSuccess(ParseObject parseObject) {
                List<ParseUser> members = parseObject.getList(Field.MEMBERS);
                ParseObject.deleteAllInBackground(members);
                parseObject.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            callback.onSuccess();
                        } else {
                            Log.d(TAG, "ParseExceptionOccurred. Code: " + e.getCode()
                                    + " Message: " + e.getMessage());
                            callback.onFailure(getParseErrorString(e.getCode()));
                        }
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Remove the given user from the given trip
     *
     * @param tripId   Id of the trip to remove member from
     * @param memberId Id of the member to remove from
     * @param callback Called on success or failure
     */
    public static void removeMemberFromTrip(final String tripId, final String memberId,
                                            final ParseResponseCallback callback) {

        ParseQuery<ParseObject> memberQuery = ParseQuery.getQuery(ParseMemberModel.MEMBER_CLASS);
        memberQuery.whereEqualTo(ParseMemberModel.Field.ID, memberId);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(TRIP_CLASS);
        query.whereMatchesQuery(Field.MEMBERS, memberQuery);
        query.include(Field.MEMBERS);

        getParseTrip(tripId, query, new ParseObjectCallback() {
            @Override
            public void onSuccess(final ParseObject parseTrip) {
                ParseObject parseMember = getParseMemberFromParseTrip(memberId, parseTrip);
                if (parseMember != null) {
                    parseTrip.removeAll(Field.MEMBERS, Collections.singletonList(parseMember));
                    parseMember.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            saveTrip(parseTrip, callback);
                        }
                    });
                } else {
                    Log.d(TAG, "Failed to remove member from Trip: Could not find member");
                    callback.onFailure("There was an error with the server");
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    /***************************************************************************************
     *                       PRIVATE METHODS (Helper methods)
     ***************************************************************************************/

    /**
     * Save the trip to Parse database
     *
     * @param parseTrip The parseTrip object to be saved
     * @param callback  Called once saving is done
     */
    private static void saveTrip(final ParseObject parseTrip, final ParseResponseCallback callback) {
        parseTrip.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(getParseErrorString(e.getCode()));
                }
            }
        });
    }

    /**
     * Gets all the trips from the database that this user is part of
     *
     * @param callback Called with the trips as an argument
     */
    private static void getTrips(ParseQuery<ParseObject> query, final TripListCallback callback) {
        query.include(Field.CREATED_BY);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseTrips, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Trips Retrieved: " + parseTrips.size());
                    List<Trip> trips = new ArrayList<>();

                    for (ParseObject parseTrip : parseTrips) {
                        Trip trip = getTripFromParseObject(parseTrip);
                        List<ParseObject> parseMembers = parseTrip.getList(ParseTripModel.Field.MEMBERS);

                        if (parseMembers != null) {
                            trip.addMembers(ParseMemberModel.getMembersFromParseObjects(parseMembers));
                        }

                        trips.add(trip);
                    }

                    callback.onSuccess(trips);

                } else {
                    Log.d(TAG, "Error while getting trips: " + e.getMessage());
                    callback.onFailure(getParseErrorString(e.getCode()));
                }
            }
        });
    }

    /**
     * Get the ParseTrip from the database
     *
     * @param tripId   Id of the trip to be retrieved
     * @param callback Called with the ParseTrip object
     */
    private static void getParseTrip(String tripId, final ParseObjectCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TRIP_CLASS);
        getParseTrip(tripId, query, callback);
    }

    /**
     * Get the ParseTrip from the database with the given query
     *
     * @param query    Query to be used for getting the Trip
     * @param tripId   Id of the trip to be retrieved
     * @param callback Called with the ParseTrip object
     */
    private static void getParseTrip(String tripId, ParseQuery<ParseObject> query,
                                     final ParseObjectCallback callback) {
        query.getInBackground(tripId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseTrip, ParseException e) {
                if (e == null) {
                    callback.onSuccess(parseTrip);

                } else {
                    Log.d(TAG, "ParseExceptionOccurred. Code: " + e.getCode()
                            + " Message: " + e.getMessage());
                    callback.onFailure(getParseErrorString(e.getCode()));
                }
            }
        });
    }

    /**
     * Get the Member with the specified id from the ParseTrip
     *
     * @param memberId  Id of the member to be retrieved
     * @param parseTrip ParseTrip to extract member from
     * @return Member with the given userId
     */
    private static ParseObject getParseMemberFromParseTrip(String memberId, ParseObject parseTrip) {
        List<ParseObject> members = parseTrip.getList(Field.MEMBERS);
        if (members != null) {
            for (ParseObject member : members) {
                if (member.getObjectId().equals(memberId)) {
                    return member;
                }
            }
        }
        return null;
    }

    /**
     * Get a Trip object from the ParseObject
     *
     * @param parseTrip ParseObject to extract Trip from
     * @return A trip with the fields of the ParseObject
     */
    private static Trip getTripFromParseObject(ParseObject parseTrip) {
        Trip trip = new Trip();
        trip.setId(parseTrip.getObjectId());
        trip.setName(parseTrip.getString(Field.NAME));
        trip.setTransportation(parseTrip.getString(Field.TRANSPORTATION));
        trip.setPrivate(parseTrip.getBoolean(Field.PRIVATE));
        trip.setOrigin(parseTrip.getJSONObject(Field.ORIGIN));
        trip.setDestination(parseTrip.getJSONObject(Field.DESTINATION));
        trip.setDateFrom(parseTrip.getDate(Field.DATE_FROM));
        trip.setDateTo(parseTrip.getDate(Field.DATE_TO));
        trip.setAdmin(ParseUserModel.getUserFromParseUser(
                parseTrip.getParseUser(Field.CREATED_BY)));
        return trip;
    }

    /**
     * Get a ParseObject from the Trip
     *
     * @param trip Trip to turn into a ParseObject
     * @return The ParseObject with the Trip's fields
     */
    private static ParseObject getParseObjectFromTrip(Trip trip) {
        ParseObject parseTrip = new ParseObject(TRIP_CLASS);
        setTripFieldsIntoParseObject(trip, parseTrip);
        return parseTrip;
    }

    /**
     * Sets all the Trip fields into the given ParseObject
     *
     * @param trip      The trip to get the fields from
     * @param parseTrip The parse object to set the fields to
     */
    private static void setTripFieldsIntoParseObject(Trip trip, ParseObject parseTrip) {
        parseTrip.put(Field.NAME, trip.getName());
        parseTrip.put(Field.ORIGIN, trip.getOrigin());
        parseTrip.put(Field.DESTINATION, trip.getDestination());
        parseTrip.put(Field.PRIVATE, trip.isPrivate());
        parseTrip.put(Field.DATE_FROM, trip.getDateFrom());
        parseTrip.put(Field.DATE_TO, trip.getDateTo());
        parseTrip.put(Field.TRANSPORTATION, trip.getTransportation());
    }

    /**
     * Interface to implement a callback that is accepted in methods of this class
     */
    public interface TripListCallback {
        void onSuccess(List<Trip> trips);

        void onFailure(String error);
    }
}