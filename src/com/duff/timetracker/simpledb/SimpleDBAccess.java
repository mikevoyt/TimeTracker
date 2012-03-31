package com.duff.timetracker.simpledb;

import android.accounts.NetworkErrorException;
import com.amazonaws.AmazonClientException;
import com.duff.timetracker.AppPreferences;
import com.duff.timetracker.RemoteAccess;
import com.duff.timetracker.TimeEntryRecord;

import java.util.ArrayList;

/**
 *
 */
public class SimpleDBAccess implements RemoteAccess {
	public ArrayList<TimeEntryRecord> getAllEntries() {
		return null;
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
