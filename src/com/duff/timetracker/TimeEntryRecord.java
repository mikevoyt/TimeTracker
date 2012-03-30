package com.duff.timetracker;

/**
 * 
 */
public class TimeEntryRecord {
	private String mProject;
	private String mTask;
	private String mHours;
	private String mDate;
	private String mNotes;
	
	public TimeEntryRecord() {
	}
	
	public void setProject(String project) {
		this.mProject = project;
	}
	
	public void setTask(String task) {
		this.mTask = task;
	}
	
	public void setHours(String hours) {
		this.mHours = hours;
	}
	
	public void setDate(String date) {
		this.mDate = date;
	}
	
	public void setNotes(String notes) {
		this.mNotes = notes;
	}
	
	public String getProject() {
		return mProject;
	}
	
	public String getTask() {
		return mTask;
	}
	
	public String getHours() {
		return mHours;
	}
	
	public String getDate() {
		return mDate;
	}
	
	public String getNotes() {
		return mNotes;
	}
}
