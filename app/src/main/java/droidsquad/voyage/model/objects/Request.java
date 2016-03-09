package droidsquad.voyage.model.objects;

import android.text.format.DateUtils;

/**
 * Class for storing all the data relevant to trip requests
 * to be displayed in the requests activity
 */
public class Request {
    public User user;
    public Trip trip;
    public String id;
    public long elapsedTime;
    public boolean isInvitation;

    public String getElapsedTimeString() {
        if (System.currentTimeMillis() - elapsedTime < (60 * 1000)) {
            return "Just Now";
        } else {
            return (String) DateUtils.getRelativeTimeSpanString(elapsedTime);
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Request && id.equals(((Request) o).id);
    }
}
