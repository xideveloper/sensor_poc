package com.my.sensor;

import com.google.inject.Inject;
import com.my.util.SensorUtil;

import roboguice.activity.RoboActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class SensorActivity extends RoboActivity implements SensorEventListener{

	 @Inject protected SensorUtil sensorUtil;
	 
	 protected final static int WAIT_TIME=2;
	 protected final static double PITCH_THRESHOLD = 0.2;
	 protected final static double AZIMUTH_THRESHOLD = 0.2;
	 protected final static int OBJECT_NEAR = 0;
		
	 protected long lastMillis = 0;
	 protected long timeInterval = 1000;
	 
	 @Override
	 public void onAccuracyChanged(Sensor sensor, int accuracy){
		 
	 }

	 @Override
	 public void onSensorChanged(SensorEvent event){
	  
	 }
}
