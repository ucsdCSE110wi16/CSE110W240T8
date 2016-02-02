package droidsquad.voyage.model;

import android.content.Context;

import com.parse.ParseObject;
import com.parse.ParseUser;

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
        /**TODO: ZEMEI*/
        parseTrip.put("creator", trip.getCreator());
        parseTrip.put("origin", trip.getOrigin());
        parseTrip.put("destination", trip.getDestination());
        parseTrip.put("private", trip.isPrivate());
        parseTrip.put("dateFrom", trip.getDateFrom());
        parseTrip.put("dateTo", trip.getDateTo());
        parseTrip.put("limit", trip.getTripLimit());
        parseTrip.put("transportation", trip.getTransportation());
        parseTrip.saveInBackground();
    }

    public static ParseUser getUser() {
        return ParseUser.getCurrentUser();
    }

}
