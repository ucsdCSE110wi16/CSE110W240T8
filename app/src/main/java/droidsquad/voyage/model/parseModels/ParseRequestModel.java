package droidsquad.voyage.model.parseModels;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import droidsquad.voyage.model.objects.Request;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.model.objects.VoyageUser;

public class ParseRequestModel extends ParseModel {
    public static final String TAG = ParseRequestModel.class.getSimpleName();

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
     * @param request Request to be declined
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

    /**
     * Get all the trip requests sent to the current user
     *
     * @param callback Called with the list of Request objects
     */
    public static void fetchRequests(final RequestListCallback callback) {
        // Get all trips for which the user has been invited
        ParseTripModel.getTripsInvitedTo(new ParseTripModel.TripListCallback() {
            @Override
            public void onSuccess(List<Trip> trips) {
                callback.onSuccess(getRequestsFromTrips(trips));
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Failed to fetch the requests: " + error);
                callback.onFailure(error);
            }
        });
    }

    /**
     * Helper method to turn ParseTrip objects into Request objects
     *
     * @param trips The parse trips objects to be turned into Request objects
     */
    private static List<Request> getRequestsFromTrips(List<Trip> trips) {
        final List<Request> requests = new ArrayList<>();

        for (Trip trip : trips) {
            Request request = new Request();
            request.trip = trip;
            request.user = trip.getAdmin();
            request.memberId = trip.getInvitees().get(0).id;
            request.elapsedTime = trip.getInvitees().get(0).getElapsedTimeString();
            requests.add(request);
        }

        return requests;
    }

    public interface RequestListCallback {
        void onSuccess(List<Request> requests);

        void onFailure(String error);
    }
}
