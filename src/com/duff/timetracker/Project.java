package com.duff.timetracker;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class Project {
	private String mProjectName;
	private List<String> mTasks = new ArrayList<String>();
	private List<TimeEntryRecord> mTimeEntryRecords = new ArrayList<TimeEntryRecord>();  //todo: store records with project so we can build summaries
	
	public Project(String projectName) {
		mProjectName = projectName;
	}
	
	public void addTask(String task) {
		mTasks.add(task);
	}
	
	public String getName() {
		return mProjectName;
	}
	
	public List<String> getTasks() {
		return mTasks;
	}
}
