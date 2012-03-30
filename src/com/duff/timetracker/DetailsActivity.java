package com.duff.timetracker;

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
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.duff.timetracker.simpledb.SimpleDB;

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

		LoadEntriesTask loadEntriesTask = new LoadEntriesTask();
		loadEntriesTask.execute();
	}


	private class LoadEntriesTask extends AsyncTask<String, Void, String> {

		private ProgressDialog mProgressDialog;
		ArrayList<TimeEntryRecord> mRecords;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage("Loading data...");
			mProgressDialog.show();

		}

		@Override
		protected String doInBackground(String... params) {

			List<Item> items = SimpleDB.getItemNamesForDomainFromUser(SimpleDB.DOMAIN_NAME, AppPreferences.getUserName());

			mRecords = new ArrayList<TimeEntryRecord>();

			for (Item item : items) {
				String itemName = item.getName();
				List<Attribute> attributes = item.getAttributes();
				TimeEntryRecord record = new TimeEntryRecord();
				for (Attribute attribute : attributes) {
					String name = attribute.getName();
					String value = attribute.getValue();
					if (name.equals(SimpleDB.DATE_ATTRIBUTE_NAME)) record.setDate(value);
					if (name.equals(SimpleDB.TASK_ATTRIBUTE_NAME)) record.setTask(value);
					if (name.equals(SimpleDB.PROJECT_ATTRIBUTE_NAME)) record.setProject(value);
					if (name.equals(SimpleDB.HOURS_ATTRIBUTE_NAME)) record.setHours(value);
				}
				mRecords.add(record);
			}

			Log.d(TAG, "items: " + items);

			return null;
		}

		//todo: move updating the list adapter to onProgressUpdate

		@Override
		protected void onPostExecute(String result) {
			mProgressDialog.hide();
			setListAdapter(new TimeEntryAdapter(mContext, R.layout.list_item, mRecords));
		}
	}	
}