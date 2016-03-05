package droidsquad.voyage.model.objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import droidsquad.voyage.R;

public class Trip implements Parcelable {
    private static final String TAG = Trip.class.getSimpleName();

    private String id;
    private String name;
    private User admin;
    private boolean isPrivate;
    private String transportation;
    private JSONObject origin;
    private JSONObject destination;
    private Date dateFrom;
    private Date dateTo;
    private List<Member> members;
    private List<Member> invitees;

    public Trip() {
        this.members = new ArrayList<>();
        this.invitees = new ArrayList<>();
    }

    public Trip(String tripName, String transportation, JSONObject origin,
                JSONObject destination, boolean isPrivate, Date dateFrom, Date dateTo) {
        this.name = tripName;
        this.origin = origin;
        this.destination = destination;
        this.transportation = transportation;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.isPrivate = isPrivate;
        this.members = new ArrayList<>();
        this.invitees = new ArrayList<>();
    }

    protected Trip(Parcel in) {
        this();
        Log.i(TAG, "Retrieving Trip from Parcel");

        id = in.readString();
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
        admin = in.readParcelable(User.class.getClassLoader());

        int numOfMembers = in.readInt();
        for (int i = 0; i < numOfMembers; i++) {
            members.add((Member) in.readParcelable(Member.class.getClassLoader()));
        }

        int numOfInvitees = in.readInt();
        for (int i = 0; i < numOfInvitees; i++) {
            invitees.add((Member) in.readParcelable(Member.class.getClassLoader()));
        }
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
        dest.writeString(name);
        dest.writeString(transportation);
        dest.writeString(origin.toString());
        dest.writeString(destination.toString());
        dest.writeLong(dateFrom.getTime());
        dest.writeLong(dateTo.getTime());
        dest.writeByte((byte) (isPrivate ? 1 : 0));

        dest.writeParcelable(admin, flags);

        dest.writeInt(members.size());
        for (Member member : members) {
            dest.writeParcelable(member, flags);
        }

        dest.writeInt(invitees.size());
        for (Member invitee : invitees) {
            dest.writeParcelable(invitee, flags);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Trip)) return false;

        Trip trip = (Trip) o;
        return name.equals(trip.getName()) &&
                transportation.equals(trip.getTransportation()) &&
                isPrivate == trip.isPrivate() &&
                dateFrom.equals(trip.getDateFrom()) &&
                dateTo.equals(trip.getDateTo()) &&
                destination.toString().equals(trip.getDestination().toString()) &&
                origin.toString().equals(trip.getOrigin().toString());
    }

    @Override
    public String toString() {
        return "Name: " + name + "\n" +
                "Private: " + isPrivate + "\n" +
                "Created By: " + admin.getFullName() + "\n" +
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
        return dateFrom.compareTo(other.dateTo) <= 0 && dateTo.compareTo(other.dateFrom) >= 0;
    }

    /**
     * Get the transportation icon based on the transportation type passed
     *
     * @return An icon to be used with the given transportation
     */
    public int getTransportationIconId() {
        switch (transportation) {
            case "Car":
                return R.drawable.ic_car_grey;

            case "Bus":
                return R.drawable.ic_bus_grey;

            default:
                return R.drawable.ic_flight_grey;
        }
    }

    /**
     * @return A simple string representing the origin and destination of this trip
     */
    public String getSimpleCitiesStringRepresentation() {
        return origin.optString("city") + "  · · ·  " + destination.optString("city");
    }

    /**
     * @return A simple string representing the dates of this trip
     */
    public String getSimpleDatesStringRepresentation() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);
        return dateFormat.format(dateFrom) + "  · · ·  " + dateFormat.format(dateTo);
    }

    public void addMember(Member member) {
        if (member.pendingRequest) {
            invitees.add(member);
        } else {
            members.add(member);
        }
    }

    public void addMembers(List<Member> members) {
        for (Member member : members) addMember(member);
    }

    public void removeMember(Member member) {
        if (!member.pendingRequest) {
            members.remove(member);
        } else {
            invitees.remove(member);
        }
    }

    /**
     * Get the members of the Trip excluding the Admin and the Current User
     *
     * @return A list of all the members except for the special cases
     */
    public List<User> getMembersAsUsersExclusive() {
        List<User> users = getMembersAsUsers();
        users.remove(admin);
        users.remove(VoyageUser.currentUser());
        return users;
    }

    public List<User> getMembersAsUsers() {
        List<User> users = new ArrayList<>();
        for (Member member : members) {
            users.add(member.user);
        }
        return users;
    }

    public List<User> getInviteesAsUsers() {
        List<User> users = new ArrayList<>();
        for (Member invitee : invitees) {
            users.add(invitee.user);
        }
        return users;
    }

    public List<Member> getMembers() {
        return members;
    }

    public List<Member> getInvitees() {
        return invitees;
    }

    public List<Member> getAllMembers() {
        List<Member> allMembers = new ArrayList<>();
        allMembers.addAll(members);
        allMembers.addAll(invitees);
        return allMembers;
    }

    public Member getMemberWithUserId(String userId) {
        for (Member member : members) {
            if (member.user.id.equals(userId)) {
                return member;
            }
        }
        return null;
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

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
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

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public User getAdmin() {
        return admin;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getAdminId() {
        return admin.id;
    }
}
