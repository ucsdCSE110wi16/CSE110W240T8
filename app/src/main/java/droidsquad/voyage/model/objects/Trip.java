package droidsquad.voyage.model.objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Trip implements Parcelable {
    private static final String TAG = Trip.class.getSimpleName();

    private String id;
    private String creatorId;
    private String name;
    private String transportation;
    private JSONObject origin;
    private JSONObject destination;
    private Date dateFrom;
    private Date dateTo;
    private boolean isPrivate;
    public boolean membersAreSet;

    private ArrayList<TripMember> allParticipants;

    public Trip() {
        // No Arguments Constructor
        this.allParticipants = new ArrayList<>();
        membersAreSet = false;
    }

    public Trip(String name, String creatorId, String transportation, JSONObject origin,
                JSONObject destination, boolean isPrivate, Date dateFrom, Date dateTo) {
        this.name = name;
        this.origin = origin;
        this.destination = destination;
        this.transportation = transportation;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.isPrivate = isPrivate;
        this.creatorId = creatorId;
        this.allParticipants = new ArrayList<>();
        membersAreSet = false;
    }

    protected Trip(Parcel in) {
        this();
        Log.i(TAG, "Retrieving Trip from Parcel");
        id = in.readString();
        creatorId = in.readString();
        name = in.readString();
        transportation = in.readString();

        try {
            origin = new JSONObject(in.readString());
            destination = new JSONObject(in.readString());
        } catch (JSONException e) {
            Log.d(TAG, "JSONException occurred with message: " + e.getMessage());
            e.printStackTrace();
        }

        dateFrom = new Date(in.readLong());
        dateTo = new Date(in.readLong());
        isPrivate = in.readByte() != 0;
        int numOfParticipants = in.readInt();

        for (int i = 0; i < numOfParticipants; i++) {
            String name = in.readString();
            String objectId = in.readString();
            String fbId = in.readString();
            addMember(name, objectId, fbId);
        }
        membersAreSet = in.readByte() != 0;
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(creatorId);
        dest.writeString(name);
        dest.writeString(transportation);
        dest.writeString(origin.toString());
        dest.writeString(destination.toString());
        dest.writeLong(dateFrom.getTime());
        dest.writeLong(dateTo.getTime());
        dest.writeByte((byte) (isPrivate ? 1 : 0));
        dest.writeInt(allParticipants.size());

        for (TripMember participant : allParticipants) {
            dest.writeString(participant.name);
            dest.writeString(participant.objectId);
            dest.writeString(participant.fbId);
        }

        dest.writeByte((byte) (membersAreSet ? 1 : 0));
    }

    @Override
    public String toString() {
        return "Trip Name: " + name + "\n" +
                "Private: " + isPrivate + "\n" +
                "Leaving From: " + origin + "\n" +
                "Destination: " + destination + "\n" +
                "Date From: " + dateFrom + "\n" +
                "Date To: " + dateTo + "\n" +
                "Transportation: " + transportation + "\n";
    }

    /**
     * Checks if the current trip overlaps with the other, in terms of departure and arrival
     *
     * @param other Trip object to check for overlaps with this trip
     * @return True if other overlaps with this trip, false otherwise
     */
    public boolean overlaps(Trip other) {
        return (other.getDateFrom().before(this.dateTo) && other.getDateFrom().after(this.dateFrom))
                || (other.getDateTo().before(this.dateTo) && other.getDateTo().after(this.dateFrom))
                || (other.getDateTo().equals(this.dateTo) && other.getDateFrom().equals(this.dateFrom));
    }

    public ArrayList<TripMember> getAllParticipants() {
        return allParticipants;
    }

    public void addMember(String name, String objectId, String fbId) {
        allParticipants.add(new TripMember(name, objectId, fbId));
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

    public Date getDateFrom() {
        return dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
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

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public class TripMember implements Serializable {
        public String name, objectId, fbId;

        public TripMember(String name, String objectId, String fbId) {
            this.name = name;
            this.objectId = objectId;
            this.fbId = fbId;
        }
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Trip))
            return false;
        Trip trip = (Trip) o;
        return name.equals(trip.getName()) && creatorId.equals(trip.getCreatorId()) &&
                transportation.equals(trip.getTransportation()) && isPrivate == trip.isPrivate()
                && dateFrom.equals(trip.getDateFrom()) && dateTo.equals(trip.getDateTo())
                && destination.toString().equals(trip.getDestination().toString())
                && origin.toString().equals(trip.getOrigin().toString());
    }
}
