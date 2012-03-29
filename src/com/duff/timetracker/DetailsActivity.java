package com.duff.timetracker;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
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
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


	}

	@Override
	protected void onResume() {
		super.onResume();

		List<Item> items = SimpleDB.getItemNamesForDomainFromUser(SimpleDB.DOMAIN_NAME, "Mike Voytovich");

		ArrayList<TimeEntryRecord> records = new ArrayList<TimeEntryRecord>();

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
			records.add(record);
		}

		Log.d(TAG, "items: " + items);
		setListAdapter(new TimeEntryAdapter(this, R.layout.list_item, records));
	}
}