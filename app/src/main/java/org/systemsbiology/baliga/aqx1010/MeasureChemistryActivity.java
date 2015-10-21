package org.systemsbiology.baliga.aqx1010;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONArray;
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
import java.util.HashMap;
import java.util.Map;

public class MeasureChemistryActivity extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private static Map<String, String> TYPE_TO_NAME = new HashMap<>();
    private static Map<String, MeasureRange> TYPE_TO_RANGE = new HashMap<>();
    private static Map<String, Integer> TYPE_TO_GRADIENT = new HashMap<>();
    private static Map<String, String> TYPE_TO_UNIT = new HashMap<>();

    static {
        TYPE_TO_NAME.put(SystemDefaults.API_MEASURE_TYPE_PH, "pH");
        TYPE_TO_NAME.put(SystemDefaults.API_MEASURE_TYPE_AMMONIUM, "Ammonium");
        TYPE_TO_NAME.put(SystemDefaults.API_MEASURE_TYPE_NITRATE, "Nitrate");

        TYPE_TO_RANGE.put(SystemDefaults.API_MEASURE_TYPE_PH, new MeasureRange(6.2f, 8.4f));
        TYPE_TO_RANGE.put(SystemDefaults.API_MEASURE_TYPE_AMMONIUM, new MeasureRange(0.0f, 6.0f));
        TYPE_TO_RANGE.put(SystemDefaults.API_MEASURE_TYPE_NITRATE, new MeasureRange(0.0f, 200.0f));

        TYPE_TO_GRADIENT.put(SystemDefaults.API_MEASURE_TYPE_PH, R.drawable.ph_gradient);
        TYPE_TO_GRADIENT.put(SystemDefaults.API_MEASURE_TYPE_AMMONIUM, R.drawable.nh4_gradient);
        TYPE_TO_GRADIENT.put(SystemDefaults.API_MEASURE_TYPE_NITRATE, R.drawable.no3_gradient);

        TYPE_TO_UNIT.put(SystemDefaults.API_MEASURE_TYPE_PH, "");
        TYPE_TO_UNIT.put(SystemDefaults.API_MEASURE_TYPE_AMMONIUM, "mg/l");
        TYPE_TO_UNIT.put(SystemDefaults.API_MEASURE_TYPE_NITRATE, "mg/l");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String measureType = getIntent().getStringExtra("measure_type");
        final String systemUID = getIntent().getStringExtra("system_uid");
        final String systemName = getIntent().getStringExtra("system_name");
        setContentView(R.layout.activity_measure_chemistry);
        getSupportActionBar().setTitle(String.format("Measure %s (%s)",
                TYPE_TO_NAME.get(measureType), systemName));
        final MeasureRange range = TYPE_TO_RANGE.get(measureType);

        SeekBar seekbar = (SeekBar) findViewById(R.id.measureSeekBar);
        final EditText dateEdit = (EditText) findViewById(R.id.editDateView);
        final EditText timeEdit = (EditText) findViewById(R.id.editTimeView);
        final Calendar calendar = GregorianCalendar.getInstance();
        final EditText measureText = (EditText) findViewById(R.id.measureValueText);

        dateEdit.setText(SystemDefaults.UI_DATE_FORMAT.format(calendar.getTime()));
        measureText.setText(String.format("%.02f", range.min));
        seekbar.setBackgroundResource(TYPE_TO_GRADIENT.get(measureType));
        TextView unitLabel = (TextView) findViewById(R.id.unitLabel);
        unitLabel.setText(TYPE_TO_UNIT.get(measureType));

        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Date submitDate = SystemDefaults.UI_DATE_FORMAT.parse(dateEdit.getText().toString());
                    String valueStr = measureText.getText().toString();
                    float value = Float.parseFloat(valueStr);
                    JSONObject json = SystemDefaults.makeMeasurement(submitDate, measureType, value);
                    new SendMeasurementTask(MeasureChemistryActivity.this,
                            GoogleTokenTask.storedEmail(MeasureChemistryActivity.this),
                            systemUID, json).execute();
                } catch (ParseException ex) {
                    Log.e("aqx1010","date parse error", ex);
                } catch (JSONException ex) {
                    Log.e("aqx1010","json error", ex);
                } catch (IOException ex) {
                    Log.e("aqx1010","io error", ex);
                }
                MeasureChemistryActivity.this.finish();
            }
        });

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

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float rangeAbs = range.max - range.min;
                float value = range.min + ((float) progress * (rangeAbs / 100.0f));
                measureText.setText(String.format("%.02f", value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }
}
