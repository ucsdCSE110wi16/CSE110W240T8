package droidsquad.voyage.UnitTests;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import droidsquad.voyage.model.objects.Member;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.model.objects.User;

import static org.junit.Assert.assertEquals;

/**
 * Created by Zemei on 3/8/2016.
 */
public class TripTest {

    Calendar calendar = Calendar.getInstance();
    Trip trip1, trip2;
    String trip1ToString, trip2ToString;

    @Before
    public void setUpTrip1() {
        calendar.set(2015, 3, 5);
        Date dateFrom = calendar.getTime();
        calendar.set(2015, 3, 20);
        Date dateTo = calendar.getTime();
        boolean isPrivate = false;

        JSONObject origin = new JSONObject();
        try {
            origin.put("address", "11200 Cruscades, France");
            origin.put("city", "Cruscades");
            origin.put("placeId", "ChIJkX_c0jS0sRIRAC1tFiGIBwQ");
        }
        catch(JSONException e) {
            e.printStackTrace();
        }

        JSONObject destination = new JSONObject();
        try {
            destination.put("address", "Gum Spring, VA 23065, USA");
            destination.put("city", "Gum Spring");
            destination.put("placeId", "ChIJyZ5nKs1YsYkR1LBX1JvaNWU");
        }
        catch(JSONException e) {
            e.printStackTrace();
        }

        String name = "Trip Name";
        String transportation = "Plane";

        trip1 = new Trip(name, transportation, origin, destination, isPrivate, dateFrom, dateTo);

        User admin = new User("yDSasmAjGv", "1245749292118566", "Zemei", "Zeng");
        trip1.setAdmin(admin);

        trip1ToString = "Name: " + name + "\n" +
                "Private: " + isPrivate + "\n" +
                "Created By: " + admin.getFullName() + "\n" +
                "Leaving From: " + origin + "\n" +
                "Destination: " + destination + "\n" +
                "Date From: " + dateFrom + "\n" +
                "Date To: " + dateTo + "\n" +
                "Transportation: " + transportation + "\n";
    }

    @Before
    public void setUpTrip2() {
        calendar.set(2015, 3, 15);
        Date dateFrom = calendar.getTime();
        calendar.set(2015, 3, 25);
        Date dateTo = calendar.getTime();
        boolean isPrivate = false;

        JSONObject origin = new JSONObject();
        try {
            origin.put("address", "Wonderland");
            origin.put("city", "Cupertino");
            origin.put("placeId", "ChIJkX_c0jS0sRIRAC1tFiGIBwQ");
        }
        catch(JSONException e) {
            e.printStackTrace();
        }

        JSONObject destination = new JSONObject();
        try {
            destination.put("address", "Wahwah");
            destination.put("city", "Great Fun");
            destination.put("placeId", "ChIJyZ5nKs1YsYkR1LBX1JvaNWU");
        }
        catch(JSONException e) {
            e.printStackTrace();
        }

        String name = "Trip Name2";
        String transportation = "Bus";

        trip2 = new Trip(name, transportation, origin, destination, isPrivate, dateFrom, dateTo);

        User admin = new User("yDSasmAjGv", "1245749292118566", "Zemei", "Zeng");
        trip2.setAdmin(admin);

        trip2ToString = "Name: " + name + "\n" +
                "Private: " + isPrivate + "\n" +
                "Created By: " + admin.getFullName() + "\n" +
                "Leaving From: " + origin + "\n" +
                "Destination: " + destination + "\n" +
                "Date From: " + dateFrom + "\n" +
                "Date To: " + dateTo + "\n" +
                "Transportation: " + transportation + "\n";
    }

    @Test
    public void testOverlap() {
        assertEquals(trip1.overlaps(trip1), true); // check trip overlaps with itself
        assertEquals(trip2.overlaps(trip2), true);

        assertEquals(trip1.overlaps(trip2), true); // check trip overlaps with another trip

        calendar.set(2015, 4, 15); // dateFrom of trip2 is after dateTo of trip1, so they won't overlap
        Date dateFrom = calendar.getTime();
        trip2.setDateFrom(dateFrom);

        calendar.set(2015, 4, 25);
        Date dateTo = calendar.getTime();
        trip2.setDateTo(dateTo);

        assertEquals(trip1.overlaps(trip2), false); // check trips that don't overlap
        assertEquals(trip2.overlaps(trip1), false);
    }

    @Test
    public void testToString() {
        assertEquals(trip1ToString, trip1.toString());
        assertEquals(trip2ToString, trip2.toString());
    }

    @Test
    public void testEquals() {
        assertEquals(trip1.equals(trip1), true); // check that same trips are equal
        assertEquals(trip2.equals(trip2), true);

        assertEquals(trip1.equals(trip2), false); // check that different trips with different fields are not equal
        assertEquals(trip2.equals(trip1), false);

        // trips are not equal if at least 1 field is not equal
        Trip trip3 = new Trip("Different name than Trip1", trip1.getTransportation(), trip1.getOrigin(),
                trip1.getDestination(), trip1.isPrivate(), trip1.getDateFrom(), trip1.getDateTo());
        assertEquals(trip1.equals(trip3), false);

        trip3.setName(trip1.getName()); // now all fields are the same, so the trips are equal
        assertEquals(trip1.equals(trip3), true);
    }

    @Test
    public void testAddTripMembers() {
        User user = new User("123", "ABC", "FirstName", "LastName"); // placeholder object to create Members

        Member m1 = new Member(user, true, 1); // boolean true means user added is Invitee
        trip1.addMember(m1);
        assertEquals(trip1.getInvitees().size(), 1);
        assertEquals(trip1.getMembers().size(), 0);

        Member m2 = new Member(user, true, 0);
        trip1.addMember(m2);
        assertEquals(trip1.getInvitees().size(), 2);
        assertEquals(trip1.getMembers().size(), 0);

        Member m3 = new Member(user, false, 1); // boolean false means user added is Member of trip
        trip1.addMember(m3);
        assertEquals(trip1.getInvitees().size(), 2);
        assertEquals(trip1.getMembers().size(), 1);

        Member m4 = new Member(user, false, 0);
        trip1.addMember(m4);
        assertEquals(trip1.getInvitees().size(), 2);
        assertEquals(trip1.getMembers().size(), 2);

        List<Member> members = trip1.getMembers(); // check the members were added correctly
        assertEquals(members.get(0), m3);
        assertEquals(members.get(1), m4);

        List<Member> invitees = trip1.getInvitees(); // check the invitees were added correctly
        assertEquals(invitees.get(0), m1);
        assertEquals(invitees.get(1), m2);
    }
}
