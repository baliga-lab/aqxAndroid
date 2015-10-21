package org.systemsbiology.baliga.aqx1010.apiclient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SystemDefaults {
    //public static final String BASE_URL = "https://aquaponics.systemsbiology.net";
    public static final String BASE_URL = "http://eric.systemsbiology.net:5000";

    //public static final String CLIENT_ID = "75692667349-b1pb7e4fh5slptq3allb93dvbtbfpjda.apps.googleusercontent.com";
    public static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    public static final int REQUEST_CODE_PICK_ACCOUNT = 1000;

    public static final String SYSTEMS_LIST_URL = BASE_URL + "/api/v1.0/systems";
    public static final String SYSTEM_INFO_URL = BASE_URL + "/api/v1.0/system/%s";
    public static final String SYSTEM_MEASUREMENTS_URL = BASE_URL + "/api/v1.0/measurements/%s";

    public static final DateFormat API_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
    public static final DateFormat API_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static final DateFormat UI_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
}
