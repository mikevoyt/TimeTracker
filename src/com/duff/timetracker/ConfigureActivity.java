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

			}
		});

	}

}