package droidsquad.voyage.model.objects;

import android.text.format.DateUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Member implements Serializable {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy h:mm a", Locale.US);

    public User user;
    public String id;
    public boolean pendingRequest;
    public long time;

    public Member(User user, boolean pendingRequest, long time) {
        this.user = user;
        this.pendingRequest = pendingRequest;
        this.time = time;
    }

    public Member() {
        // Default no-args constructor
    }

    public String getElapsedTimeString() {
        return (String) DateUtils.getRelativeTimeSpanString(time);
    }

    @Override
    public String toString() {
        return String.format("Member: %s - PendingRequest: %s - Time: %s",
                user.getFullName(), pendingRequest, dateFormat.format(new Date(time)));
    }
}
