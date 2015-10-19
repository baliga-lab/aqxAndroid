package org.systemsbiology.baliga.aqx1010.apiclient;

/**
 * Created by weiju on 10/19/15.
 */
public class SystemDefaults {
    //public static final String CLIENT_ID = "75692667349-b1pb7e4fh5slptq3allb93dvbtbfpjda.apps.googleusercontent.com";
    public static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    public static final int REQUEST_CODE_PICK_ACCOUNT = 1000;

    public static final String SYSTEMS_LIST_URL = "http://eric.systemsbiology.net:5000/api/v1.0/systems";
    public static final String SYSTEM_DETAILS_URL = "http://eric.systemsbiology.net:5000/api/v1.0/system-details/%s";
    //public static final String SYSTEMS_LIST_URL = "http://192.168.1.4:5000/api/v1.0/systems";

}
