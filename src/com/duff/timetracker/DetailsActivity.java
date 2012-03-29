package com.duff.timetracker;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.duff.timetracker.simpledb.SimpleDB;

import java.util.HashMap;
import java.util.List;

/**
 * Displays details of all of this user's entries
 */
public class DetailsActivity extends ListActivity {
	
	private final String TAG = "TimeTracker";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		List<Item> items = SimpleDB.getItemNamesForDomainFromUser(SimpleDB.DOMAIN_NAME, "Mike Voytovich");

		HashMap<String,String> attributeMap = new HashMap<String,String>(30);
		for (Item item : items) {
			String itemName = item.getName();
			List<Attribute> attributes = item.getAttributes();
			for (Attribute attribute : attributes) {
				String name = attribute.getName();
				String value = attribute.getValue();

				attributeMap.put(name, value);
			}
		}

		Log.d(TAG, "items: " + items);
		//setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, items.values()));
	}
}