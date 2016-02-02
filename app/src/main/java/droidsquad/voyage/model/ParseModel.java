package droidsquad.voyage.model;

import android.content.Context;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Andrew on 1/20/16.
 */
public class ParseModel {

    public static final String USER_OBJECT = "User";
    public static final String TRIP_OBJECT = "Trip";
    private static final String TAG = ParseUser.class.getSimpleName();
    // add more key/value final Strings


    private Context context;

    public ParseModel() {
    }

    public static void saveTrip(Trip trip) {
        Log.d(TAG, "Attempting to save trip to parse.\nTrip: " + trip.toString());
        ParseObject parseTrip = new ParseObject("Trip");
        parseTrip.put("name", trip.getName());
        parseTrip.put("creatorId", trip.getCreatorId());
        parseTrip.put("origin", trip.getOrigin());
        parseTrip.put("destination", trip.getDestination());
        parseTrip.put("private", trip.isPrivate());
        parseTrip.put("dateFrom", trip.getDateFrom());
        parseTrip.put("dateTo", trip.getDateTo());
        parseTrip.put("limit", trip.getTripLimit());
        parseTrip.put("transportation", trip.getTransportation());

        //TODO: Initialize UserGroup stuff for new trips?
        ParseRelation<ParseObject> relation = parseTrip.getRelation("members");
        relation.add(ParseUser.getCurrentUser());
        parseTrip.saveInBackground();

        trip.setTripId(parseTrip.getObjectId());
    }

    public static void searchForAllTrips() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Trip");
        query.whereEqualTo("creatorId", ParseUser.getCurrentUser().getObjectId());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                getAllMyTrips(objects);
            }
        });
    }

    public static ArrayList<Trip> getAllMyTrips(List<ParseObject> trips) {
        ArrayList<Trip> allMyTrips = new ArrayList();
        if(trips == null)
            return allMyTrips;

        for(ParseObject parseTrip: trips) {
            String name = parseTrip.getString("name");
            String creatorId = parseTrip.getString("creatorId");
            JSONObject origin = parseTrip.getJSONObject("origin");
            JSONObject destination = parseTrip.getJSONObject("destination");
            boolean isPrivate = parseTrip.getBoolean("private");
            Date dateFrom = parseTrip.getDate("dateFrom");
            Date dateTo = parseTrip.getDate("dateTo");
            int memberLimit = parseTrip.getInt("memberLimit");
            String transportation = parseTrip.getString("transportation");

            Trip trip = new Trip(name, origin, destination, isPrivate, memberLimit,
                    dateFrom, dateTo, transportation, creatorId);

            allMyTrips.add(trip);
        }
        return allMyTrips;
    }

    public static String getUser() {
        return ParseUser.getCurrentUser().getObjectId();
    }

}
