package droidsquad.voyage.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Random;

import droidsquad.voyage.model.api.FacebookAPI;
import droidsquad.voyage.util.Constants;
import droidsquad.voyage.view.activity.MainNavDrawerActivity;

public class TripBroadcastReceiver extends ParsePushBroadcastReceiver {
    private JSONObject data;

    private final static String TAG = TripBroadcastReceiver.class.getSimpleName();

    @Override
    public void onPushReceive(Context context, Intent intent) {
        try {
            data = new JSONObject(intent.getStringExtra("com.parse.Data"));
            Log.d(TAG, "Notification received with data: " + data);
        } catch (JSONException e) {
            Log.d(TAG, "JSONException occurred while parsing notification's data: ", e);
        }

        sendNotification(context, intent);
    }

    /**
     * Send the notification to the user
     */
    protected Notification sendNotification(final Context context, Intent intent) {
        String title = data.optString("title");
        String alert = data.optString("alert");
        String fbId = data.optString("fbId");
        String tickerText = String.format(Locale.getDefault(), "%s: %s", title, alert);

        Bundle extras = intent.getExtras();
        String packageName = context.getPackageName();

        Random random = new Random();
        int contentIntentRequestCode = random.nextInt();
        int deleteIntentRequestCode = random.nextInt();

        Intent contentIntent = new Intent(context, MainNavDrawerActivity.class);
        contentIntent.putExtras(extras);
        contentIntent.putExtra(Constants.KEY_FRAGMENT_MAIN_ACTIVITY, Constants.FRAGMENT_REQUESTS);
        contentIntent.setPackage(packageName);

        Intent deleteIntent = new Intent("com.parse.push.intent.DELETE");
        deleteIntent.putExtras(extras);
        deleteIntent.setPackage(packageName);

        PendingIntent pContentIntent = PendingIntent.getBroadcast(context, contentIntentRequestCode,
                contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pDeleteIntent = PendingIntent.getBroadcast(context, deleteIntentRequestCode,
                deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(title)
                .setContentText(alert)
                .setTicker(tickerText)
                .setSmallIcon(super.getSmallIconId(context, intent))
                .setContentIntent(pContentIntent)
                .setDeleteIntent(pDeleteIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);

        if (fbId != null) {
            FacebookAPI.getProfilePicAsync(fbId, "normal", new FacebookAPI.ProfilePicCallback() {
                @Override
                public void onCompleted(Bitmap bitmap) {
                    float multiplier = getImageFactor(context.getResources());

                    builder.setLargeIcon(Bitmap.createScaledBitmap(
                            bitmap,
                            (int)(bitmap.getWidth() * multiplier),
                            (int)(bitmap.getHeight() * multiplier),
                            false));

                    fireNotification(context, builder.build());
                }
            });
        }

        return null;
    }

    /**
     * Fires off a notification to the user
     *
     * @param context      The context in which to send the notification
     * @param notification The notification to be sent
     */
    private void fireNotification(Context context, Notification notification) {
        // Fire off the notification
        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Pick an id that probably won't overlap anything
        int notificationId = (int) System.currentTimeMillis();

        try {
            nm.notify(notificationId, notification);
        } catch (SecurityException e) {
            // Some phones throw an exception for unapproved vibration
            notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND;
            nm.notify(notificationId, notification);
        }
    }

    private static float getImageFactor(Resources r) {
        DisplayMetrics metrics = r.getDisplayMetrics();
        return metrics.density / 3f;
    }
}
