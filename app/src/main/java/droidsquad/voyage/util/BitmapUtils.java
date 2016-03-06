package droidsquad.voyage.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class has a bunch of utility functions for manipulating bitmaps
 * All methods are static since it does not maintain state
 */
public class BitmapUtils {
    private static final String TAG = BitmapUtils.class.getSimpleName();

    public static void getBitmapAsync(final String url, final BitmapCallback callback) {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    URL imageURL = new URL(url);
                    return BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    Log.d(TAG, "Malformed Exception occurred: " + e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG, "IOException occurred: " + e.getMessage());
                }

                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    callback.done(bitmap, null);
                } else {
                    callback.done(null, "Could not load the image");
                }
            }
        }.execute();
    }

    /**
     * Get a scaled version of bitmap that scales to fit the metrics of the device
     *
     * @param context The context to get the metrics from
     * @param bitmap  The bitmap to be scaled
     * @return The scaled bitmap
     */
    public static Bitmap getScaledBitmap(Context context, Bitmap bitmap) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return Bitmap.createScaledBitmap(bitmap,
                bitmap.getScaledWidth(metrics),
                bitmap.getScaledHeight(metrics),
                false);
    }

    /**
     * Get a round version of the Bitmap
     *
     * @param bitmap Bitmap to get a round version from
     * @return The rounded bitmap
     */
    public static Bitmap getRoundedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();
        return output;
    }

    public interface BitmapCallback {
        void done(Bitmap bitmap, String error);
    }
}
