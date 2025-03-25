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
    private Sensor accelerometer, gyroscope, magnetometer;
    private TextView accelerometerData, gyroData, orientationData;
    private int height,weight;
    private long lastTimestamp = 0;
    private float[] accelValues = new float[3];
    private float[] magnetValues = new float[3];
    private float[] gyroValues = new float[3];

    private float[] rotationMatrix = new float[9];
    private float[] orientationAngles = new float[3];
    private float[] gyroRotation = new float[3]; // Store integrated gyro values

    private float filteredPitch = 0, filteredRoll = 0, filteredYaw = 0;

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
        orientationData = findViewById(R.id.orientationData);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accelerometer= sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        Button edit = findViewById(R.id.Edit);
        if (gyroscope == null) {
            gyroData.setText("Gyroscope not available");
        }
        if(accelerometer==null){
            accelerometerData.setText("Accelerometer not available");
        }
        if (magnetometer != null) {
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        }

        edit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v){
            HeightLayout();
        }
        });

        Button AngleReset = findViewById(R.id.angleReset);
        Button ResetAll = findViewById(R.id.resetAll);
        AngleReset.setOnClickListener(new View.OnClickListener(){
         @Override
         public void onClick(View v){
                gyroValues[0]=0;
                gyroValues[1]=0;
                gyroValues[2]=0;
            }
        });

        ResetAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                gyroValues[0]=0;
                gyroValues[1]=0;
                gyroValues[2]=0;

                accelValues[0]=0;
                accelValues[1]=0;
                accelValues[2]=0;
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
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }


    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelValues, 0, event.values.length);
            updateOrientation();
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetValues, 0, event.values.length);
            updateOrientation();
        }
        else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (lastTimestamp != 0) {
                float dt = (event.timestamp - lastTimestamp) / 1_000_000_000.0f; // Convert ns to seconds

                gyroValues[0] += event.values[0] * dt; // Integrate X rotation
                gyroValues[1] += event.values[1] * dt; // Integrate Y rotation
                gyroValues[2] += event.values[2] * dt; // Integrate Z rotation
            }
            lastTimestamp = event.timestamp;
        }

        float gyroX_deg = gyroValues[0] * (180f / (float) Math.PI);
        float gyroY_deg = gyroValues[1] * (180f / (float) Math.PI);
        float gyroZ_deg = gyroValues[2] * (180f / (float) Math.PI);

        String dataG = String.format("X: %.2f°\nY: %.2f°\nZ: %.2f°",
                gyroX_deg, gyroY_deg, gyroZ_deg);
        gyroData.setText(dataG);

        String dataA = String.format("X: %.2f m/s²\nY: %.2f m/s²\nZ: %.2f m/s²",
                accelValues[0], accelValues[1], accelValues[2]);
        accelerometerData.setText(dataA);

    }
    private void updateOrientation() {
        if (SensorManager.getRotationMatrix(rotationMatrix, null, accelValues, magnetValues)) {
            SensorManager.getOrientation(rotationMatrix, orientationAngles);

            float pitch = (float) Math.toDegrees(orientationAngles[1]); // Rotation around X-axis
            float roll = (float) Math.toDegrees(orientationAngles[2]); // Rotation around Y-axis
            float yaw = (float) Math.toDegrees(orientationAngles[0]); // Rotation around Z-axis (compass)

            // Complementary Filter to smooth values
            final float alpha = 0.98f;
            filteredPitch = alpha * (filteredPitch + gyroValues[0]) + (1 - alpha) * pitch;
            filteredRoll = alpha * (filteredRoll + gyroValues[1]) + (1 - alpha) * roll;
            filteredYaw = alpha * (filteredYaw + gyroValues[2]) + (1 - alpha) * yaw;

            String orientationText = String.format(
                    "Pitch: %.2f°\nRoll: %.2f°\nYaw: %.2f°",
                    filteredPitch, filteredRoll, filteredYaw
            );

            orientationData.setText(orientationText);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for basic gyroscope usage
    }




}