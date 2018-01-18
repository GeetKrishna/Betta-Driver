package com.example.nisch.trial;

import android.hardware.SensorEvent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private TextView countDownText;
    private Button countDownButton;
    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds = 60000;
    private boolean timeRunning;

    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;

    private OnShakeListener mListener;
    private long mShakeTimestamp;
    private int mShakeCount;

    private Sensor sensor;
    private SensorManager sensorManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        countDownText = findViewById(R.id.countDown_text);
        countDownButton = findViewById(R.id.countDown_button);

        countDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startstop();
            }
        });

    }

    private void startstop() {
        if (timeRunning) {
            stopTimer();
        } else startTimer();
    }

    private void stopTimer() {
        //countDownTimer.cancel();
        //countDownButton.setText("START");
        //timeRunning = false;
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMilliseconds, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMilliseconds = l;
                updateTimer();
            }

            @Override
            public void onFinish() {

            }
        }.start();

        countDownButton.setText("PAUSE");
        timeRunning = true;
    }

    private void updateTimer() {
        int mins = (int)timeLeftInMilliseconds/60000;
        int secs = (int)timeLeftInMilliseconds%60000/1000;

        String time;
        time = "" + mins;
        time += ":" ;
        if (secs < 10) time+= 0;
        time += secs;

        countDownText.setText(time);
    }

    public void setOnShakeListener(OnShakeListener listener) {
        this.mListener = listener;
    }

    public interface OnShakeListener {
        public void onShake(int count);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (mListener != null) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
           /*float x= 2.7F;
           float y= 3.2F;
           float z= 9.5F;*/

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement.
            float gForce = (float) Math.sqrt((gX*gX)+(gY*gY)+(gZ*gZ));

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {

                Toast.makeText(this, "inside gforce.", Toast.LENGTH_SHORT).show();
                final long now = System.currentTimeMillis();
                // ignore shake events too close to each other (500ms)
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return;
                }

                mShakeTimestamp = now;
                mShakeCount++;

                //mListener.onShake(mShakeCount);
                Log.d("Sensor:Accelerometer", "sudden movement detected at time: " + mShakeTimestamp);
                Toast.makeText(this, "Sudden Movement detected ", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
