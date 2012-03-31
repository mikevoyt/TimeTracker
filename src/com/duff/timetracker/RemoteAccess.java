package com.duff.timetracker;

import android.accounts.NetworkErrorException;

import java.util.ArrayList;

/**
 *
 */
public interface RemoteAccess {
	ArrayList<TimeEntryRecord> getAllEntries() throws NetworkErrorException;
	void addNewEntry(TimeEntryRecord entry) throws NetworkErrorException;
}
