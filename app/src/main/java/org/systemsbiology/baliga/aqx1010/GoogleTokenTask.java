package org.systemsbiology.baliga.aqx1010;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A background task that runs in the background and retrieves a token.
 * We can use this as a base class for all classes that
 */
public class GoogleTokenTask extends AsyncTask<Void, Integer, Void> {
    private Activity mActivity;
    private String mScope;
    private String mEmail;

    public GoogleTokenTask(Activity activity, String email, String scope) {
        this.mActivity = activity;
        this.mScope = scope;
        this.mEmail = email;
    }

    /**
     * Executes the asynchronous job. This runs when you call execute()
     * on the AsyncTask instance.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            String token = fetchToken();
            if (token != null) {
                // **Insert the good stuff here.**
                // Use the token to access the user's Google data.
                Log.d("aqx1010", "Token: " + token);
                URL url = new URL("http://eric.systemsbiology.net:5000/api/v1/api-test");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", String.format("Bearer %s", token));
                BufferedReader in = null;
                try {
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder buffer = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        buffer.append(line);
                    }
                    Log.d("aqx1010", "Data: " + buffer.toString());
                } finally {
                    if (in != null) in.close();
                }
            }
        } catch (IOException e) {
            // The fetchToken() method handles Google-specific exceptions,
            // so this indicates something went wrong at a higher level.
            // TIP: Check for network connectivity before starting the AsyncTask.
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets an authentication token from Google and handles any
     * GoogleAuthException that may occur.
     */
    protected String fetchToken() throws IOException {
        try {
            return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
        } catch (UserRecoverableAuthException userRecoverableException) {
            // GooglePlayServices.apk is either old, disabled, or not present
            // so we need to show the user some UI in the activity to recover.
            // TODO: complain about old version

        } catch (GoogleAuthException fatalException) {
            // Some other type of unrecoverable exception has occurred.
            // Report and log the error as appropriate for your app.
            fatalException.printStackTrace();
        }
        return null;
    }
}

