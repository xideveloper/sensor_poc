package com.my.sensor;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

@ContentView(R.layout.main)
public class MainActivity extends SensorActivity{

	@InjectView (R.id.proximityTextView) private TextView proximityTxt;
	
	private SensorManager sensorMgr;
	private Sensor proxSensor;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        proxSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_PROXIMITY);
      
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){
    	super.onAccuracyChanged(sensor, accuracy);
    }

    @Override
    public void onSensorChanged(SensorEvent event){
    	super.onSensorChanged(event);
    	
    	if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
    		if(event.values[0]==OBJECT_NEAR){ //if an object is approaching the phone
        		
        		Intent i = new Intent(this, MenuActivity.class);
                startActivity(i); 
                
        	}
    	}
    	
    }
    
    
    protected void onResume() {
        super.onResume();
 
        sensorMgr.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        
        sensorMgr.unregisterListener(this);
    }
    
    
    //TODO: open menu when vertically shake the phone
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
