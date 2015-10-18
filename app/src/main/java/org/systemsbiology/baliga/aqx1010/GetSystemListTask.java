package org.systemsbiology.baliga.aqx1010;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetSystemListTask extends GoogleTokenTask<Void, Integer, List<AqxSystem>> {

    private GetSystemListTaskListener listener;
    public GetSystemListTask(Activity activity, String email, String scope,
                             GetSystemListTaskListener listener) {
        super(activity, email, scope);
        this.listener = listener;
    }
    /**
     * Executes the asynchronous job. This runs when you call execute()
     * on the AsyncTask instance.
     */
    @Override
    protected List<AqxSystem> doInBackground(Void... params) {
        List<AqxSystem> result = new ArrayList<>();
        BufferedReader in = null;
        try {
            //URL url = new URL("http://eric.systemsbiology.net:5000/api/v1/systems");
            URL url = new URL("http://192.168.1.4:5000/api/v1/systems");
            HttpURLConnection conn = getConnection(url);
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            JSONObject json = new JSONObject(buffer.toString());
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
        } catch (IOException e) {
            // The fetchToken() method handles Google-specific exceptions,
            // so this indicates something went wrong at a higher level.
            // TIP: Check for network connectivity before starting the AsyncTask.
            e.printStackTrace();
        } finally {
            if (in != null) try { in.close(); } catch (IOException ex) { }
        }
        return result;
    }

    @Override
    protected void onPostExecute(List<AqxSystem> result) {
        if (listener != null) listener.systemListRetrieved(result);
    }
}
