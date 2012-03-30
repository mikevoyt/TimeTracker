package com.duff.timetracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TimeEntryAdapter extends ArrayAdapter<TimeEntryRecord> {

	private ArrayList<TimeEntryRecord> records;

	public TimeEntryAdapter(Context context, int textViewResourceId, ArrayList<TimeEntryRecord> records) {
		super(context, textViewResourceId, records);
		this.records = records;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_item, null);
		}

		TimeEntryRecord record = records.get(position);
		if (record != null) {
			TextView date = (TextView) v.findViewById(R.id.date);
			TextView project = (TextView) v.findViewById(R.id.project);
			TextView task = (TextView) v.findViewById(R.id.task);
			TextView hours = (TextView) v.findViewById(R.id.hours);
			TextView notes = (TextView) v.findViewById(R.id.notes);


			date.setText(record.getDate());
			project.setText(record.getProject());
			task.setText(record.getTask());
			hours.setText(record.getHours());
			notes.setText(record.getNotes());
		}
		return v;
	}

}
