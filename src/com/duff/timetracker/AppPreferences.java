package com.duff.timetracker;

import android.app.Activity;
import android.content.SharedPreferences;
import com.duff.timetracker.simpledb.SimpleDB;

/**
 *
 */
public class AppPreferences {
		
	public static final String PREFS_NAME = "TimeTrackerPreferences";
	static private SharedPreferences mSettings;

	public static void initAppPreferences(Activity activity) {
		mSettings = activity.getSharedPreferences(PREFS_NAME, 0);
	}
	
	public static String getUserName() {
		return "Mike Voytovich";
		//return mSettings.getString(SimpleDB.USER_ATTRIBUTE_NAME, null);
	}

}
