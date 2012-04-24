package com.duff.timetracker.restapi;

import android.accounts.NetworkErrorException;
import android.util.Base64;
import android.util.Log;
import com.amazonaws.AmazonClientException;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.duff.timetracker.AppPreferences;
import com.duff.timetracker.RemoteAccess;
import com.duff.timetracker.TimeEntryRecord;
import com.duff.timetracker.simpledb.SimpleDB;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class RestAPIAccess implements RemoteAccess {

	public static final String REMOTE_SERVER_URL = "https://dufftimesheet.herokuapp.com/timesheet_entries.json";
	public static final String LOCAL_SERVER_URL = "http://10.0.2.2:3000/timesheet_entries.json";
	private final String TAG = "TimeTracker";
	private static String mServer;
	private static String mUserName;
	private static String mPassword;
	private static boolean mLoggedIn;

	public boolean login(String server, String userName, String password) throws NetworkErrorException {
		mLoggedIn = false;
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(server);
		httpGet.setHeader("Content-Type", "application/json; charset=utf-8");

		byte[] encodeString = Base64.encode((userName + ":" + password).getBytes(), Base64.NO_WRAP);
		String d = new String(encodeString);
		httpGet.setHeader("Authorization", "Basic " + d);

		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				mServer = server;
				mUserName = userName;
				mPassword = password;
				mLoggedIn = true;
			} else {
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return mLoggedIn;
	}

	public boolean logout() throws NetworkErrorException {
		mLoggedIn = false;
		return false;
	}

	public boolean isLoggedIn() {
		return mLoggedIn;
	}

	public ArrayList<TimeEntryRecord> getAllEntries() throws NetworkErrorException {

		ArrayList<TimeEntryRecord> records = new ArrayList<TimeEntryRecord>();

		String feed = readTimesheetEntries();

		try {
			JSONArray jsonArray = new JSONArray(feed);
			Log.i(TAG, "Number of entries " + jsonArray.length());
			for (int i = 0; i < jsonArray.length(); i++) {
				TimeEntryRecord record = new TimeEntryRecord();
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				record.setProject(jsonObject.getString("project_name"));
				record.setTask(jsonObject.getString("task_name"));
				double hours = jsonObject.getDouble("hours");
				DecimalFormat df = new DecimalFormat("#.#");
				record.setHours(df.format(hours));
				record.setDate(jsonObject.getString("performed_on"));
				String notes = jsonObject.getString("notes");
				if (notes == null) notes = "";
				record.setNotes(notes);
				records.add(record);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		 return records;
	}

	public void addNewEntry(TimeEntryRecord entry) throws NetworkErrorException {

		// Create a new HttpClient and Post Header
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(mServer);
		httppost.setHeader("Content-Type", "application/json; charset=utf-8");

		byte[] encodeString = Base64.encode((mUserName + ":" + mPassword).getBytes(), Base64.NO_WRAP);
		String d = new String(encodeString);
		httppost.setHeader("Authorization", "Basic " + d);

		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("project_name", entry.getProject());
			jsonObject.put("task_name", entry.getTask());
			jsonObject.put("hours", Double.parseDouble(entry.getHours()));
			jsonObject.put("notes", entry.getNotes());
			jsonObject.put("performed_on", entry.getDate());
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

	public String readTimesheetEntries() {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(mServer);
		httpGet.setHeader("Content-Type", "application/json; charset=utf-8");

		byte[] encodeString = Base64.encode((mUserName + ":" + mPassword).getBytes(), Base64.NO_WRAP);
		String d = new String(encodeString);
		httpGet.setHeader("Authorization", "Basic " + d);

		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e(TAG, "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

}
