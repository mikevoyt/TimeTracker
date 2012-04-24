package com.duff.timetracker.restapi;

import android.accounts.NetworkErrorException;
import android.util.Base64;
import android.util.Log;
import com.duff.timetracker.RemoteAccess;
import com.duff.timetracker.TimeEntryRecord;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 */
public class RestAPIAccess implements RemoteAccess {

	private final String TAG = "TimeTracker";
	private final String SERVER_URL = "http://10.0.2.2:3000/timesheet_entries.json";
	private final String USER_NAME = "example@railstutorial.org";
	private final String PASSWORD = "foobar";


	public boolean login() throws NetworkErrorException {
		return false;
	}

	public boolean logout() throws NetworkErrorException {
		return false;
	}

	public ArrayList<TimeEntryRecord> getAllEntries() throws NetworkErrorException {

		return null;
	}

	public void addNewEntry(TimeEntryRecord entry) throws NetworkErrorException {

		// Create a new HttpClient and Post Header
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(SERVER_URL);
		httppost.setHeader("Content-Type", "application/json; charset=utf-8");

		byte[] encodeString = Base64.encode((USER_NAME + ":" + PASSWORD).getBytes(), Base64.NO_WRAP);
		String d = new String(encodeString);
		httppost.setHeader("Authorization", "Basic " + d);

		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("project_name", entry.getProject());
			jsonObject.put("task_name", entry.getTask());
			jsonObject.put("hours", Integer.parseInt(entry.getHours()));
			jsonObject.put("notes", entry.getNotes());
			StringEntity se = new StringEntity( jsonObject.toString());

			httppost.setEntity(se);

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			//todo: verify 201 code (created)
			Log.d(TAG, response.toString());

		} catch (ClientProtocolException e) {
			Log.e(TAG, e.toString());
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
	}


}
