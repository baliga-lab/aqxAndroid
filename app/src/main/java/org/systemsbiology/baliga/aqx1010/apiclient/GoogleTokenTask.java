package org.systemsbiology.baliga.aqx1010.apiclient;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * A background task that runs in the background and retrieves a token.
 * We can use this as a base class for all classes that
 */
public abstract class GoogleTokenTask<ParamType, ProgressType, RetType>
        extends AsyncTask<ParamType, ProgressType, RetType> {

    private Activity mActivity;
    private String mScope;
    private String mEmail;

    public GoogleTokenTask(Activity activity, String email) {
        this.mActivity = activity;
        this.mScope = SystemDefaults.SCOPE;
        this.mEmail = email;
    }

    /**
     * Common function that returns the JSON object for the specified API call.
     * @param url the API URL to call
     * @return the JSON object
     * @throws IOException thrown if reading from URL fails
     * @throws JSONException thrown if parsing the JSON object fails
     */
    @Nullable
    protected JSONObject fetchObjectForURL(URL url) throws IOException, JSONException {
        HttpURLConnection conn = getConnection(url);
        BufferedReader in = null;
        if (conn == null) return null;
        try {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            return new JSONObject(buffer.toString());
        } finally {
            if (in != null) try { in.close(); } catch (IOException ex) {
                Log.e("aqx1010", "can not close input stream", ex);
            }
        }

    }

    protected JSONObject postJSONToURL(URL url, JSONObject obj) throws IOException, JSONException {
        HttpURLConnection conn = getConnection(url);
        if (conn != null) conn.setRequestMethod("POST");
        BufferedReader in = null;
        OutputStreamWriter out = null;
        if (conn == null) return null;
        try {
            out = new OutputStreamWriter(conn.getOutputStream());
            out.write(obj.toString());
            out.flush();

            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            return new JSONObject(buffer.toString());
        } finally {
            if (in != null) try { in.close(); } catch (IOException ex) {
                Log.e("aqx1010", "can not close input stream", ex);
            }
        }

    }


    /**
     * Gets an authentication token from Google and handles any
     * GoogleAuthException that may occur.
     */
    @Nullable
    private String fetchToken() throws IOException {
        try {
            return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
        } catch (UserRecoverableAuthException ex) {
            // GooglePlayServices.apk is either old, disabled, or not present
            // so we need to show the user some UI in the activity to recover.
            // TODO: complain about old version
            Log.e("aqx1010", "required Google Play version not found", ex);
        } catch (GoogleAuthException fatalException) {
            // Some other type of unrecoverable exception has occurred.
            // Report and log the error as appropriate for your app.
            Log.e("aqx1010", "fatal authorization exception", fatalException);
        }
        return null;
    }

    @Nullable
    private HttpURLConnection getConnection(URL url) throws IOException {
        String token = fetchToken();
        if (token != null) {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Authorization", String.format("Bearer %s", token));
            return conn;
        }
        return null;
    }

    /**
     * Determine whether the device is online.
     * @param context context object
     * @return true if connected, false if not
     */
    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Retrieves the email string stored in local device storage.
     *
     * @param context Context object
     * @return stored email string
     * @throws IOException if email string could not be retrieved
     */
    public static String storedEmail(Context context) throws IOException {
        FileInputStream fis = context.openFileInput("email");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(fis));
            return in.readLine();
        } finally {
            if (in != null) try { in.close(); } catch (IOException ex) {
                Log.e("aqx1010", "could not close email file", ex);
            }
        }

    }

    /**
     * Store the specified email string into local device storage.
     * @param context context object
     * @param email email string
     * @throws IOException if storing the email string failed
     */
    public static void storeEmail(Context context, String email) throws IOException {
        if (email == null) return;
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput("email", Context.MODE_PRIVATE);
            fos.write(email.getBytes());
        } finally {
            if (fos != null) try { fos.close(); } catch (IOException ex) {
                Log.e("aqx1010", "can't close output file", ex);
            }
        }
    }
}
