package com.duff.timetracker.simpledb;

import android.accounts.NetworkErrorException;
import android.util.Log;
import com.amazonaws.AmazonClientException;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.duff.timetracker.AppPreferences;
import com.duff.timetracker.RemoteAccess;
import com.duff.timetracker.TimeEntryRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * the SimpleDB implementation for remote access.
 */
public class SimpleDBAccess implements RemoteAccess  {
	private static String TAG = "TimeTracker";

	public boolean login(String server, String userName, String password) throws NetworkErrorException {
		return false;
	}

	public boolean logout() throws NetworkErrorException {
		return false;
	}

	public boolean isLoggedIn() {
		return true;
	}

	public ArrayList<TimeEntryRecord> getAllEntries() throws NetworkErrorException {

		ArrayList<TimeEntryRecord> records = new ArrayList<TimeEntryRecord>();
		
		try {
			List<Item> items = SimpleDB.getItemNamesForDomainFromUser(SimpleDB.DOMAIN_NAME, AppPreferences.getUserName());

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
					if (name.equals(SimpleDB.NOTES_ATTRIBUTE_NAME)) record.setNotes(value);
				}
				records.add(record);
			}

			Log.d(TAG, "items: " + items);
		} catch (AmazonClientException e) {
			Log.e(TAG,"AmazonClientException: " + e);
			throw new NetworkErrorException(e);
		}		
		return records;
	}

	public void addNewEntry(TimeEntryRecord entry) throws NetworkErrorException {
		try {
			SimpleDB.createDomain(SimpleDB.DOMAIN_NAME);  //no harm if already created

			String newItem = java.util.UUID.randomUUID().toString();
			SimpleDB.createItem(SimpleDB.DOMAIN_NAME, newItem);

			SimpleDB.createAttributeForItem(SimpleDB.DOMAIN_NAME, newItem, SimpleDB.USER_ATTRIBUTE_NAME, AppPreferences.getUserName());

			String date = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(new java.util.Date());
			SimpleDB.createAttributeForItem(SimpleDB.DOMAIN_NAME, newItem, SimpleDB.DATE_ATTRIBUTE_NAME, date);

			SimpleDB.createAttributeForItem(SimpleDB.DOMAIN_NAME, newItem, SimpleDB.PROJECT_ATTRIBUTE_NAME, entry.getProject());
			SimpleDB.createAttributeForItem(SimpleDB.DOMAIN_NAME, newItem, SimpleDB.TASK_ATTRIBUTE_NAME, entry.getTask());
			SimpleDB.createAttributeForItem(SimpleDB.DOMAIN_NAME, newItem, SimpleDB.HOURS_ATTRIBUTE_NAME, entry.getHours());
			SimpleDB.createAttributeForItem(SimpleDB.DOMAIN_NAME, newItem, SimpleDB.NOTES_ATTRIBUTE_NAME, entry.getNotes());			
		} catch (AmazonClientException e) {
			throw new NetworkErrorException(e);
		}
	}
}
