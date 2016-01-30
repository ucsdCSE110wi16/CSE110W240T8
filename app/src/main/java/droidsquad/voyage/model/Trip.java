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
    private boolean tripPrivate;

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

    @Override
    public String toString(){
        String toReturn = "Trip Name: " + tripName + "\n";
        toReturn += "Leaving From: " + tripOrigin + "\n";
        toReturn += "Destination: " + tripDestination + "\n";
        toReturn += "Transportation: " + transportation + "\n";
        toReturn += "Private: " + tripPrivate + "\n";
        toReturn += "Limit: " + tripLimit + " persons\n";
        toReturn += "Date From: " + dateFrom + "\n";
        toReturn += "Date To: " + dateTo + "\n";
        return toReturn;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getTripOrigin() {
        return tripOrigin;
    }

    public void setTripOrigin(String tripOrigin) {
        this.tripOrigin = tripOrigin;
    }

    public String getTripDestination() {
        return tripDestination;
    }

    public void setTripDestination(String tripDestination) {
        this.tripDestination = tripDestination;
    }

    public boolean isTripPrivate() {
        return tripPrivate;
    }

    public void setTripPrivate(boolean tripPrivate) {
        this.tripPrivate = tripPrivate;
    }

    public int getTripLimit() {
        return tripLimit;
    }

    public void setTripLimit(int tripLimit) {
        this.tripLimit = tripLimit;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public String getTransportation() {
        return transportation;
    }

    public void setTransportation(String transportation) {
        this.transportation = transportation;
    }
}
