package droidsquad.voyage.model;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import droidsquad.voyage.model.api.FacebookAPI;

public class TripBroadcastReceiver extends ParsePushBroadcastReceiver {
    private JSONObject data;

    private final static String TAG = TripBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            data = new JSONObject(intent.getExtras().getString("com.parse.Data"));
        } catch (JSONException e) {
            Log.d(TAG, "JSONException occurred while parsing notification's data");
            e.printStackTrace();
        }

        super.onReceive(context, intent);
    }

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        String title = null, alert = null, fbId = null;

        try {
            title = data.getString("title");
            alert = data.getString("alert");
            fbId = data.getString("fbId");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setContentTitle(title)
                        .setContentText(alert)
                        .setSmallIcon(super.getSmallIconId(context, intent));

        if (fbId != null) {
            FacebookAPI.getProfilePic(fbId, "square", new FacebookAPI.ProfilePicCallback() {
                @Override
                public void onCompleted(Bitmap bitmap) {
                    builder.setLargeIcon(bitmap);
                }
            });
        }

        return builder.build();
    }
}
