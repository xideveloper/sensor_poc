package com.my.sensor;

import java.util.ArrayList;
import java.util.List;

import com.my.model.Contactor;

import roboguice.inject.ContentView;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

@ContentView(R.layout.missed_calls)
public class MissedCallsActivity extends SensorActivity{

	private final int scrollDist = 90;
	private SensorManager sensorMgr;
	private Sensor proxSensor;
	private Sensor rotatSensor;
	
	
	private ViewGroup contentView;
	private LayoutInflater inflater;
	private LinearLayout missedCallsLinearLayout;
	private ScrollView scrollView;
	
	
	private String selectedPhoneNr;
	private List<Contactor> phoneNrList;
	private TextView replyToNr, replyToName;
	private int moveCounter=0;
	
	
	
	private long lastMillis = 0;
	private long intervalMillis = 1000;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		inflater = LayoutInflater.from(this);
		
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        proxSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        rotatSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            
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
            
            //update the result every second (i.e. intervalMillis)
            synchronized (this) {
            	long currentMillis = System.currentTimeMillis();
                if (System.currentTimeMillis() > lastMillis + intervalMillis) {
                	lastMillis = currentMillis;
                	//Log.v("**zaimuth_angle**", String.valueOf(azimuth_angle));
                	detectScrolling(vectorX);
                	
                }
            }
        	
