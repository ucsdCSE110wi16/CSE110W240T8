package droidsquad.voyage.model;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class TripBroadcastReceiver extends ParsePushBroadcastReceiver {
    private final static String TAG = TripBroadcastReceiver.class.getSimpleName();

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(intent.getExtras().getString("com.parse.Data"));
        } catch (JSONException e) {
            Log.d(TAG, "JSONException occurred while parsing notification's data");
            e.printStackTrace();
        }

        Log.d(TAG, "Received data from push notification, data: " + jsonObject);
        super.onPushReceive(context, intent);
    }

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        return super.getNotification(context, intent);
    }

    @Override
    protected Class<? extends Activity> getActivity(Context context, Intent intent) {
        return super.getActivity(context, intent);
    }

    @Override
    protected Bitmap getLargeIcon(Context context, Intent intent) {
        return super.getLargeIcon(context, intent);
    }
}
