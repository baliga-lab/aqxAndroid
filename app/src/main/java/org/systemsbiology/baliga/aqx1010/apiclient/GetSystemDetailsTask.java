package org.systemsbiology.baliga.aqx1010.apiclient;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
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
            Date creationDate = new Date(); // TODO
            return new AqxSystemDetails(details.getString("name"), creationDate);
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
