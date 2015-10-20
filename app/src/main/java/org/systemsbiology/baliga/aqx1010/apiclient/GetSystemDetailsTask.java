package org.systemsbiology.baliga.aqx1010.apiclient;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetSystemDetailsTask extends GoogleTokenTask<Void, Integer, AqxSystemDetails> {

    private GetSystemDetailsTaskListener listener;
    private String systemUID;

    public GetSystemDetailsTask(Activity activity, String email, String systemUID,
                             GetSystemDetailsTaskListener listener) {
        super(activity, email);
        this.listener = listener;
        this.systemUID = systemUID;
    }

    @Override
    @Nullable
    protected AqxSystemDetails doInBackground(Void... params) {
        try {
            URL url = new URL(String.format(SystemDefaults.SYSTEM_DETAILS_URL, systemUID));
            JSONObject json = fetchObjectForURL(url);
            if (json == null) return null; // return null -> JSON object was not retrieved
            JSONObject details = json.getJSONObject("system_details");
            Date creationTime = new Date(); // TODO
            Date startDate = null;

            try {
                creationTime = SystemDefaults.API_DATE_TIME_FORMAT.parse(details.getString("creation_time")); // TODO
            } catch (ParseException ex) {
                Log.e("aqx1010", "parse date error", ex);
            }

            if (details.getString("start_date").length() > 0) {
                try {
                    startDate = SystemDefaults.API_DATE_FORMAT.parse(details.getString("start_date")); // TODO
                } catch (ParseException ex) {
                    Log.e("aqx1010", "parse date error", ex);
                }
            }
            JSONArray organismsJSON = details.getJSONArray("aquatic_organisms");
            JSONArray cropsJSON = details.getJSONArray("crops");
            NameAndCount[] organisms = new NameAndCount[organismsJSON.length()];
            NameAndCount[] crops = new NameAndCount[cropsJSON.length()];
            for (int i = 0; i < organismsJSON.length(); i++) {
                JSONObject obj = organismsJSON.getJSONObject(i);
                String name = obj.names().getString(0);
                organisms[i] = new NameAndCount(name, obj.getInt(name));
            }
            for (int i = 0; i < cropsJSON.length(); i++) {
                JSONObject obj = cropsJSON.getJSONObject(i);
                String name = obj.names().getString(0);
                crops[i] = new NameAndCount(name, obj.getInt(name));
            }

            return new AqxSystemDetails(details.getString("name"), creationTime, startDate,
                    details.getString("aqx_technique"), organisms, crops);
        } catch (JSONException ex) {
            Log.e("aqx1010", "can not parse json", ex);
        } catch (IOException ex) {
            // The fetchToken() method handles Google-specific exceptions,
            // so this indicates something went wrong at a higher level.
            // TIP: Check for network connectivity before starting the AsyncTask.
            Log.e("aqx1010", "unknown io exception", ex);
        }
        return null;
    }
    @Override
    protected void onPostExecute(AqxSystemDetails result) {
        if (listener != null) listener.detailsRetrieved(result);
    }
}
