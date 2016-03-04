package droidsquad.voyage.model.objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Member implements Parcelable {
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

    protected Member(Parcel in) {
        user = in.readParcelable(User.class.getClassLoader());
        id = in.readString();
        pendingRequest = in.readByte() != 0;
        time = in.readLong();
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    public String getElapsedTimeString() {
        return (String) DateUtils.getRelativeTimeSpanString(time);
    }

    @Override
    public String toString() {
        return String.format("Member: %s - PendingRequest: %s - Time: %s",
                user.getFullName(), pendingRequest, dateFormat.format(new Date(time)));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(user, flags);
        dest.writeString(id);
        dest.writeByte((byte) (pendingRequest ? 1 : 0));
        dest.writeLong(time);
    }
}
