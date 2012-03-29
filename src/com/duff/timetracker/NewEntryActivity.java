package com.duff.timetracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.duff.timetracker.simpledb.SimpleDB;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows user to enter a new time tracking entry
 */
public class NewEntryActivity extends Activity
{
	private static String ADD_NEW = "Add New...";

	private static String DOMAIN_NAME = "TimeTracker";
	private static String USER_ATTRIBUTE_NAME = "User";
	private static String DATE_ATTRIBUTE_NAME = "Date";
	private static String PROJECT_ATTRIBUTE_NAME = "Project";
	private static String TASK_ATTRIBUTE_NAME = "Task";
	private static String HOURS_ATTRIBUTE_NAME = "Hours";

	private Spinner mProjectSpinner;
	private Spinner mTaskSpinner;
	private EditText mHoursEditText;
	private Button mSubmitButton;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
				
        super.onCreate(savedInstanceState);
		setContentView(R.layout.new_entry);
		mProjectSpinner = (Spinner)findViewById(R.id.selectProject);
		mTaskSpinner = (Spinner)findViewById(R.id.selectTask);
		mHoursEditText = (EditText)findViewById(R.id.enterHours);
		mSubmitButton = (Button)findViewById(R.id.submit);

		initProjectSpinner();
		initTaskSpinner();
		initSubmitButton();
    }

	private void initProjectSpinner() {
		List<String> list = new ArrayList<String>();
		//TODO: get saved list
		list.add("Nest");
		list.add("Duff");
		list.add(ADD_NEW);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mProjectSpinner.setAdapter(dataAdapter);
		mProjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				if (adapterView.getItemAtPosition(i).toString().equals(ADD_NEW)) {
					//bring up add new Project dialog
				}
			}

			public void onNothingSelected(AdapterView<?> adapterView) {
				//To change body of implemented methods use File | Settings | File Templates.
			}
		});
	}

	private void initTaskSpinner() {
		List<String> list = new ArrayList<String>();
		//TODO: get saved list
		list.add("2.1 bugs");
		list.add("2.1 features");
		list.add(ADD_NEW);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mTaskSpinner.setAdapter(dataAdapter);
		mTaskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				if (adapterView.getItemAtPosition(i).toString().equals(ADD_NEW)) {
					//bring up add new Project dialog
				}
			}

			public void onNothingSelected(AdapterView<?> adapterView) {
				//To change body of implemented methods use File | Settings | File Templates.
			}
		});	}

	private void initSubmitButton() {
		mSubmitButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				SimpleDB.createDomain(DOMAIN_NAME);

				String newItem = java.util.UUID.randomUUID().toString();
				SimpleDB.createItem(DOMAIN_NAME, newItem);

				SimpleDB.createAttributeForItem(DOMAIN_NAME, newItem, USER_ATTRIBUTE_NAME, "Mike Voytovich");

				String date = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date());
				SimpleDB.createAttributeForItem(DOMAIN_NAME, newItem, DATE_ATTRIBUTE_NAME, date);

				SimpleDB.createAttributeForItem(DOMAIN_NAME, newItem, PROJECT_ATTRIBUTE_NAME, "Nest");
				SimpleDB.createAttributeForItem(DOMAIN_NAME, newItem, TASK_ATTRIBUTE_NAME, "Bug fixes");
				SimpleDB.createAttributeForItem(DOMAIN_NAME, newItem, HOURS_ATTRIBUTE_NAME, "1.5");
			}
		});
	}
}
