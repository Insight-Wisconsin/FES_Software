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
    private Sensor accelerometer;
    private TextView accelerometerData;
    private TextView accelerometerPosition;
    private TextView gyroData;
    private int height;
    private int weight;
    private long lastTimestamp = 0;
    private float velocityX = 0, velocityY = 0, velocityZ = 0;
    private float positionX = 0, positionY = 0, positionZ = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HeightLayout();

    }

    protected void HeightLayout(){
        setContentView(R.layout.height);
        Button Heightbutton = findViewById(R.id.HeightButton);
        EditText HeighteditText = findViewById(R.id.Height);
        TextView error = findViewById(R.id.error);
        Heightbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String text = HeighteditText.getText().toString();
                try{
                    height = Integer.parseInt(text);
                    WeightLayout();
                }catch(Exception e){
                    error.setText("not a number");
                }
            }
        });
    }
    protected void WeightLayout(){
        setContentView(R.layout.weight);
        Button Weightbutton = findViewById(R.id.WeightButton);
        EditText WeighteditText = findViewById(R.id.Weight);
        TextView error = findViewById(R.id.error);
        Weightbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String text = WeighteditText.getText().toString();
                try{
                    weight = Integer.parseInt(text);
                    mainLayout();
                }catch(Exception e){
                    error.setText("not a number");
                }
            }
        });
    }

    protected void mainLayout(){
        setContentView(R.layout.activity_main);
        TextView WeightNumber = (TextView) findViewById(R.id.textViewWeight);
        TextView HeightNumber = (TextView) findViewById(R.id.textViewHeight);
        HeightNumber.setText(String.valueOf(height));
        WeightNumber.setText(String.valueOf(weight));
        gyroData = (TextView) findViewById(R.id.gyroData);
        accelerometerData = (TextView) findViewById(R.id.accelerometerData);
        accelerometerPosition =(TextView) findViewById(R.id.AccelerometerPosition);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accelerometer= sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
       Button edit = findViewById(R.id.Edit);
        if (gyroscope == null) {
            gyroData.setText("Gyroscope not available");
        }
        if(accelerometer==null){
            accelerometerData.setText("Accelerometer not available");
        }
        edit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v){
            HeightLayout();
        }
        });

        onResume();
    }
    @Override
    protected void onResume(){
        super.onResume();
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
        }
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }


    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0]; // Rotation around X-axis
            float y = event.values[1]; // Rotation around Y-axis
            float z = event.values[2]; // Rotation around Z-axis

            String dataG = String.format("X: %.2f rad/s\nY: %.2f rad/s\nZ: %.2f rad/s", x, y, z);
            gyroData.setText(dataG);
        }
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float x = event.values[0]; // Rotation around X-axis
            float y = event.values[1]; // Rotation around Y-axis
            float z = event.values[2]; // Rotation around Z-axis

            String dataA = String.format("X: %.2f m/s^2\nY: %.2f m/s^2\nZ: %.2f m/s^2", x, y, z);
            accelerometerData.setText(dataA);

            long currentTime = event.timestamp;
            float deltaTime = (lastTimestamp == 0) ? 0 : (currentTime - lastTimestamp) / 1_000_000_000.0f;
            lastTimestamp = currentTime;

            // Integrate acceleration to velocity
            velocityX += x * deltaTime;
            velocityY += y * deltaTime;
            velocityZ += z * deltaTime;

            // Integrate velocity to position
            positionX += velocityX * deltaTime;
            positionY += velocityY * deltaTime;
            positionZ += velocityZ * deltaTime;
            String dataB = String.format(
                    "X: %.2f m\nY: %.2f m\nZ: %.2f m",
                    positionX, positionY, positionZ
            );
            accelerometerPosition.setText(dataB);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for basic gyroscope usage
    }
}