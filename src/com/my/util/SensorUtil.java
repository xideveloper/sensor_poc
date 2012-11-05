package com.my.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import roboguice.inject.ContextSingleton;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.inject.Inject;

@ContextSingleton
public class SensorUtil {
	
	@Inject private SharedPreferences mSharedPreference;

	public boolean containsKey(String key){
		return mSharedPreference.contains(key);
	}
	
	public long getLongValue(String key, long defaultVal) {
		return mSharedPreference.getLong(key, defaultVal);
	}

	public void setLongValue(String key, long value) {
		Editor editor = mSharedPreference.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	public String getStringValue(String key, String defaultVal) {
		return mSharedPreference.getString(key, defaultVal);
	}

	public void setStringValue(String key, String value) {
		Editor editor = mSharedPreference.edit(); 
		editor.putString(key, value);
		editor.commit();
	}
	
	public String getDate(long milliSeconds, String dateFormat)
	{
	 
	    DateFormat formatter = new SimpleDateFormat(dateFormat);
	    formatter.setTimeZone(TimeZone.getTimeZone("UTC/GMT+2"));
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(milliSeconds);
	    return formatter.format(calendar.getTime());
	}
	

}
