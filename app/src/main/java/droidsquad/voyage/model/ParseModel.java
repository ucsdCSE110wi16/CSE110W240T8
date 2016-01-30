package droidsquad.voyage.model;

import android.content.Context;

import com.parse.Parse;
import com.parse.ParseObject;

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
        parseTrip.put("name", trip.getTripName());
        parseTrip.put("origin", trip.getTripOrigin());
        parseTrip.put("destination", trip.getTripDestination());
        parseTrip.put("private", trip.isTripPrivate());
        parseTrip.put("dateFrom", trip.getDateFrom());
        parseTrip.put("dateTo", trip.getDateTo());
        parseTrip.put("limit", trip.getTripLimit());
        parseTrip.put("transportation", trip.getTransportation());
        parseTrip.saveInBackground();
    }
}
