package org.systemsbiology.baliga.aqx1010;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A background task that runs in the background and retrieves a token.
 * We can use this as a base class for all classes that
 */
public abstract class GoogleTokenTask<ParamType, ProgressType, RetType>
        extends AsyncTask<ParamType, ProgressType, RetType> {

    private Activity mActivity;
    private String mScope;
    private String mEmail;

    public GoogleTokenTask(Activity activity, String email, String scope) {
        this.mActivity = activity;
        this.mScope = scope;
        this.mEmail = email;
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

    protected HttpURLConnection getConnection(URL url) throws IOException {
        String token = fetchToken();
        if (token != null) {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", String.format("Bearer %s", token));
            return conn;
        }
        return null;
    }
}

