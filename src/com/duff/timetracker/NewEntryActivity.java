package com.duff.timetracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.amazonaws.AmazonClientException;
import com.duff.timetracker.simpledb.SimpleDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows user to enter a new time tracking entry
 */
public class NewEntryActivity extends Activity
{
	private static String TAG = "TimeTracker";
	private static String ADD_NEW = "Add New...";

	private Spinner mProjectSpinner;
	private Spinner mTaskSpinner;
	private EditText mHoursEditText;
	private EditText mNotesEditText;
	private Button mSubmitButton;
	private Context mContext;
	private List<Project> mProjects = new ArrayList<Project>();
	Project mDefaultProject;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_entry);
		mProjectSpinner = (Spinner)findViewById(R.id.selectProject);
		mTaskSpinner = (Spinner)findViewById(R.id.selectTask);
		mHoursEditText = (EditText)findViewById(R.id.enterHours);
		mNotesEditText = (EditText)findViewById(R.id.notes);
		mSubmitButton = (Button)findViewById(R.id.submit);
		mContext = this;

		initProjects();
		initProjectSpinner();
		initTaskSpinner(mDefaultProject);
		initSubmitButton();
		AppPreferences.initAppPreferences(this);
		if (AppPreferences.getUserName() == null) {
			//todo: launch user-name dialog
		}
	}
	
	private void initProjects() {
		//todo: populate real data
		Project project = new Project("Duff");
		project.addTask("AWS and rails investigation");
		project.addTask("misc");
		mProjects.add(project);
		
		project = new Project("Nest");
		project.addTask("2.1 bugs");
		project.addTask("2.1 features");
		mProjects.add(project);

		mDefaultProject = project;
	}

	private void initProjectSpinner() {
		List<String> list = new ArrayList<String>();
		
		for (Project project : mProjects) {
			list.add(project.getName());
		}
		list.add(ADD_NEW);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mProjectSpinner.setAdapter(dataAdapter);
		mProjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				if (adapterView.getItemAtPosition(i).toString().equals(ADD_NEW)) {
					//bring up add new Project dialog
				} else {
					initTaskSpinner(mProjects.get(i));
				}
			}

			public void onNothingSelected(AdapterView<?> adapterView) {
			}
		});
	}

	private void initTaskSpinner(Project project) {
		List<String> list = new ArrayList<String>();
		
		for (String task : project.getTasks()) {
			list.add(task);
		}
		list.add(ADD_NEW);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mTaskSpinner.setAdapter(dataAdapter);
		mTaskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				if (adapterView.getItemAtPosition(i).toString().equals(ADD_NEW)) {
					//todo: bring up add new Project dialog
				}
			}

			public void onNothingSelected(AdapterView<?> adapterView) {
			}
		});
	}

	private void initSubmitButton() {
		mSubmitButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				if (AppPreferences.getUserName() == null) {
					//todo: launch user-name dialog
					return;
				}


				if (mHoursEditText.getText().toString().equals("")) {
					AlertDialog alertDialog = new AlertDialog.Builder(mContext).setTitle("Error").setMessage("Please enter hours").create();
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							return;
						} });
					alertDialog.show();
					return;
				}

				SubmitEntryTask task = new SubmitEntryTask();
				task.execute(null);
			}
		});
	}

	private class SubmitEntryTask extends AsyncTask<String, Void, String> {

		private ProgressDialog mProgressDialog;
		private AmazonClientException mException = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage("Sending entry...");
			mProgressDialog.show();

		}

		@Override
		protected String doInBackground(String... params) {
			String project = (String)mProjectSpinner.getSelectedItem();
			String task = (String)mTaskSpinner.getSelectedItem();
			String hours = mHoursEditText.getText().toString();
			String notes = mNotesEditText.getText().toString();

			try {
				SimpleDB.createDomain(SimpleDB.DOMAIN_NAME);

				String newItem = java.util.UUID.randomUUID().toString();
				SimpleDB.createItem(SimpleDB.DOMAIN_NAME, newItem);

				SimpleDB.createAttributeForItem(SimpleDB.DOMAIN_NAME, newItem, SimpleDB.USER_ATTRIBUTE_NAME, AppPreferences.getUserName());

				String date = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(new java.util.Date());
				SimpleDB.createAttributeForItem(SimpleDB.DOMAIN_NAME, newItem, SimpleDB.DATE_ATTRIBUTE_NAME, date);

				SimpleDB.createAttributeForItem(SimpleDB.DOMAIN_NAME, newItem, SimpleDB.PROJECT_ATTRIBUTE_NAME, project);
				SimpleDB.createAttributeForItem(SimpleDB.DOMAIN_NAME, newItem, SimpleDB.TASK_ATTRIBUTE_NAME, task);
				SimpleDB.createAttributeForItem(SimpleDB.DOMAIN_NAME, newItem, SimpleDB.HOURS_ATTRIBUTE_NAME, hours);
				SimpleDB.createAttributeForItem(SimpleDB.DOMAIN_NAME, newItem, SimpleDB.NOTES_ATTRIBUTE_NAME, notes);
			} catch (AmazonClientException e) {
				Log.e(TAG,"AmazonClientException: " + e);
				mException = e;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			mProgressDialog.hide();

			String displayText;
			if (mException != null) {
				displayText = "Network error, please try again";
			} else {
				mHoursEditText.setText("");
				mNotesEditText.setText("");
				displayText = "Submitted!";
			}
			Toast toast = Toast.makeText(mContext, displayText, 3000);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}

}
