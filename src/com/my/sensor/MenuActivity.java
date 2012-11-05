package com.my.sensor;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

@ContentView(R.layout.menu)
public class MenuActivity extends SensorActivity{
	
	private SensorManager sensorMgr;
	private Sensor rotatSensor;
	
	private int timeCounter, timeCounter2;
	
	@InjectView (R.id.go_back_btn) private Button goBackBtn;
	@InjectView (R.id.missed_calls_btn) private Button missedCallsBtn;
	@InjectView (R.id.email_btn) private Button emailBtn;
	@InjectView (R.id.sms_btn) private Button smsBtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
	    rotatSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
	        
	}
	
	@Override
	public void onStart(){
		super.onStart();
		initButtons();
	}
	
	private void initButtons(){
		timeCounter = 0;
		/*
		missedCallsBtn.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	
		    	if(sensorUtil.containsKey(KEY_BUTTON_CLICK_TIME)){
	    			long currentMillis = System.currentTimeMillis();
	    			long storedMillis = sensorUtil.getLongValue(KEY_BUTTON_CLICK_TIME, 0);
	    			long millisDiff = currentMillis - storedMillis;
	    			Log.v("***double click difference**", millisDiff+"**"+millisDiff/1000 +"");
	    		}
	    	
		    }
		});
		*/
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy){
		super.onAccuracyChanged(sensor, accuracy);
	}

    @Override
    public void onSensorChanged(SensorEvent event){
    	super.onSensorChanged(event);
    	
    	
    	float vectorX = event.values[0]; // around x-axis, point to east direction
		
    	//the following two vectors are not used for now.
    	float vectorY = event.values[1]; // around y-axis, point to magnetic north direction
    	float vectorZ = event.values[2]; // around z-axis, ponit to sky direction 
        
        //update sensor data every second (i.e. timeInterval)
        synchronized (this) {
        	long currentTimeMillis = System.currentTimeMillis();
            if (System.currentTimeMillis() > lastMillis + timeInterval) {
            	lastMillis = currentTimeMillis;
            	
            	detectButtonClick(vectorX, vectorY);
            	
            }
        }
        
        //highlight button accordingly
        detectCompassUpOrDown(vectorX);
    	detectCompassLeftOrRightSide(vectorY);
      
    }
    
  
    private void detectButtonClick(float vectorX, float vectorY){
    	
    	if (vectorX <= AZIMUTH_THRESHOLD && vectorX >= -AZIMUTH_THRESHOLD) {
    		Log.v("**idle up-down position**","ok");
    		timeCounter = 0;
    	} else if (vectorX < -AZIMUTH_THRESHOLD) {
    		Log.v("**missed call clicked**","ok");
    		
    		timeCounter++;
    		if(timeCounter==WAIT_TIME){
    			Log.v("*GO TO MISSED CALLS*","ok");
    			Intent i = new Intent(this, MissedCallsActivity.class);
    			startActivity(i); 
    		}
    		
    		
    		
    	} else if (vectorX > AZIMUTH_THRESHOLD) {
    		Log.v("**SMS click**","ok");
    		
    		timeCounter++;
    		if(timeCounter==WAIT_TIME){
    			Log.v("*GO TO SMS*","ok");
    			Intent i = new Intent(this, SmsActivity.class);
                startActivity(i);
    		}
    		
    	}
    	
    	
    	
    	if (vectorY <= PITCH_THRESHOLD && vectorY >= -PITCH_THRESHOLD) {
    		Log.v("**idle left-right position**","ok");
    		timeCounter2 = 0;
    	}else if(vectorY < -PITCH_THRESHOLD){
    		Log.v("**Right up**","ok");
    		
    		timeCounter2++;
    		if(timeCounter2==WAIT_TIME){
    			Log.v("*GO BACK!*","ok");
    		}
    		
    	}else if(vectorY > PITCH_THRESHOLD){
    		Log.v("**Left up**","ok");
    		
    		timeCounter2++;
    		if(timeCounter2==WAIT_TIME){
    			Log.v("*GO to EMAIL!*","ok");
    		}
    		
    	}
    }
    
    //an unsmart way for changing buttons' background
    private void detectCompassUpOrDown(float vectorX){
    	if (vectorX <= AZIMUTH_THRESHOLD && vectorX >= -AZIMUTH_THRESHOLD){
    		Log.v("**VERTICAL**","ok");
    		this.missedCallsBtn.setBackgroundResource(R.drawable.button_normal);
    		this.smsBtn.setBackgroundResource(R.drawable.button_normal);
    	} else if (vectorX < -AZIMUTH_THRESHOLD) {
    		Log.v("**HEAD DOWN**","ok");
    		
    		this.missedCallsBtn.setBackgroundResource(R.drawable.button_pressed);
    		this.goBackBtn.setBackgroundResource(R.drawable.button_normal);
    		this.emailBtn.setBackgroundResource(R.drawable.button_normal);
    		this.smsBtn.setBackgroundResource(R.drawable.button_normal);
    		
    		
    	} else if (vectorX > AZIMUTH_THRESHOLD) {
    		Log.v("**HEAD UP**","ok");
    		
    		this.missedCallsBtn.setBackgroundResource(R.drawable.button_normal);
    		this.goBackBtn.setBackgroundResource(R.drawable.button_normal);
    		this.emailBtn.setBackgroundResource(R.drawable.button_normal);
    		this.smsBtn.setBackgroundResource(R.drawable.button_pressed);
    		
    	}
    }
    
    private void detectCompassLeftOrRightSide(float vectorY){
    	if (vectorY <= PITCH_THRESHOLD && vectorY >= -PITCH_THRESHOLD) {
    		//Log.v("**VERTICAL**","ok");
    		
    		this.goBackBtn.setBackgroundResource(R.drawable.button_normal);
    		this.emailBtn.setBackgroundResource(R.drawable.button_normal);
    		
    	} else if (vectorY < -PITCH_THRESHOLD) {
    		//Log.v("**RIGHT UP**","ok");
    		
    		this.missedCallsBtn.setBackgroundResource(R.drawable.button_normal);
    		this.goBackBtn.setBackgroundResource(R.drawable.button_pressed);
    		this.emailBtn.setBackgroundResource(R.drawable.button_normal);
    		this.smsBtn.setBackgroundResource(R.drawable.button_normal);
    		
    		
    	} else if (vectorY > PITCH_THRESHOLD) {
    		//Log.v("**LEFT UP**","ok");
    		
    		this.missedCallsBtn.setBackgroundResource(R.drawable.button_normal);
    		this.goBackBtn.setBackgroundResource(R.drawable.button_normal);
    		this.emailBtn.setBackgroundResource(R.drawable.button_pressed);
    		this.smsBtn.setBackgroundResource(R.drawable.button_normal);
    		
    	}
    }
    
    
 
   
    @Override
    protected void onResume() {
        super.onResume();
        
        sensorMgr.registerListener(this, rotatSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
    
        sensorMgr.unregisterListener(this);
    }

}
