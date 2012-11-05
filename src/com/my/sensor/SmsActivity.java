package com.my.sensor;

import roboguice.inject.ContentView;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

@ContentView(R.layout.sms)
public class SmsActivity extends SensorActivity{
	
	private float mLastAccX, mLastAccY, mLastAccZ;
	private boolean mInitialized;
	private final float NOISE = (float) 2.0;
	
	private ViewGroup contentView;
	private TextView shakeMeTxt;
	
	private LayoutInflater inflater;
	private LinearLayout smsLinearLayout;
	private ScrollView scrollView;
	
	private SensorManager sensorMgr;
	private Sensor accSensor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mInitialized = false;
		inflater = LayoutInflater.from(this);
		
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        accSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            
	}
	
	protected void onResume() {
		super.onResume();
		sensorMgr.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		sensorMgr.unregisterListener(this);
	}
	
	@Override
	public void onStart(){
    	super.onStart();
    	
    	contentView = (ViewGroup)getWindow().getDecorView();
    	shakeMeTxt = (TextView) contentView.findViewById(R.id.shake_me_text);
    	smsLinearLayout = (LinearLayout) contentView.findViewById(R.id.smss);
	 }
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		detectShake(event);
	}
	
	private void detectShake(SensorEvent event){
    	float accX = event.values[0]; //acceleration (-Gx) on the x-axis 
		float accY = event.values[1]; //acceleration (-Gy) on the y-axis
		float accZ = event.values[2]; //acceleration (-Gz) on the z-axis
		if(!mInitialized) {
			mInitialized = true;
			
			mLastAccX = accX;
			mLastAccY = accY;
			mLastAccZ = accZ;
		
			
		}
		else {
			float diffX = Math.abs(mLastAccX - accX);
			float diffY = Math.abs(mLastAccY - accY);
			float diffZ = Math.abs(mLastAccZ - accZ);
			
			if (diffX < NOISE) 
				diffX = (float)0.0;
			if (diffY < NOISE) 
				diffY = (float)0.0;
			if (diffZ < NOISE)
				diffZ = (float)0.0;
			
			
			if (diffX > diffY) {
				Log.v("**H shake**","ok");
				getUnreadSMS();
				
			} else if (diffY > diffX) {
				Log.v("**V shake**","ok");
				getUnreadSMS();
				
			} else {
				Log.v("**NO SHAKE**","ok");
				
			}
			
			mLastAccX = accX;
			mLastAccY = accY;
			mLastAccZ = accZ;
		}
    }
	
	private void getUnreadSMS(){
		
		String[] projection = { "person","body" };
		
		final Uri SMS_INBOX = Uri.parse("content://sms/inbox");

		Cursor c = getContentResolver().query(SMS_INBOX, projection, "read = 0", null, null);
		int unreadMessagesCount = c.getCount();
		if(c.moveToLast()){
			String person = "Unknown";
			while(c.moveToPrevious()){
				
				 person = c.getString(c.getColumnIndex("person"));
	    		 String body = c.getString(c.getColumnIndex("body"));
				
	    		 Log.v("body", body);
	    		 
	    		 //TODO: populate list
	    		 //populateSMSList( person,  body,  smsLinearLayout);
			}
		}
		Log.v("*nr of unread sms*", c.getCount()+"");
		//shakeMeTxt.setVisibility(View.GONE);
		shakeMeTxt.setText("There are "+c.getCount()+" unread SMS messages");
		c.close();
		
	}
	
	//TODO: populate the unread SMS list
	private void populateSMSList(String person, String body, LinearLayout container){
		RelativeLayout missedCallRow = (RelativeLayout) inflater.inflate(R.layout.missed_call_row, null);
		TextView callerNameField = (TextView)missedCallRow.findViewById(R.id.caller_name);
		TextView callDateField = (TextView)missedCallRow.findViewById(R.id.call_date);
		TextView callerNrField = (TextView)missedCallRow.findViewById(R.id.call_nr);
		TextView callNrTypeField =(TextView)missedCallRow.findViewById(R.id.call_nr_type);
		
		callerNameField.setText(person);
		
		callerNrField.setText(body);
		
		
	}
}
