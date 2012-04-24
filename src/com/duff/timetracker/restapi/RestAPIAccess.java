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
import java.util.ArrayList;
import java.util.List;

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
				record.setHours(jsonObject.getString("hours").toString());
				//record.setDate(jsonObject.getString("date"));
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

	public String readTimesheetEntries() {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(SERVER_URL);
		httpGet.setHeader("Content-Type", "application/json; charset=utf-8");

		byte[] encodeString = Base64.encode((USER_NAME + ":" + PASSWORD).getBytes(), Base64.NO_WRAP);
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
