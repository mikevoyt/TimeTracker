package com.duff.timetracker;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.duff.timetracker.restapi.RestAPIAccess;



/**
 * Allow user to login/logout/signup
 */
public class ConfigureActivity extends Activity {

	private final String TAG = "TimeTracker";

	private Context mContext;
	private EditText mUserNameEditText;
	private EditText mPasswordEditText;
	private Button mLoginButton;
	private TextView mLoggedInAsEditText;
	private Button mLogoutButton;
	private CheckBox mLocalServerCheckBox;
	private View mLoginView;
	private View mLogoutView;


	public void onCreate(Bundle savedInstanceState) {
		mContext = this;
		super.onCreate(savedInstanceState);
		mLoginView = getLayoutInflater().inflate(R.layout.login, null);
		mUserNameEditText = (EditText)mLoginView.findViewById(R.id.userName);
		mPasswordEditText = (EditText)mLoginView.findViewById(R.id.password);
		mLoginButton = (Button)mLoginView.findViewById(R.id.login);
		mLocalServerCheckBox = (CheckBox)mLoginView.findViewById(R.id.localServer);

		mLogoutView = getLayoutInflater().inflate(R.layout.logout, null);
		mLoggedInAsEditText = (TextView)mLogoutView.findViewById(R.id.loggedInLabel);
		mLogoutButton = (Button)mLogoutView.findViewById(R.id.logout);

		setContentView(mLoginView);

		mLoginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				LoginTask loginTask = new LoginTask();
				loginTask.execute();
			}
		});

	}

	private class LoginTask extends AsyncTask<String, Void, String> {

		private ProgressDialog mProgressDialog;
		private  NetworkErrorException mException = null;
		private String mLoginMessage = "Logged In";
		private boolean mLoggedIn = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage("Logging in...");
			mProgressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {


			String username = mUserNameEditText.getText().toString();
			String password = mPasswordEditText.getText().toString();
			String server = mLocalServerCheckBox.isChecked() ? RestAPIAccess.LOCAL_SERVER_URL : RestAPIAccess.REMOTE_SERVER_URL;
			Log.d(TAG, "logging in with: username=" + username + ", password=" + password);
			final RemoteAccess remote = new RestAPIAccess();
			try {
				mLoggedIn = remote.login(server, username, password);
				if (!mLoggedIn) {
					mLoginMessage = "Login Failed";
				}

			} catch (NetworkErrorException e) {
				mLoginMessage = "Network error, please try again";
			}


			return null;
		}

		//todo: move updating the list adapter to onProgressUpdate

		@Override
		protected void onPostExecute(String result) {
			mProgressDialog.hide();

			Toast toast = Toast.makeText(mContext, mLoginMessage, 3000);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();

			if (mLoggedIn) {
				mLogoutView.setVisibility(View.VISIBLE);
				setContentView(mLogoutView);
				final RemoteAccess remote = new RestAPIAccess();
				mLoggedInAsEditText.setText("Logged in as:  " + mUserNameEditText.getText());
				mLogoutButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						try {
							remote.logout();

						} catch (NetworkErrorException e) {
							Toast toast = Toast.makeText(mContext, "Error Logging Out", 3000);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}

						setContentView(mLoginView);
					}
				});
			}

		}
	}

}