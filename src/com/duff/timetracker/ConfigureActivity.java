package com.duff.timetracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Allow user to login/logout/signup
 */
public class ConfigureActivity extends Activity {

	private final String TAG = "TimeTracker";

	//the following stuff is hardcoded now for development
	private final String SERVER_URL = "http://10.0.2.2:3000/timesheet_entries.json";
	private final String USER_NAME = "example@railstutorial.org";
	private final String PASSWORD = "foobar";

	private EditText mUserNameEditText;
	private EditText mPasswordEditText;
	private Button mLoginButton;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure);
		mUserNameEditText = (EditText)findViewById(R.id.userName);
		mPasswordEditText = (EditText)findViewById(R.id.password);
		mLoginButton = (Button)findViewById(R.id.login);

		mLoginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String username = mUserNameEditText.getText().toString();
				String password = mPasswordEditText.getText().toString();
				Log.d(TAG, "logging in with: username=" + username + ", password=" + password);

				//postData();
				readAndDumpTimesheetEntries();


			}
		});

	}

	private void readAndDumpTimesheetEntries() {
		String readTwitterFeed = readTimesheetEntries();

		try {
			JSONArray jsonArray = new JSONArray(readTwitterFeed);
			Log.i(ConfigureActivity.class.getName(),
					"Number of entries " + jsonArray.length());
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Log.i(ConfigureActivity.class.getName(), jsonObject.getString("notes"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	//TODO: do this in a background task
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
				Log.e(ConfigureActivity.class.toString(), "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}


	public void postData() {
		// Create a new HttpClient and Post Header
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(SERVER_URL);
		httppost.setHeader("Content-Type", "application/json; charset=utf-8");

		byte[] encodeString = Base64.encode((USER_NAME + ":" + PASSWORD).getBytes(), Base64.NO_WRAP);
		String d = new String(encodeString);
		httppost.setHeader("Authorization", "Basic " + d);

		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("project_name", "yay JSONObject!!"); //Data being sent to the server, which should produce a reply
			jsonObject.put("hours", 4); //Data being sent to the server, which should produce a reply
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