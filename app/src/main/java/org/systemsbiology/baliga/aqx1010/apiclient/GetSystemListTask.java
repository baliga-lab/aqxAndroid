package org.systemsbiology.baliga.aqx1010.apiclient;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
                Log.d("aqx1010", String.format("System name: %s uid: %s", sysname, sysuid));
                result.add(new AqxSystem(sysname, sysuid));
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
}
