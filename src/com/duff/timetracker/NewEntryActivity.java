package com.duff.timetracker;

import android.app.Activity;
import android.os.Bundle;

/**
 * Allows user to enter a new time tracking entry
 */
public class NewEntryActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
