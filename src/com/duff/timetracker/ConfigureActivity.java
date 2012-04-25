package com.duff.timetracker;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.duff.timetracker.restapi.RestAPIAccess;


/**
 * Allow user to login/logout/signup
 */
public class ConfigureActivity extends Activity {

	private final String TAG = "TimeTracker";

	private EditText mUserNameEditText;
	private EditText mPasswordEditText;
	private Button mLoginButton;
	private TextView mLoggedInAsEditText;
	private Button mLogoutButton;
	private CheckBox mLocalServerCheckBox;
	private View mLoginView;
	private View mLogoutView;


	public void onCreate(Bundle savedInstanceState) {
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

		final Activity mContext = this;
		mLoginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				boolean loggedIn = false;
				String username = mUserNameEditText.getText().toString();
				String password = mPasswordEditText.getText().toString();
				String server = mLocalServerCheckBox.isChecked() ? RestAPIAccess.LOCAL_SERVER_URL : RestAPIAccess.REMOTE_SERVER_URL;
				Log.d(TAG, "logging in with: username=" + username + ", password=" + password);
				final RemoteAccess remote = new RestAPIAccess();
				String loginMessage = "Logged In";
				try {
					loggedIn = remote.login(server, username, password);
					if (!loggedIn) {
						loginMessage = "Login Failed";
					}

				} catch (NetworkErrorException e) {
					loginMessage = "Network error, please try again";
				}

				Toast toast = Toast.makeText(mContext, loginMessage, 3000);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

				if (loggedIn) {
					mLogoutView.setVisibility(View.VISIBLE);
					setContentView(mLogoutView);
					mLoggedInAsEditText.setText("Logged in as:" + username);
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
		});

	}

}