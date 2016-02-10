package droidsquad.voyage.model;

import com.parse.ParseUser;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Trip implements Serializable {
    private String creatorId;
    private String tripId;

    private String name;
    private String origin;
    private String destination;
    private String transportation;
    private Date dateFrom;
    private Date dateTo;
    private boolean isPrivate;

    private ArrayList<String> allParticipants;

    public Trip(String name, String origin, String destination, boolean isPrivate,
                Date dateFrom, Date dateTo, String transportation, String creatorId) {
        this.name = name;
        this.origin = origin;
        this.destination = destination;
        this.transportation = transportation;

        this.dateFrom = dateFrom;
        this.dateTo = dateTo;

        this.isPrivate = isPrivate;
        this.creatorId = creatorId;
    }

    @Override
    public String toString() {
        return "Trip Name: " + name + "\n" +
                "Leaving From: " + origin + "\n" +
                "Destination: " + destination + "\n" +
                "Transportation: " + transportation + "\n" +
                "Private: " + isPrivate + "\n" +
                "Date From: " + dateFrom + "\n" +
                "Date To: " + dateTo + "\n";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        this.isPrivate = aPrivate;
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
