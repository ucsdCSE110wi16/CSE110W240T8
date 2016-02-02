package droidsquad.voyage.model;

import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Andrew on 1/20/16.
 */
public class ParseModel {

    public static final String USER_OBJECT = "User";
    public static final String TRIP_OBJECT = "Trip";
    // add more key/value final Strings


    private Context context;

    public ParseModel() {
    }

    public static void saveTrip(Trip trip) {
        ParseObject parseTrip  = new ParseObject("Trip");
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
                if (e == null) {
                    getAllMyTrips(objects);
                } else {
                    //TODO: if no trips for the user are found
                }
            }
        });
    }

    public static void getAllMyTrips(List<ParseObject> trips) {
        //TODO: return Trip java objects
    }

    public static String getUser() {
        return ParseUser.getCurrentUser().getObjectId();
    }

}
