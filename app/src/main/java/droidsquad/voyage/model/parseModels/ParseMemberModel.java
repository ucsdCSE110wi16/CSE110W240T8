package droidsquad.voyage.model.parseModels;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import droidsquad.voyage.model.objects.Member;

public class ParseMemberModel extends ParseModel {
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
     * Creates a ParseMember as invitee from the ParseUser
     *
     * @param parseUser ParseUser to turn into member
     * @return The ParseMember object
     */
    public static ParseObject createMemberFromParseUser(ParseUser parseUser) {
        ParseObject parseMember = new ParseObject(MEMBER_CLASS);
        parseMember.put(Field.USER, parseUser);
        parseMember.put(Field.PENDING_REQUEST, false);
        parseMember.put(Field.TIME, System.currentTimeMillis());
        return parseMember;
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
}
