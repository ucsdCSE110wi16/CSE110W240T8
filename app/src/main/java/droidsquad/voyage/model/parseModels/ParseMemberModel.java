package droidsquad.voyage.model.parseModels;

import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import droidsquad.voyage.model.objects.Member;
import droidsquad.voyage.model.objects.VoyageUser;

public class ParseMemberModel extends ParseModel {
    private static final String TAG = ParseMemberModel.class.getSimpleName();
    protected static final String MEMBER_CLASS = "Member";

    protected interface Field {
        String ID = "objectId";
        String USER = "user";
        String PENDING_REQUEST = "pendingRequest";
        String TIME = "time";
    }

    /**
     * This method should be called once a user accepts or is accepted into a Trip Promotes a user
     * currently in Invitees stage into a member.
     *
     * @param memberId The id of the member to be promoted from invitee
     * @param callback Called on success or failure
     */
    public static void promoteInvitee(String memberId, final ParseResponseCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(MEMBER_CLASS);
        query.getInBackground(memberId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseMember, ParseException e) {
                if (e == null) {
                    parseMember.put(Field.PENDING_REQUEST, false);
                    parseMember.put(Field.TIME, System.currentTimeMillis());
                    parseMember.saveInBackground();
                    callback.onSuccess();
                } else {
                    callback.onFailure(getParseErrorString(e.getCode()));
                }
            }
        });
    }

    public static void promoteCurrentUser(String tripId, final ParseResponseCallback callback) {
        ParseQuery<ParseObject> memberQuery = ParseQuery.getQuery(MEMBER_CLASS);
        memberQuery.whereEqualTo(Field.USER, ParseUser.getCurrentUser());

        ParseQuery<ParseObject> tripQuery = ParseQuery.getQuery(ParseTripModel.TRIP_CLASS);
        tripQuery.whereMatchesQuery(ParseTripModel.Field.MEMBERS, memberQuery);
        tripQuery.include(ParseTripModel.Field.MEMBERS);

        tripQuery.getInBackground(tripId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseTrip, ParseException e) {
                if (e == null) {
                    ParseObject parseMember = (ParseObject) parseTrip.getList(ParseTripModel.Field.MEMBERS).get(0);
                    parseMember.put(Field.PENDING_REQUEST, false);
                    parseMember.saveInBackground();
                    callback.onSuccess();
                } else {
                    Log.d(TAG, "Exception while promoting trip member", e);
                    callback.onFailure(getParseErrorString(e.getCode()));
                }
            }
        });
    }

    public static void removeCurrentUser(String tripId, final ParseResponseCallback callback) {
        ParseQuery<ParseObject> memberQuery = ParseQuery.getQuery(MEMBER_CLASS);
        memberQuery.whereEqualTo(Field.USER, ParseUser.getCurrentUser());

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseTripModel.TRIP_CLASS);
        query.whereMatchesQuery(ParseTripModel.Field.MEMBERS, memberQuery);
        query.include(ParseTripModel.Field.MEMBERS);

        query.getInBackground(tripId, new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject parseTrip, ParseException e) {
                if (e == null) {
                    ParseObject member = (ParseObject) parseTrip.getList(ParseTripModel.Field.MEMBERS).get(0);
                    parseTrip.removeAll(ParseTripModel.Field.MEMBERS, Collections.singletonList(member));
                    member.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                parseTrip.saveInBackground();
                                callback.onSuccess();
                            } else {
                                callback.onFailure(getParseErrorString(e.getCode()));
                            }
                        }
                    });
                } else {
                    callback.onFailure(getParseErrorString(e.getCode()));
                }
            }
        });
    }

    /**
     * Creates a ParseMember as invitee from the ParseUser
     * OBS: Pending request is set to true
     *
     * @param parseUser ParseUser to turn into member
     * @return The ParseMember object
     */
    public static ParseObject createMemberFromParseUser(ParseUser parseUser) {
        ParseObject parseMember = new ParseObject(MEMBER_CLASS);
        parseMember.put(Field.USER, parseUser);
        parseMember.put(Field.PENDING_REQUEST, true);
        parseMember.put(Field.TIME, System.currentTimeMillis());
        return parseMember;
    }

    /**
     * Creates a ParseMember as invitee for each of the ParseUsers
     *
     * @param parseUsers The ParseUsers to create ParseMembers from
     * @return List containing all the created ParseMembers
     */
    public static List<ParseObject> createMembersFromParseUsers(List<ParseUser> parseUsers) {
        List<ParseObject> members = new ArrayList<>();
        for (ParseUser parseUser : parseUsers) {
            members.add(createMemberFromParseUser(parseUser));
        }
        return members;
    }

    /**
     * Get the Parse Member Object from the Member Object
     *
     * @param member   The member object to get the ParseMember from
     * @param callback Called on success or failure
     */
    public static void getParseObjectFromMember(final Member member, final ParseObjectCallback callback) {
        ParseUserModel.getParseUserFromUser(member.user, new ParseUserModel.ParseUserCallback() {
            @Override
            public void onSuccess(ParseUser parseUser) {
                ParseObject parseMember = new ParseObject(MEMBER_CLASS);
                parseMember.put(Field.USER, parseUser);
                parseMember.put(Field.PENDING_REQUEST, member.pendingRequest);
                parseMember.put(Field.TIME, member.time);
                callback.onSuccess(parseMember);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Constructs a Member object from the ParseMember's information
     *
     * @param parseMember ParseMember to get the information from
     * @return A Member object with the info from ParseMember
     */
    public static Member getMemberFromParseObject(ParseObject parseMember) {
        Member member = new Member();
        member.id = parseMember.getObjectId();
        member.user = ParseUserModel.getUserFromParseUser(parseMember.getParseUser(Field.USER));
        member.pendingRequest = parseMember.getBoolean(Field.PENDING_REQUEST);
        member.time = parseMember.getLong(Field.TIME);
        return member;
    }

    public static List<Member> getMembersFromParseObjects(List<ParseObject> parseMembers) {
        List<Member> members = new ArrayList<>();
        for (ParseObject parseMember : parseMembers) {
            members.add(getMemberFromParseObject(parseMember));
        }
        return members;
    }
}
