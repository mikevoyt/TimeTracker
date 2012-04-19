package com.duff.timetracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

/**
 * Allow user to login/logout/signup
 */
public class ConfigureActivity extends Activity {

	private final String TAG = "TimeTracker";
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

				//test: read timesheet entries
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
		});

	}


	//TODO: do this in a background task
	public String readTimesheetEntries() {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(
				"https://dufftimesheet.herokuapp.com/timesheet_entries.json");
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


}