            if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            	Log.v("**PROX**","YES");
            	if(event.values[0]==OBJECT_NEAR){
                	
                	if(selectedPhoneNr!=null && !selectedPhoneNr.equals("")){
                		Intent callIntent = new Intent(Intent.ACTION_CALL);
               	     	callIntent.setData(Uri.parse("tel:"+selectedPhoneNr));
               	     	startActivity(callIntent);
                	}
                	
                	
                }
            }
            
    	

	 }
    
    private void detectScrolling(float vectorX){
    	
    	if (vectorX <= AZIMUTH_THRESHOLD && vectorX >= -AZIMUTH_THRESHOLD) {
    		Log.v("**idle up-down pos**",String.valueOf(moveCounter));
    		
    		
    		
    	} else if (vectorX < -AZIMUTH_THRESHOLD) {
    		Log.v("**Head DOWN**",String.valueOf(moveCounter));
    	
    		scrollView.post(new Runnable() {
  	    	    @Override
  	    	    public void run() {
  	    	    	Log.v("*SCROLL UP*","Ok");
  	    	    	//if(moveCounter >0 && moveCounter<=phoneNrList.size()-1){
  	    	    		Contactor caller = phoneNrList.get(moveCounter);
  	  	    	    	selectedPhoneNr= caller.getPhoneNr();
  	  	    	    	replyToNr.setText(caller.getPhoneNr()+"  "+caller.getPhoneNrType());
  	  	    	    	replyToName.setText(caller.getName());
  	  	    	    	
  	    	    	//}
  	    	    	moveCounter++;
  	    	    	
	  	    	    scrollView.smoothScrollBy(0, scrollDist);
  	    
  	    	    } 
  	    	});
    		
    		
    		
    	} else if (vectorX > AZIMUTH_THRESHOLD) {
    		Log.v("**head UP**",moveCounter+"");
    		
    		scrollView.post(new Runnable() {
  	    	    @Override
  	    	    public void run() {
  	    	    	Log.v("*SCROLL DOWN*","Ok");
  	    	    	
  	    	    	//if(moveCounter >0 && moveCounter<=phoneNrList.size()-1){
  	    	    		Contactor caller = phoneNrList.get(moveCounter); 
  	  	    	    	selectedPhoneNr= caller.getPhoneNr();
  	  	    	    	replyToNr.setText(caller.getPhoneNr()+"  "+caller.getPhoneNrType());
  	  	    	    	replyToName.setText(caller.getName());
  	  	    	    	
  	  	    	    	
  	  	    	    	
  	    	    	//}
  	    	    	if(moveCounter>0){
  	    	    		moveCounter--;
  	    	    	}
  	    	    	
  	    	        scrollView.smoothScrollBy(0, -scrollDist);
  	    	        
  	    	     
  	    	    } 
  	    	});
    		
    	}
    }
    
    
    
    @Override
    public void onStart(){
    	super.onStart();
    	
    	phoneNrList = new ArrayList<Contactor>();
    	contentView = (ViewGroup)getWindow().getDecorView();
    	missedCallsLinearLayout = (LinearLayout) contentView.findViewById(R.id.missed_calls);
    	
    	replyToNr = (TextView) contentView.findViewById(R.id.reply_to_nr);
    	replyToName = (TextView) contentView.findViewById(R.id.reply_to_name);
    	scrollView = (ScrollView) contentView.findViewById(R.id.phonebook_scrollview);
    	removeMissedCallsShowingOnScreen();
    	getMissedCalls();
    }
	
     @Override
	 protected void onResume() {
	    super.onResume();
	    
	    sensorMgr.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);
	    sensorMgr.registerListener(this, rotatSensor, SensorManager.SENSOR_DELAY_NORMAL);
	   
	}
	
    @Override
	protected void onPause() {
	    super.onPause();
	    
	    sensorMgr.unregisterListener(this);
	
	}
    
    @Override
    protected void onStop (){
    	super.onStop();
    	
    }
	
	private void getMissedCalls(){
		
		String[] projection = { CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.DURATION, CallLog.Calls.TYPE, 
								CallLog.Calls.IS_READ, CallLog.Calls.NEW, CallLog.Calls.CACHED_NAME,
								CallLog.Calls.CACHED_NUMBER_LABEL, CallLog.Calls.CACHED_NUMBER_TYPE};
	       String where = CallLog.Calls.TYPE+"="+CallLog.Calls.MISSED_TYPE;   
	       Log.v("*WHERE clause*", where);
	       Cursor c = this.getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, where, null, null);
	       
	       int nrOfMissedCalls = 0;
	       if (c.moveToLast()){
	    	   String callerName=getString(R.string.Unknown);
	    	   String nrLabel=getString(R.string.Unknown);
	    	   String nrType=getString(R.string.Unknown);
	    	   
	    	   while(c.moveToPrevious()){
	    		   
	    		   String callType = c.getString(c.getColumnIndex(CallLog.Calls.TYPE));
	    		   String isRead = c.getString(c.getColumnIndex(CallLog.Calls.IS_READ));
	    		   String isNewCall = c.getString(c.getColumnIndex(CallLog.Calls.NEW));
	    		  
	    		   Log.v("CallType", callType);
	    		   Log.v("isRead", isRead);
	    		   Log.v("isNewCall", isNewCall);
	    		   if((Integer.parseInt(callType)==CallLog.Calls.MISSED_TYPE) /*&& (Integer.parseInt(isNewCall)==0)*/){
	    			   String phoneNr = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
		    		   String callDate = c.getString(c.getColumnIndex(CallLog.Calls.DATE));
		    		   String formatedCalldate = sensorUtil.getDate(Long.parseLong(callDate), "E, dd.M.yyyy hh:mm:ss"); 
		    		   
		    		   String nameGot = c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME));
		    		   String nrLabelGot = c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NUMBER_LABEL));
		    		   String nrTypeGot = c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE));
		    		   //String callDuration = c.getString(c.getColumnIndex(CallLog.Calls.DURATION));
		    		   
		    		   if(nameGot!=null){
		    			   callerName = nameGot;
		    		   }
		    		   if(nrLabelGot!=null){
		    			   nrLabel = nrLabelGot;
		    		   }
		    		   if(nrTypeGot!=null){
		    			   Integer typeInt = Integer.parseInt(nrTypeGot);
		    			   switch(typeInt.intValue()){
		    			   case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
		    				   nrType = getString(R.string.home);
		    				   break;
		    			   case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
		    				   nrType = getString(R.string.mobile);
		    				   break;
		    			   case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
		    				   nrType = getString(R.string.work);
		    				   break;
		    			   case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
		    				   nrType = getString(R.string.work_mobile);
		    				   break;
		    			   case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
		    				   nrType = getString(R.string.other);
		    				   break;
		    			   
		    				   
		    			   }
		    			  
		    		   }
		    		   
		    		   nrOfMissedCalls++;
		    		   
		    		   Log.v("all INFO", phoneNr+", "+formatedCalldate+", "+"noDur"+", "+callType+", "+ isRead+", "+isNewCall+", "+callerName+", "+nrLabel+", "+nrType);
		    		   
		    		   populateMissedCallList(callerName, formatedCalldate, phoneNr,  nrType,  missedCallsLinearLayout);
		    		   
		    		   Contactor caller = new Contactor(callerName, phoneNr, nrType);
		    		   phoneNrList.add(caller);
	    		   }
	    		   
	    		   /*
	    		   String[] columnNames = c.getColumnNames();
		    
		    	   for(int i=0; i<columnNames.length; i++){
		    		   Log.v("*columnNames[i]*", columnNames[i]);
		    		   Log.v("*c.getColumnIndex(columnNames[i])*", c.getColumnIndex(columnNames[i])+"");
		    		   
		    		   Log.d("Cursor result",c.getString(c.getColumnIndex(columnNames[i])));
		    	   }
		    	   */
	    	   }
	    	  
	    	 
	       }
	    	
	       Log.v("NR of missed CALL", ""+c.getCount());
	       c.close(); 
	       
	       for(int i=0; i<nrOfMissedCalls; i++){
	    	   populateMissedCallList("", "", "",  "",  missedCallsLinearLayout);
	       }
	       
	       TextView nrOfMissedCallsField = (TextView) missedCallsLinearLayout.findViewById(R.id.nr_of_missed_calls);
	       nrOfMissedCallsField.setText(getString(R.string.nr_of_missed_calls)+" "+ String.valueOf(nrOfMissedCalls));
	   
	}
	
	private void populateMissedCallList(String callerName, String callDate, String callNr, String callNrType, LinearLayout container){
		RelativeLayout missedCallRow = (RelativeLayout) inflater.inflate(R.layout.missed_call_row, null);
		TextView callerNameField = (TextView)missedCallRow.findViewById(R.id.caller_name);
		TextView callDateField = (TextView)missedCallRow.findViewById(R.id.call_date);
		TextView callerNrField = (TextView)missedCallRow.findViewById(R.id.call_nr);
		TextView callNrTypeField =(TextView)missedCallRow.findViewById(R.id.call_nr_type);
		
		callerNameField.setText(callerName);
		callDateField.setText(callDate);
		callerNrField.setText(callNr);
		callNrTypeField.setText(callNrType);
		
		container.addView(missedCallRow);
	}
	
	private void removeMissedCallsShowingOnScreen(){
		int nrOfChildViews = missedCallsLinearLayout.getChildCount();
		if(nrOfChildViews>1){
			for(int i=1; i<nrOfChildViews; i++){
				missedCallsLinearLayout.removeViewAt(1);
			}
			
		}
		
	}

}
