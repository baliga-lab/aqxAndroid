package org.systemsbiology.baliga.aqx1010.apiclient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SystemDefaults {
    // Official measurement types used by the API
    public static final String API_MEASURE_TYPE_LIGHT = "light";
    public static final String API_MEASURE_TYPE_TEMP = "temp";
    public static final String API_MEASURE_TYPE_DIO = "o2";
    public static final String API_MEASURE_TYPE_PH = "ph";
    public static final String API_MEASURE_TYPE_AMMONIUM = "ammonium";
    public static final String API_MEASURE_TYPE_NITRATE = "nitrate";

    //public static final String BASE_URL = "https://aquaponics.systemsbiology.net";
    public static final String BASE_URL = "http://eric.systemsbiology.net:5000";

    //public static final String CLIENT_ID = "75692667349-b1pb7e4fh5slptq3allb93dvbtbfpjda.apps.googleusercontent.com";
    public static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    public static final int REQUEST_CODE_PICK_ACCOUNT = 1000;

    public static final String SYSTEMS_LIST_URL = BASE_URL + "/api/v1.0/systems";
    public static final String SYSTEM_INFO_URL = BASE_URL + "/api/v1.0/system/%s";
    public static final String SYSTEM_MEASUREMENTS_URL = BASE_URL + "/api/v1.0/measurements/%s";

    public static final DateFormat API_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
    public static final DateFormat API_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);

    public static final DateFormat UI_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
    public static final String UI_TIME_FORMAT = "%02d:%02d";

    /**
     * Common helper method to construct a measurement object.
     *
     * @param date submission date
     * @param measureType measurement type
     * @param value measurement value
     * @return the JSONObject instance
     * @throws JSONException something went wrong
     */
    public static JSONObject makeMeasurement(Date date, String measureType, float value)
        throws JSONException {
        JSONObject json = new JSONObject();
        JSONObject measurement = new JSONObject();
        measurement.put("time", API_DATE_TIME_FORMAT.format(date));
        measurement.put(measureType, value);
        JSONArray measurements = new JSONArray();
        measurements.put(measurement);
        json.put("measurements", measurements);
        return json;
    }
}
