package droidsquad.voyage.util;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

/**
 * Created by Andrew on 1/20/16.
 */
public class ParseApplication extends Application {

    @Override
    public void onCreate()
    {
        super.onCreate();

        // Initialize the singletons so their instances
        // are bound to the application process.
        initSingletons();
    }

    private void initSingletons() {
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(getApplicationContext());
    }
}
