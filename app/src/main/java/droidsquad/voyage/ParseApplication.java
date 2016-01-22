package droidsquad.voyage;

import android.app.Application;

import com.parse.Parse;

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
    }
}
