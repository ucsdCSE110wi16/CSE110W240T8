package droidsquad.voyage.util;

public class Constants {
    public static final String EXAMPLE_STRING = "example";
    public static final int DEFAULT_TRIP_LENGTH = 7;
    public static final String ERROR_UNKNOWN = "Something went wrong. Please try again in a while.";
    public static final String ERROR_NO_INTERNET_CONNECTION = "No connection";
    public static final String FB_PICTURE_URL = "https://graph.facebook.com/%s/picture?type=%s";

    // Request Codes
    public static final int REQUEST_CODE_TRIP_ACTIVITY = 9001;
    public static final int REQUEST_CODE_CREATE_TRIP_ACTIVITY = 9002;

    // Result Codes
    public static final int RESULT_CODE_TRIP_DELETED = 5001;
    public static final int RESULT_CODE_TRIP_LEFT = 5002;
    public static final int RESULT_CODE_TRIP_CREATED = 5003;

    // Parse Related constants
    public static final String PARSE_RELATION_MEMBERS = "members";
    public static final String PARSE_RELATION_INVITEES = "invitees";
}