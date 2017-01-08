package com.example.aggel.blindlight.Listeners;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import com.example.aggel.blindlight.Activities.SettingsActivity;
import com.example.aggel.blindlight.util.MyAsyncTask;
import com.example.aggel.blindlight.util.SoundEvent;

import static com.example.aggel.blindlight.Activities.MainActivity.macAddress;
import static com.example.aggel.blindlight.Activities.MainActivity.offine_mode;
import static com.example.aggel.blindlight.Activities.MainActivity.Port_Ip;
import static com.example.aggel.blindlight.Activities.MainActivity.date;
import static com.example.aggel.blindlight.Activities.MainActivity.locationListener;


public class ProximityEventListener extends SettingsActivity implements SensorEventListener {

    private TextView proxText;
    public MyAsyncTask tt;
    private boolean CheckProx;
    private Context context;
    private SoundEvent se;
    private int soundId;
    private int streamId;
    public String sensor_name;
    public String sensor_value;

    public ProximityEventListener(SensorManager SM, boolean CheckProx, TextView proxText, Context context) {
        //Proximity Sensor
        Sensor mySensor = SM.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensor_name = ("Proximity Sensor");


        //Register sensor listener
        SM.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_NORMAL);

        //Assign TextView
        this.proxText = proxText;
        this.context = context;
        this.CheckProx = CheckProx;

        //mysound
        se = new SoundEvent();
        soundId = se.getSoundId(context);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        sensor_value = Float.toString(event.values[0]);
        proxText.setText("Οff");

        if(event.values[0] == 0){
            proxText.setText("Οn");
        }


        //---------------Calling Async Task Function---------------


        if (offine_mode == false) {
            //final String c = sensor_value;
            String topic = macAddress + "/" + getSensorName() + "/" + getSensorValue() + "/" + date + "/" + locationListener.getDevLatitude() + "/" + locationListener.getDevLongtitude();
            tt = new MyAsyncTask(topic, Port_Ip);
            tt.execute();

        }
        else {
            if ((event.values[0] == 0) && (CheckProx)) {
                proxText.setText("Οn");
                CharSequence text = "Βe careful!!";
                final Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 1500);
                streamId = se.playNonStop(soundId);
                return;
            }
            se.stopSound(streamId);
        }
    }


    public void unregister(SensorManager SM){
        SM.unregisterListener(this);
        se.stopSound(streamId);
    }
    public String getSensorName() {
        return sensor_name;
    }

    public String getSensorValue() {
         return sensor_value;
     }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Not in use
    }
}
