package droidsquad.voyage.model.objects;

import android.text.format.DateUtils;

/**
 * Class for storing all the data relevant to trip requests
 * to be displayed in the requests activity
 */
public class Request {
    public User user;
    public Trip trip;
    public String memberId;
    public long elapsedTime;
    public boolean isInvitation;

    public String getElapsedTimeString() {
        return (String) DateUtils.getRelativeTimeSpanString(elapsedTime);
    }
}
