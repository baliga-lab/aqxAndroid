package org.systemsbiology.baliga.aqx1010;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONException;
import org.json.JSONObject;
import org.systemsbiology.baliga.aqx1010.apiclient.GoogleTokenTask;
import org.systemsbiology.baliga.aqx1010.apiclient.SendMeasurementTask;
import org.systemsbiology.baliga.aqx1010.apiclient.SystemDefaults;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MeasureLightActivity extends AppCompatActivity
implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private TextView lightValueView;
    private float currentValue;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String measureType = getIntent().getStringExtra("measure_type");
        final String systemUID = getIntent().getStringExtra("system_uid");
        final String systemName = getIntent().getStringExtra("system_name");
        setContentView(R.layout.activity_measure_light);

        lightValueView = (TextView) this.findViewById(R.id.lightValueTextView);

        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_LIGHT);
        if (sensors.size() > 0) {
            Log.d("aqx1010", "HAS LIGHT SENSOR");
        } else {
            Log.d("aqx1010", "DON'T HAVE LIGHT SENSOR");
        }
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        getSupportActionBar().setTitle(String.format("Measure Light (%s)", systemName));
        final EditText dateEdit = (EditText) findViewById(R.id.editDateView);
        final EditText timeEdit = (EditText) findViewById(R.id.editTimeView);
        final Calendar calendar = GregorianCalendar.getInstance();
        dateEdit.setText(SystemDefaults.UI_DATE_FORMAT.format(calendar.getTime()));

        datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        dateEdit.setText(SystemDefaults.UI_DATE_FORMAT.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dateEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) datePickerDialog.show();
            }
        });

        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeEdit.setText(String.format(SystemDefaults.UI_TIME_FORMAT, hourOfDay, minute));
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        timeEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) timePickerDialog.show();
            }
        });
        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Date date = SystemDefaults.UI_DATE_FORMAT.parse(dateEdit.getText().toString());
                    JSONObject json = SystemDefaults.makeMeasurement(date, measureType, currentValue);
                    new SendMeasurementTask(MeasureLightActivity.this,
                            GoogleTokenTask.storedEmail(MeasureLightActivity.this),
                            systemUID, json).execute();

                } catch (IOException ex) {
                    Log.e("aqx1010","io error", ex);
                } catch (ParseException ex) {
                    Log.e("aqx1010","date parse error", ex);
                } catch (JSONException ex) {
                    Log.e("aqx1010","json error", ex);
                }
                MeasureLightActivity.this.finish();
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        currentValue = event.values[0];
        lightValueView.setText(String.format("%.03f lux", currentValue));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    protected void onResume() {
        super.onResume();
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (lightSensor != null) {
            sensorManager.unregisterListener(this);
        }
    }
}
