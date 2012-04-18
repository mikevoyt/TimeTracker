package com.duff.timetracker;

import android.accounts.NetworkErrorException;

import java.util.ArrayList;

/**
 *
 */
public interface RemoteAccess {
	boolean login() throws NetworkErrorException;
	boolean logout() throws NetworkErrorException;
	ArrayList<TimeEntryRecord> getAllEntries() throws NetworkErrorException;
	void addNewEntry(TimeEntryRecord entry) throws NetworkErrorException;
}
