package com.duff.timetracker;

import android.accounts.NetworkErrorException;

import java.util.ArrayList;

/**
 *
 */
public interface RemoteAccess {
	boolean login(String server, String userName, String password) throws NetworkErrorException;
	boolean logout() throws NetworkErrorException;
	boolean isLoggedIn();
	ArrayList<TimeEntryRecord> getAllEntries() throws NetworkErrorException;
	void addNewEntry(TimeEntryRecord entry) throws NetworkErrorException;
}
