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

		List<String> dates = new ArrayList<String>();

		for (Item item : items) {
			String itemName = item.getName();
			List<Attribute> attributes = item.getAttributes();
			for (Attribute attribute : attributes) {
				String name = attribute.getName();
				String value = attribute.getValue();
				if (name.equals(SimpleDB.DATE_ATTRIBUTE_NAME)) dates.add(value);

			}
		}

		Log.d(TAG, "items: " + items);
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, dates));
	}
}