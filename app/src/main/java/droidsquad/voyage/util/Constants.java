package droidsquad.voyage.util;

public class Constants {
    public static final int DEFAULT_TRIP_LENGTH = 7;
    public static final String ERROR_UNKNOWN = "Something went wrong. Please try again in a while.";
    public static final String ERROR_NO_INTERNET_CONNECTION = "No connection";
    public static final String UPDATE_TRIP = "Update";

    // Intent Keys
    public static final String KEY_FRAGMENT_MAIN_ACTIVITY = "FRAGMENT_MAIN_ACTIVITY";

    public static final String FRAGMENT_REQUESTS = "FRAGMENT_REQUESTS";
    public static final String FRAGMENT_SETTINGS = "FRAGMENT_SETTINGS";
    public static final String FRAGMENT_FEED = "FRAGMENT_FEED";

    // Request Codes
    public static final int REQUEST_CODE_TRIP_ACTIVITY = 9001;
    public static final int REQUEST_CODE_CREATE_TRIP_ACTIVITY = 9002;
    public static final int REQUEST_CODE_ADD_FRIENDS_ACTIVITY = 9003;

    // Result Codes
    public static final int RESULT_CODE_TRIP_DELETED = 5001;
    public static final int RESULT_CODE_TRIP_LEFT = 5002;
    public static final int RESULT_CODE_TRIP_UPDATED = 5003;
    public static final int RESULT_CODE_TRIP_CREATED = 5004;
    public static final int RESULT_CODE_INVITEES_ADDED = 5005;

    // Notification types
    public static final String NOTIFICATION_INVITATION = "NOTIFICATION_INVITATION";
    public static final String NOTIFICATION_REQUEST = "NOTIFICATION_REQUEST";
}
