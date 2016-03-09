package droidsquad.voyage.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

/**
 * Used to create app-wide app alerts, for example WiFi checks
 */
public class NetworkAlerts {

    private static WifiManager wifiManager = null;

    public static void setContext(Context context) {
        initWifiManager(context);
    }

    /**
     * Initializes the WifiManager used to obtain the MAC and IP Address
     *
     * @param context context of the device, cannot be null
     */
    private static void initWifiManager(Context context){
        if(context == null){
            throw new IllegalArgumentException();
        }
        wifiManager= (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * Checks if the network is currently available, used from attemptLogin()
     * to prevent login attempts with no network connection
     *
     * @return boolean of whether the network is available or not
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        // if no network is available networkInfo will be null
        if (networkInfo != null && networkInfo.isAvailable()
                && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * Specialized alert popup for LoginActivity, for generic error codes which only are dismissed,
     * or network alerts, which provide a link to wifi settings
     *
     */
    public static void showNetworkAlert(final Context context){
        if(context == null){
            return;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            alertDialogBuilder.setTitle("No Network Connection");
            alertDialogBuilder.setMessage("Make sure you are online and check again");

            // Create the button for going to Wifi Settings
            alertDialogBuilder.setPositiveButton("WiFi Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.cancel();
                    context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });

            // Create the button for dismissing the popup
            alertDialogBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.cancel();
                }
            });

        // Display the alertDialog
        final Dialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);

        alertDialog.show();

    }
}
