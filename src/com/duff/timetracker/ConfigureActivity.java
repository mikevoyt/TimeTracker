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
				postData();
				//readAndDumpTimesheetEntries();


				try {
					//getServerData();
				} catch (Exception e) {
					Log.d(TAG, e.toString());
				}
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


	public void postData() {


		//CredentialsProvider credProvider = new BasicCredentialsProvider();
		//credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
				//new UsernamePasswordCredentials("example@railstutorial.org", "foobar"));
		//
		//DefaultHttpClient http = new DefaultHttpClient();

		// Create a new HttpClient and Post Header
		DefaultHttpClient httpclient = new DefaultHttpClient();
		//httpclient.setCredentialsProvider(credProvider);




		HttpPost httppost = new HttpPost("http://10.0.2.2:3000/timesheet_entries.json");
		//httppost.setHeader("Accept", "application/json");
		httppost.setHeader("Content-Type", "application/json; charset=utf-8");

		//httpclient.getCredentialsProvider().setCredentials(
		//		new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
		//		new UsernamePasswordCredentials("example@railstutorial.org", "foobar"));

		byte[] encodeString = Base64.encode("example@railstutorial.org:foobar".getBytes(), Base64.NO_WRAP);
		String d = new String(encodeString);
		Log.d(TAG, d);
		//should be ZXhhbXBsZUByYWlsc3R1dG9yaWFsLm9yZzpmb29iYXI=
		httppost.setHeader("Authorization", "Basic " + d);
				//Base64.encode("example@railstutorial.org:foobar".getBytes(), Base64.NO_WRAP));

		try {
			// Add your data
			//List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			//nameValuePairs.add(new BasicNameValuePair("authenticity_token", "ZAiU8Rq1fUaJvoS6YfGGhRn2MNDvUxoVQs/M6aR47Uc="));
			//nameValuePairs.add(new BasicNameValuePair("session[email]", "example@railstutorial.org"));
			//nameValuePairs.add(new BasicNameValuePair("session[password]", "foobar"));
			//httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			StringEntity se = new StringEntity("{\"project_name\":\"from android\",\"hours\":1}");
			//StringEntity se = new StringEntity("{}");
			httppost.setEntity(se);

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			Log.d(TAG, response.toString());

		} catch (ClientProtocolException e) {
			Log.e(TAG, e.toString());
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
	}

	public ArrayList<String> getServerData() throws JSONException, ClientProtocolException, IOException {
		ArrayList<String> stringData = new ArrayList<String>();


		CredentialsProvider credProvider = new BasicCredentialsProvider();
		credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials("example@railstutorial.org", "foobar"));
		//
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.setCredentialsProvider(credProvider);


		ResponseHandler<String> resonseHandler = new BasicResponseHandler();
		//special development host IP address
		HttpPost postMethod = new HttpPost("https://10.0.2.2:3000/timesheet_entries.json");
		postMethod.setHeader("Content-type", "application/json");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("key", ""); //Data being sent to the server, which should produce a reply

		nameValuePairs.add(new BasicNameValuePair("jsonString", jsonObject.toString()));
		postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		String response = httpClient.execute(postMethod, resonseHandler);

		JSONObject jsonResponse = new JSONObject(response);

		JSONArray serverData1 = jsonResponse.getJSONArray("data1");
		JSONArray serverData2 = jsonResponse.getJSONArray("data2");
		for(int i = 0; i < serverData1.length() && i < serverData2.length(); i++) {
			//Do something with the data
		}

		return null; //the data;
	}

}