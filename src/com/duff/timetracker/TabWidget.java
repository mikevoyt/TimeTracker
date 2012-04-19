package com.duff.timetracker;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class TabWidget extends TabActivity {
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost();  // The activity TabHost
		TabHost.TabSpec spec;  // Resusable TabSpec for each tab
		Intent intent;  // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, NewEntryActivity.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("newentry").setIndicator("Add New Entry",
				res.getDrawable(android.R.drawable.ic_menu_add))
				.setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, DetailsActivity.class);
		spec = tabHost.newTabSpec("details").setIndicator("Details",
				res.getDrawable(android.R.drawable.ic_menu_info_details))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, ConfigureActivity.class);
		spec = tabHost.newTabSpec("configure").setIndicator("Configure",
				res.getDrawable(android.R.drawable.ic_menu_preferences))
				.setContent(intent);
		tabHost.addTab(spec);


		tabHost.setCurrentTab(0);
	}	
}