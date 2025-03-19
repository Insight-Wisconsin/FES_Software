package com.example.fes_software;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor gyroscope;
    private TextView gyroData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView HeightNumber = (TextView) findViewById(R.id.textViewHeight);
        TextView WeightNumber = (TextView) findViewById(R.id.textViewWeight);
        Button Heightbutton = findViewById(R.id.HeightButton);
        Button Weightbutton = findViewById(R.id.WeightButton);
        EditText HeighteditText = findViewById(R.id.Height);
        EditText WeighteditText = findViewById(R.id.Weight);
        gyroData = (TextView) findViewById(R.id.gyroData);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (gyroscope == null) {
            gyroData.setText("Gyroscope not available");
        }

        Heightbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = HeighteditText.getText().toString();
                HeightNumber.setText(text);
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();

            }
        });
        Weightbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = WeighteditText.getText().toString();
                WeightNumber.setText(text);
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();

            }
        });

    }
    @Override
    protected void onResume(){
        super.onResume();
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0]; // Rotation around X-axis
            float y = event.values[1]; // Rotation around Y-axis
            float z = event.values[2]; // Rotation around Z-axis

            String data = String.format("X: %.2f rad/s\nY: %.2f rad/s\nZ: %.2f rad/s", x, y, z);
            gyroData.setText(data);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for basic gyroscope usage
    }
}