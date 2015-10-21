package org.systemsbiology.baliga.aqx1010.apiclient;

import android.app.Activity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class SendMeasurementTask extends GoogleTokenTask<Void, Integer, Void> {

    private String systemUID;
    private JSONObject measurementObject;

    public SendMeasurementTask(Activity activity, String email, String systemUID,
                               JSONObject measurementObject) {
        super(activity, email);
        this.systemUID = systemUID;
        this.measurementObject = measurementObject;
    }
    protected Void doInBackground(Void... params) {
        try {
            URL url = new URL(String.format(SystemDefaults.SYSTEM_MEASUREMENTS_URL, systemUID));
            JSONObject result = this.postJSONToURL(url, measurementObject);
            Log.d("aqx1010", result.toString());
        } catch (IOException ex) {
            Log.e("aqx1010", "io exception", ex);
        } catch (JSONException ex) {
            Log.e("aqx1010", "json exception", ex);
        }
        return null;
    }
}
