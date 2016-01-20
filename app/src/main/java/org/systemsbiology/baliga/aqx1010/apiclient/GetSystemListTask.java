package org.systemsbiology.baliga.aqx1010.apiclient;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetSystemListTask extends GoogleTokenTask<Void, Integer, List<AqxSystem>> {

    private GetSystemListTaskListener listener;
    public GetSystemListTask(Activity activity, String email,
                             GetSystemListTaskListener listener) {
        super(activity, email);
        this.listener = listener;
    }
    /**
     * Executes the asynchronous job. This runs when you call execute()
     * on the AsyncTask instance.
     */
    @Override
    protected List<AqxSystem> doInBackground(Void... params) {
        List<AqxSystem> result = new ArrayList<>();
        try {
            URL url = new URL(SystemDefaults.SYSTEMS_LIST_URL);
            JSONObject json = fetchObjectForURL(url);
            if (json == null) return new ArrayList<>(); // result was null -> empty list
            JSONArray arr = json.getJSONArray("systems");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject s = arr.getJSONObject(i);
                String sysname = s.getString("name");
                String sysuid = s.getString("uid");
                String thumbURL = s.getString("thumb_url");
                try {
                    URL fullThumbURL = SystemDefaults.makeImageURL(thumbURL);
                    Log.d("aqx1010", "image URL: " + fullThumbURL.toString());
                    Bitmap thumbnail = BitmapFactory.decodeStream(fullThumbURL.openConnection().getInputStream());
                    thumbnail = GetSystemListTask.getRoundedCornerBitmap(thumbnail, 5);
                    result.add(new AqxSystem(sysname, sysuid, thumbnail));
                } catch (MalformedURLException ex) {
                    Log.e("aqx1010", "URL error", ex);
                } catch (IOException ex) {
                    Log.e("aqx1010", "IO error", ex);
                }
            }
        } catch (JSONException ex) {
            Log.e("aqx1010", "can not parse json", ex);
        } catch (IOException ex) {
            // The fetchToken() method handles Google-specific exceptions,
            // so this indicates something went wrong at a higher level.
            // TIP: Check for network connectivity before starting the AsyncTask.
            Log.e("aqx1010", "unknown io exception", ex);
        }
        return result;
    }

    @Override
    protected void onPostExecute(List<AqxSystem> result) {
        if (listener != null) listener.systemListRetrieved(result);
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }}
