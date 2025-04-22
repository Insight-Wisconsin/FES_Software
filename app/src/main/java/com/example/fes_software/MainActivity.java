package com.example.fes_software;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope;

    private LinearLayout relativeLayout;
    private TextView accelerometerData, gyroData,orientationData,AccelerometerPosition;
    private boolean man;
    private double shoesize;
    private long lastTimestamp = 0;
    private float[] accelValues = new float[3];
    private float[] gyroValues = new float[3];
    private boolean isForward=false;
    private boolean isBackward=false;
    private float Xlastevent;
    private boolean Impulse;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenderLayout();

    }

    protected void GenderLayout(){
        setContentView(R.layout.gender);
        Button malebutton = findViewById(R.id.maleButton);
        Button femalebutton = findViewById(R.id.femaleButton);
        malebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                    man=true;
                shoesizeLayout();


            }
        });
        femalebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                man=false;
                shoesizeLayout();


            }
        });
    }
    protected void shoesizeLayout(){
        setContentView(R.layout.shoesize);
        Button Weightbutton = findViewById(R.id.shoesizeButton);
        EditText WeighteditText = findViewById(R.id.shoesize);
        TextView error = findViewById(R.id.error);
        Weightbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String text = WeighteditText.getText().toString();

                try{
                    shoesize = Double.parseDouble(text);
                    mainLayout();
                }catch(Exception e){
                    error.setText(e.getMessage());

                }



            }
        });
    }

    protected void mainLayout(){
        setContentView(R.layout.activity_main);
        relativeLayout = (LinearLayout) findViewById(R.id.main);
        TextView male = (TextView) findViewById(R.id.textViewMale);
        TextView ShoesizeNumber = (TextView) findViewById(R.id.textViewShoesize);
        if(man) {
            male.setText("True");
        }else{
            male.setText("False");

        }
        ShoesizeNumber.setText(String.valueOf(shoesize));

        gyroData = (TextView) findViewById(R.id.gyroData);
        accelerometerData = (TextView) findViewById(R.id.accelerometerData);
        orientationData = (TextView) findViewById(R.id.orientationData);
        AccelerometerPosition = (TextView) findViewById(R.id.AccelerometerPosition);

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
            GenderLayout();
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

        mediaPlayer = MediaPlayer.create(this, R.raw.impulse);
        mediaPlayer.setLooping(true); // So it loops during the impulse

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
        sendImpulse(gyroZ_deg,event.values[0],Xlastevent);
        Xlastevent = event.values[0];
        String dataG = String.format("X: %.2f°\nY: %.2f°\nZ: %.2f°",
                gyroX_deg, gyroY_deg, gyroZ_deg);
        gyroData.setText(dataG);

        String dataA = String.format("X: %.2f m/s²\nY: %.2f m/s²\nZ: %.2f m/s²",
                accelValues[0], accelValues[1], accelValues[2]);
        accelerometerData.setText(dataA);

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for basic gyroscope usage
    }

    public void sendImpulse(Float zRotation, Float xGyro, Float lastevent) {
        if (zRotation >= 28 && xGyro > 0 && !isForward) {
            orientationData.setText("forward impulse " + zRotation + " " + xGyro);
            isForward = true;
            isBackward = false;
            Impulse = true;
        }
        if (zRotation <= -28 && xGyro < 0 && !isBackward) {
            orientationData.setText("Backward impulse " + zRotation + " " + xGyro);
            isBackward = true;
            isForward = false;
            Impulse = true;
        }

        if (zRotation < 28 && zRotation > -28 && Impulse) {
            orientationData.setText("reset " + zRotation + " " + xGyro);
            AccelerometerPosition.setText("no impulse");
            relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            isForward = false;
            isBackward = false;
            Impulse = false;

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            }
        }

        if (Impulse) {
            AccelerometerPosition.setText("Impulse is being sent");
            relativeLayout.setBackgroundColor(Color.parseColor("#8BC34A"));

            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}