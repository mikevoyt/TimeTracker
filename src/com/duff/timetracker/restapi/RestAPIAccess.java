package com.duff.timetracker.restapi;

import android.accounts.NetworkErrorException;
import com.duff.timetracker.RemoteAccess;
import com.duff.timetracker.TimeEntryRecord;

import java.util.ArrayList;

/**
 *
 */
public class RestAPIAccess implements RemoteAccess {

	public boolean login() throws NetworkErrorException {
		return false;
	}

	public boolean logout() throws NetworkErrorException {
		return false;
	}

	public ArrayList<TimeEntryRecord> getAllEntries() throws NetworkErrorException {

		return null;
	}

	public void addNewEntry(TimeEntryRecord entry) throws NetworkErrorException {
	}
}
