package droidsquad.voyage.model;

import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class Trip {
    private static final int DEFAULT_LIMIT = 5;

    private String creatorId;
    String tripId;

    private String name;
    private JSONObject origin;
    private JSONObject destination;
    private String transportation;

    private Date dateFrom;
    private Date dateTo;

    private boolean isPrivate;
    private int membersLimit;

    ArrayList<String> allParticipants;

    public Trip(String name, JSONObject origin, JSONObject destination, boolean isPrivate,
                int membersLimit, Date dateFrom, Date dateTo, String transportation,
                String creatorId) {
        this.name = name;
        this.origin = origin;
        this.destination = destination;
        this.transportation = transportation;

        this.dateFrom = dateFrom;
        this.dateTo = dateTo;

        this.isPrivate = isPrivate;
        this.membersLimit = membersLimit;

        this.creatorId = creatorId;
    }

    /**
     * TODO: Check if the Trip is valid for saving in Parse
     *
     * @return true if trip is valid, false otherwise
     */
    public boolean isValid() {
        // Use googlePlacesModel.isSourceCityValid() and googlePlacesModel.isDestCityValid()
        // to know if the user did select a valid Google Maps location or not.

        return true;
    }

    @Override
    public String toString() {
        return "Trip Name: " + name + "\n" +
                "Leaving From: " + origin + "\n" +
                "Destination: " + destination + "\n" +
                "Transportation: " + transportation + "\n" +
                "Private: " + isPrivate + "\n" +
                "Limit: " + membersLimit + " persons\n" +
                "Date From: " + dateFrom + "\n" +
                "Date To: " + dateTo + "\n";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JSONObject getOrigin() {
        return origin;
    }

    public void setOrigin(JSONObject origin) {
        this.origin = origin;
    }

    public JSONObject getDestination() {
        return destination;
    }

    public void setDestination(JSONObject destination) {
        this.destination = destination;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        this.isPrivate = aPrivate;
    }

    public int getTripLimit() {
        return membersLimit;
    }

    public void setMembersLimit(int membersLimit) {
        this.membersLimit = membersLimit;
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

    public void setCreatorId( String creatorId ) {
        this.creatorId = creatorId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripId() {
        return tripId;
    }
}
