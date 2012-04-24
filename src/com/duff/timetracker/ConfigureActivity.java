package com.duff.timetracker;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.duff.timetracker.restapi.RestAPIAccess;


/**
 * Allow user to login/logout/signup
 */
public class ConfigureActivity extends Activity {

	private final String TAG = "TimeTracker";

	private EditText mUserNameEditText;
	private EditText mPasswordEditText;
	private Button mLoginButton;
	private CheckBox mLocalServerCheckBox;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure);
		mUserNameEditText = (EditText)findViewById(R.id.userName);
		mPasswordEditText = (EditText)findViewById(R.id.password);
		mLoginButton = (Button)findViewById(R.id.login);
		mLocalServerCheckBox = (CheckBox)findViewById(R.id.localServer);


		final Activity mContext = this;
		mLoginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				boolean loggedIn;
				String username = mUserNameEditText.getText().toString();
				String password = mPasswordEditText.getText().toString();
				String server = mLocalServerCheckBox.isChecked() ? RestAPIAccess.LOCAL_SERVER_URL : RestAPIAccess.REMOTE_SERVER_URL;
				Log.d(TAG, "logging in with: username=" + username + ", password=" + password);
				RemoteAccess remote = new RestAPIAccess();
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
			}
		});

	}

}