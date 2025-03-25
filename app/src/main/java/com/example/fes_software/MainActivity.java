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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope;
    private TextView accelerometerData, gyroData,orientationData;
    private int height,weight;
    private long lastTimestamp = 0;
    private float[] accelValues = new float[3];
    private float[] gyroValues = new float[3];
    private boolean isForward=false;

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
        orientationData = (TextView) findViewById(R.id.orientationData);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accelerometer= sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

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

                isForward=false;
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
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelValues, 0, event.values.length);
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
        sendImpulse(gyroZ_deg,gyroValues[0]);
        String dataG = String.format("X: %.2f°\nY: %.2f°\nZ: %.2f°",
                gyroX_deg, gyroY_deg, gyroZ_deg);
        gyroData.setText(dataG);

        String dataA = String.format("X: %.2f m/s²\nY: %.2f m/s²\nZ: %.2f m/s²",
                accelValues[0], accelValues[1], accelValues[2]);
        accelerometerData.setText(dataA);
        String dataB = String.format("X: %.2f rad/s\nY: %.2f rad/s\nZ: %.2f rad/s",
                gyroValues[0], gyroValues[1], gyroValues[2]);
        orientationData.setText(dataA);

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for basic gyroscope usage
    }

    public void sendImpulse(Float zRotation,Float xGyro){
        if(zRotation>=28 && xGyro>0&& isForward==false){
            Toast.makeText(getApplicationContext(),"sending impulse",Toast.LENGTH_SHORT).show();
            System.out.println("forward impulse "+zRotation+ " "+ xGyro);
            isForward = true;
        }

    }


}