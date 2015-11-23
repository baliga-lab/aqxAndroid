package org.systemsbiology.baliga.aqx1010;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.systemsbiology.baliga.aqx1010.apiclient.SystemDefaults;

import java.text.ParseException;

import static org.systemsbiology.baliga.aqx1010.apiclient.SystemDefaults.*;
import java.util.*;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    private static final String API_DATE_TIME1 = "2015-11-20T11:15:53Z";
    public ApplicationTest() {
        super(Application.class);
    }

    public void testParseDate() throws java.text.ParseException {
        Date time = API_DATE_TIME_FORMAT.parse(API_DATE_TIME1);
    }
}