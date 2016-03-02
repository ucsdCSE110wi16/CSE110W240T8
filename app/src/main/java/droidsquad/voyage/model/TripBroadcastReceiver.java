package droidsquad.voyage.model;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Random;

import droidsquad.voyage.R;
import droidsquad.voyage.model.api.FacebookAPI;
import droidsquad.voyage.model.parseModels.ParseRequestModel;
import droidsquad.voyage.util.BitmapManipulator;
import droidsquad.voyage.util.Constants;
import droidsquad.voyage.view.activity.MainNavDrawerActivity;

public class TripBroadcastReceiver extends ParsePushBroadcastReceiver {
    private final static String TAG = TripBroadcastReceiver.class.getSimpleName();
    private static final String ACTION_REQUEST_ACCEPT = "droidsquad.voyage.intent.ACCEPT_INVITATION";
    private static final String ACTION_REQUEST_DECLINE = "droidsquad.voyage.intent.DECLINE_INVITATION";
    private static final String NOTIFICATION_ID = "notificationId";

    private static JSONObject data;

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case ACTION_REQUEST_ACCEPT:
                onAcceptTripInvitation(context, intent);
                break;
            case ACTION_REQUEST_DECLINE:
                onDeclineTripInvitation(context, intent);
                break;
            default:
                super.onReceive(context, intent);
        }
    }

    @Override
    public void onPushReceive(Context context, Intent intent) {
        try {
            data = new JSONObject(intent.getStringExtra(KEY_PUSH_DATA));
            Log.d(TAG, "Notification received with data: " + data);
            sendNotification(context, intent);
        } catch (JSONException e) {
            Log.d(TAG, "JSONException occurred while parsing notification's data: ", e);
        }
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        Log.d(TAG, "Notification clicked");
        // Send a Parse Analytics "push opened" event
        ParseAnalytics.trackAppOpenedInBackground(intent);
        startActivities(context, intent);
    }

    private void onAcceptTripInvitation(final Context context, final Intent intent) {
        ParseRequestModel.acceptRequest(data.optString("tripId"), new ParseRequestModel.ParseResponseCallback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "Accepted trip from notification");
                dismissNotification(context, intent.getIntExtra(NOTIFICATION_ID, 0));
            }

            @Override
            public void onFailure(String error) {
                Log.i(TAG, "Error while accepting trip from notification: " + error);
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onDeclineTripInvitation(final Context context, final Intent intent) {
        ParseRequestModel.declineRequest(data.optString("tripId"), new ParseRequestModel.ParseResponseCallback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "Declined trip from notification");
                dismissNotification(context, intent.getIntExtra(NOTIFICATION_ID, 0));
            }

            @Override
            public void onFailure(String error) {
                Log.i(TAG, "Error while declining trip from notification: " + error);
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Starts the activities given the type of this notification
     */
    private void startActivities(Context context, Intent intent) {
        Class<? extends Activity> activity;
        Bundle extras = intent.getExtras();

        switch (data.optString("type")) {
            case Constants.NOTIFICATION_INVITATION:
                activity = MainNavDrawerActivity.class;
                extras.putString(Constants.KEY_FRAGMENT_MAIN_ACTIVITY, Constants.FRAGMENT_REQUESTS);
                break;

            default:
                activity = super.getActivity(context, intent);
        }

        Intent activityIntent = new Intent(context, activity);
        activityIntent.putExtras(extras);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(activity);
        stackBuilder.addNextIntent(activityIntent);
        stackBuilder.startActivities();
    }

    /**
     * Send the notification to the user
     */
    private void sendNotification(final Context context, Intent intent) {
        String title = data.optString("title");
        String alert = data.optString("alert");
        String fbId = data.optString("fbId");
        String tickerText = String.format(Locale.getDefault(), "%s: %s", title, alert);

        // Pick an id that probably won't overlap anything
        final int notificationId = (int) System.currentTimeMillis();

        Bundle extras = intent.getExtras();
        extras.putInt(NOTIFICATION_ID, notificationId);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(title)
                .setContentText(alert)
                .setTicker(tickerText)
                .setSmallIcon(super.getSmallIconId(context, intent))
                .setContentIntent(getPendingIntent(context, ACTION_PUSH_OPEN, extras))
                .setDeleteIntent(getPendingIntent(context, ACTION_PUSH_DELETE, extras))
                .addAction(getAction(context, ACTION_REQUEST_ACCEPT, extras))
                .addAction(getAction(context, ACTION_REQUEST_DECLINE, extras))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setDefaults(Notification.DEFAULT_ALL);

        if (fbId != null) {
            FacebookAPI.getProfilePicAsync(fbId, "normal", new FacebookAPI.ProfilePicCallback() {
                @Override
                public void onCompleted(Bitmap bitmap) {
                    bitmap = BitmapManipulator.getRoundedBitmap(bitmap);
                    bitmap = BitmapManipulator.getScaledBitmap(context, bitmap);
                    builder.setLargeIcon(bitmap);
                    fireNotification(context, builder.build(), notificationId);
                }
            });
        } else {
            fireNotification(context, builder.build(), notificationId);
        }
    }

    private NotificationCompat.Action getAction(Context context, String actionType, Bundle extras) {
        PendingIntent actionIntent = getPendingIntent(context, actionType, extras);
        return new NotificationCompat.Action.Builder(
                getActionIcon(actionType), getActionText(actionType), actionIntent).build();
    }

    private String getActionText(String actionType) {
        switch (actionType) {
            case ACTION_REQUEST_ACCEPT:
                return "Accept";
            case ACTION_REQUEST_DECLINE:
                return "Decline";
            default:
                return "";
        }
    }

    private int getActionIcon(String actionType) {
        switch (actionType) {
            case ACTION_REQUEST_ACCEPT:
                return R.drawable.ic_check;
            case ACTION_REQUEST_DECLINE:
                return R.drawable.ic_close_black;
            default:
                return 0;
        }
    }

    /**
     * Return the pending intent corresponding to the given context and intentCode
     *
     * @param context    Context to get set on the intent
     * @param actionType Action type to be launched by the intent
     * @param extras     The extras to put on this intent @Nullable
     * @return A pending intent with the intent from the intent code
     */
    private PendingIntent getPendingIntent(Context context, String actionType, @Nullable Bundle extras) {
        String packageName = context.getPackageName();

        Random random = new Random();
        int intentRequestCode = random.nextInt();

        Intent intent = new Intent(actionType);
        intent.setPackage(packageName);

        if (extras != null) {
            intent.putExtras(extras);
        }

        return PendingIntent.getBroadcast(context, intentRequestCode,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Fires off a notification to the user
     *
     * @param context      The context in which to send the notification
     * @param notification The notification to be sent
     */
    private void fireNotification(Context context, Notification notification, int notificationId) {
        // Fire off the notification
        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        try {
            nm.notify(notificationId, notification);
        } catch (SecurityException e) {
            // Some phones throw an exception for unapproved vibration
            notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND;
            nm.notify(notificationId, notification);
        }
    }

    /**
     * Dismiss the notification with the given id
     *
     * @param context The context in which the notification lives
     * @param id The id of the notification to be dismissed
     */
    private void dismissNotification(Context context, int id) {
        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(id);
    }
}
