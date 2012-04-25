package com.duff.timetracker;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.amazonaws.AmazonClientException;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.duff.timetracker.restapi.RestAPIAccess;
import com.duff.timetracker.simpledb.SimpleDB;
import com.duff.timetracker.simpledb.SimpleDBAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Displays details of all of this user's entries
 */
public class DetailsActivity extends ListActivity {
	
	private final String TAG = "TimeTracker";
	private Context mContext;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
	}

	@Override
	protected void onResume() {
		super.onResume();

		//clear list
		setListAdapter(new TimeEntryAdapter(mContext, R.layout.list_item, new ArrayList<TimeEntryRecord>()));

		LoadEntriesTask loadEntriesTask = new LoadEntriesTask();
		loadEntriesTask.execute();
	}


	private class LoadEntriesTask extends AsyncTask<String, Void, String> {

		private ProgressDialog mProgressDialog;
		ArrayList<TimeEntryRecord> mRecords;
		private  NetworkErrorException mException = null;
		private boolean mNeedsLogin;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage("Loading data...");
			mProgressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {

			try {
				RemoteAccess remoteAccess = new RestAPIAccess(); //swap this out for different back-ends
				if (!remoteAccess.isLoggedIn()) {
					mNeedsLogin = true;
				} else {
					mRecords = remoteAccess.getAllEntries();
				}

			} catch (NetworkErrorException e) {
				Log.e(TAG,"NetworkErrorException: " + e);
				mException = e;
			}

			return null;
		}

		//todo: move updating the list adapter to onProgressUpdate

		@Override
		protected void onPostExecute(String result) {
			mProgressDialog.hide();
			if (mNeedsLogin) {
				Toast toast = Toast.makeText(mContext, "Please log in", 3000);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			else if (mException != null)  {
				Toast toast = Toast.makeText(mContext, "Network error, please try again", 3000);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			} else {
				setListAdapter(new TimeEntryAdapter(mContext, R.layout.list_item, mRecords));

			}
		}
	}	
}