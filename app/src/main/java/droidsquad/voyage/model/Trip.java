package droidsquad.voyage.model;

import com.parse.ParseObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Andrew on 1/23/16.
 */
public class Trip {

    private static final int DEFAULT_LIMIT = 5;
    private static final String DEFAULT_TRANSP = "Plane";

    // General
    private String tripName;
    private String tripOrigin;
    private String tripDestination;
    boolean tripPrivate;

    // Details
    private int tripLimit;
    private Date dateFrom;
    private Date dateTo;
    private String transportation;

    public Trip( String name, String orig, String dest, boolean priv,
                 int limit, Date dateFrom, Date dateTo, String transp ){

        tripName = name;
        tripOrigin = orig;
        tripDestination = dest;
        tripPrivate = priv;

        tripLimit = limit;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        transportation = transp;

    }

    // TODO: Define minimum requirements to create a trip
    public Trip( String name, String orig, String dest, boolean priv  ){
        tripName = name;
        tripOrigin = orig;
        tripDestination = dest;
        tripPrivate = priv;

        // TODO: Define default values
        tripLimit = DEFAULT_LIMIT;
        Calendar calendar = Calendar.getInstance();
        dateFrom = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
        dateTo = calendar.getTime();
        transportation = DEFAULT_TRANSP;
    }

    public void setLimit( int limit ) {
        tripLimit = limit;
    }

    public void setDateFrom( Date from ) {
        dateFrom = from;
    }

    public void setDateto( Date to ) {
        dateTo = to;
    }

    public void setTransp( String transp ){
        transportation = transp;
    }

    @Override
    public String toString(){
        String toReturn = "Trip Name: " + tripName + "\n";
        toReturn += "Leaving From: " + tripOrigin + "\n";
        toReturn += "Destination: " + tripDestination + "\n";
        toReturn += "Private: " + tripPrivate + "\n";
        return toReturn;
    }

    public void save() {
        ParseObject trip = new ParseObject("Trip");
        trip.put("name", tripName);
        trip.put("origin", tripOrigin);
        trip.put("destination", tripDestination);
        trip.put("private", tripPrivate);
        trip.put("dateFrom", dateFrom);
        trip.put("dateTo", dateTo);
        trip.put("limit", tripLimit);
        trip.put("transportation", transportation);
        trip.saveInBackground();
    }

}
