package org.systemsbiology.baliga.aqx1010;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

public class MeasureLightActivity extends AppCompatActivity
implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_LIGHT);
        if (sensors.size() > 0) {
            Log.d("aqx1010", "HAS LIGHT SENSOR");
        } else {
            Log.d("aqx1010", "DON'T HAVE LIGHT SENSOR");
        }
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        setContentView(R.layout.activity_measure_light);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float luxValues = event.values[0];
        TextView view = (TextView) this.findViewById(R.id.lightValueTextView);
        view.setText(String.format("%.03f lux", luxValues));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

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
