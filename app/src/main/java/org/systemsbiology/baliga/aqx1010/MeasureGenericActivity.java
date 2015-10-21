package org.systemsbiology.baliga.aqx1010;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import org.systemsbiology.baliga.aqx1010.apiclient.SystemDefaults;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class MeasureGenericActivity extends AppCompatActivity {

    private static Map<String, String> TYPE_TO_NAME = new HashMap<>();
    private static Map<String, MeasureRange> TYPE_TO_RANGE = new HashMap<>();
    private static Map<String, String> TYPE_TO_UNIT = new HashMap<>();
    static {
        TYPE_TO_NAME.put("dio", "Dissolved Oxygen");
        TYPE_TO_NAME.put("temp", "Temperature");
        TYPE_TO_UNIT.put("dio", "mg/l");
        TYPE_TO_UNIT.put("temp", "&deg;C");

        TYPE_TO_RANGE.put("dio", new MeasureRange(6.2f, 8.4f));
        TYPE_TO_RANGE.put("temp", new MeasureRange(0.0f, 50.0f));
    }
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String measureType = getIntent().getStringExtra("measure_type");
        final String systemUID = getIntent().getStringExtra("system_uid");
        final String systemName = getIntent().getStringExtra("system_name");
        setContentView(R.layout.activity_measure_generic);
        getSupportActionBar().setTitle(String.format("Measure %s (%s)",
                TYPE_TO_NAME.get(measureType), systemName));
        final MeasureRange range = TYPE_TO_RANGE.get(measureType);
        final EditText dateEdit = (EditText) findViewById(R.id.editDateView);
        final EditText timeEdit = (EditText) findViewById(R.id.editTimeView);
        final Calendar calendar = GregorianCalendar.getInstance();
        final EditText measureText = (EditText) findViewById(R.id.measureValueText);
        measureText.setText(String.format("%.02f", range.min));

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
                timeEdit.setText(String.format("%02d:%02d", hourOfDay, minute));
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
                MeasureGenericActivity.this.finish();
            }
        });
    }
}
