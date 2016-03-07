package droidsquad.voyage.model.parseModels;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import droidsquad.voyage.model.objects.Member;
import droidsquad.voyage.model.objects.Request;
import droidsquad.voyage.model.objects.Trip;

public class ParseRequestModel extends ParseModel {
    public static final String TAG = ParseRequestModel.class.getSimpleName();

    /**
     * Get all the invitations to join trips sent to the current user
     *
     * @param callback Called with the list of Request objects
     */
    public static void fetchInvitations(final RequestListCallback callback) {
        // Get all trips for which the user has been invited
        ParseTripModel.getTripsInvitedTo(new ParseTripModel.TripListCallback() {
            @Override
            public void onSuccess(List<Trip> trips) {
                callback.onSuccess(getInvitationsFromTrips(trips));
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Failed to fetch the requests: " + error);
                callback.onFailure(error);
            }
        });
    }

    /**
     * Get all the invitations and requests pertinent to the current user
     *
     * @param callback Called on success with all the invitations and requests
     */
    public static void fetchInvitationsAndRequests(final RequestListCallback callback) {
        fetchInvitations(new RequestListCallback() {
            @Override
            public void onSuccess(final List<Request> invitations) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseTripModel.TRIP_CLASS);
                query.whereEqualTo(ParseTripModel.Field.CREATED_BY, ParseUser.getCurrentUser());
                query.include(ParseTripModel.Field.REQUESTS);
                query.include(ParseTripModel.Field.REQUESTS + "." + ParseMemberModel.Field.USER);

                ParseTripModel.getTrips(query, new ParseTripModel.TripListCallback() {
                    @Override
                    public void onSuccess(List<Trip> trips) {
                        List<Request> requests = getRequestsFromTrips(trips);
                        requests.addAll(invitations);
                        callback.onSuccess(requests);
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.d(TAG, "Error while getting trips for invitations and requests");
                        callback.onFailure(error);
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
     * Accept the request for the given trip
     *
     * @param memberId Id of the member who accepted the request
     * @param callback Called on success or error
     */
    public static void acceptRequest(final String memberId, final ParseResponseCallback callback) {
        ParseMemberModel.promoteInvitee(memberId, callback);
    }

    /**
     * Decline the request for the given trip
     *
     * @param request  Request to be declined
     * @param callback Called on success or error
     */
    public static void declineRequest(Request request, final ParseResponseCallback callback) {
        ParseTripModel.removeMemberFromTrip(request.trip.getId(), request.memberId,
                new ParseModel.ParseResponseCallback() {
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

    public static void acceptRequestFromNotification(String tripId, ParseResponseCallback callback) {
        ParseMemberModel.promoteCurrentUser(tripId, callback);
    }

    public static void declineRequestFromNotification(String tripId, ParseResponseCallback callback) {
        ParseMemberModel.removeCurrentUser(tripId, callback);
    }

    public static void sendRequest(Trip trip, final ParseResponseCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseTripModel.TRIP_CLASS);
        query.getInBackground(trip.getId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseTrip, ParseException e) {
                if (e == null) {
                    ParseObject parseMember = ParseMemberModel.createMemberFromParseUser(ParseUser.getCurrentUser());
                    parseTrip.add(ParseTripModel.Field.REQUESTS, parseMember);
                    parseTrip.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                callback.onSuccess();
                            } else {
                                callback.onFailure(e.getMessage());
                            }
                        }
                    });
                } else {
                    callback.onFailure(e.getMessage());
                }
            }
        });
    }

    /**
     * Helper method to turn ParseTrip objects into Request objects
     *
     * @param trips The parse trips objects to be turned into Request objects
     */
    private static List<Request> getInvitationsFromTrips(List<Trip> trips) {
        final List<Request> invitations = new ArrayList<>();

        for (Trip trip : trips) {
            Request request = new Request();
            request.trip = trip;
            request.user = trip.getAdmin();
            request.memberId = trip.getInvitees().get(0).id;
            request.elapsedTime = trip.getInvitees().get(0).time;
            request.isInvitation = true;
            invitations.add(request);
        }

        return invitations;
    }

    private static List<Request> getRequestsFromTrips(List<Trip> trips) {
        List<Request> requests = new ArrayList<>();

        for (Trip trip : trips) {
            requests.addAll(trip.getRequests());
        }

        return requests;
    }

    public static List<Request> getRequestsFromParseTrip(ParseObject parseTrip) {
        List<ParseObject> parseMembers = parseTrip.getList(ParseTripModel.Field.REQUESTS);
        List<Request> requests = new ArrayList<>();

        try {
            for (ParseObject parseMember : parseMembers) {
                Member member = ParseMemberModel.getMemberFromParseObject(parseMember);
                Request request = new Request();
                request.user = member.user;
                request.memberId = member.id;
                request.elapsedTime = member.time;
                request.isInvitation = false;
                requests.add(request);
            }

        } catch (Exception e) {
            Log.d(TAG, "Exception occurred while getting requests from trips: " + e.getMessage());
        }

        return requests;
    }

    public interface RequestListCallback {
        void onSuccess(List<Request> requests);

        void onFailure(String error);
    }
}
