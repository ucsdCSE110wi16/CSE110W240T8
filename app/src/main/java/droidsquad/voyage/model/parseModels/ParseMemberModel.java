package droidsquad.voyage.model.parseModels;

import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import droidsquad.voyage.model.objects.Member;

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

    /**
     * Promote the current user from invitee to member for the given trip
     *
     * @param tripId Id of the trip to promote user from
     * @param callback Called on successful promotion or on failure with error string
     */
    public static void promoteCurrentUser(String tripId, final ParseResponseCallback callback) {
        ParseTripModel.getParseTripWithMembers(tripId, new ParseObjectCallback() {
            @Override
            public void onSuccess(ParseObject parseObject) {
                List<ParseObject> parseMembers = parseObject.getList(ParseTripModel.Field.MEMBERS);

                for (ParseObject parseMember : parseMembers) {
                    if (parseMember.getParseUser(Field.USER).equals(ParseUser.getCurrentUser())) {
                        parseMember.put(Field.PENDING_REQUEST, false);
                        parseMember.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    callback.onSuccess();
                                } else {
                                    callback.onFailure(getParseErrorString(e.getCode()));
                                }
                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }


    /**
     * Removes the current user from the members list of the given trip
     *
     * @param tripId Id of the trip to remove the user from
     * @param callback Called on success or failure
     */
    public static void removeCurrentUser(String tripId, final ParseResponseCallback callback) {
        ParseTripModel.getParseTripWithMembers(tripId, new ParseObjectCallback() {
            @Override
            public void onSuccess(final ParseObject parseTrip) {
                List<ParseObject> parseMembers = parseTrip.getList(ParseTripModel.Field.MEMBERS);

                for (ParseObject parseMember : parseMembers) {
                    if (parseMember.getParseUser(Field.USER).equals(ParseUser.getCurrentUser())) {
                        parseTrip.removeAll(ParseTripModel.Field.MEMBERS, Collections.singletonList(parseMember));
                        parseMember.deleteInBackground(new DeleteCallback() {
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
                        break;
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
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
    public static Member getMemberFromParseObject(ParseObject parseMember) throws IllegalStateException {
        Member member = new Member();
        member.user = ParseUserModel.getUserFromParseUser(parseMember.getParseUser(Field.USER));
        member.id = parseMember.getObjectId();
        member.pendingRequest = parseMember.getBoolean(Field.PENDING_REQUEST);
        member.time = parseMember.getLong(Field.TIME);
        return member;
    }

    public static List<Member> getMembersFromParseObjects(List<ParseObject> parseMembers) {
        List<Member> members = new ArrayList<>();

        try {
            for (ParseObject parseMember : parseMembers) {
                members.add(getMemberFromParseObject(parseMember));
            }
        } catch (IllegalStateException e) {
            Log.d(TAG, "Trip has no members");
        }

        return members;
    }
}
