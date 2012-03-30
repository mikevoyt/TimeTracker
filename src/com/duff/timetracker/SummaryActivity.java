package com.duff.timetracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Displays a summary of user's hours
 */
public class SummaryActivity extends Activity {

	private final String TAG = "TimeTracker";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WebView webview = new WebView(this);
		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);

		String chart;
		String data = "        ['Work',    11],\n" +
				"        ['Eat',      2],\n" +
				"        ['Commute',  2],\n" +
				"        ['Watch TV', 2],\n" +
				"        ['Sleep',    7]";


		try {
			//URL url = new URL("file:///android_asset/chart.html");
			//chart = com.google.common.io.Resources.toString(url, Charset.defaultCharset());
			//webview.loadData(chart, "text/html; charset=UTF-8", null);

			InputStream chartIs = getResources().openRawResource(R.raw.chart);
			String string = CharStreams.toString(new InputStreamReader(chartIs, "UTF-8"));
			webview.loadData(string, "text/html; charset=UTF-8", null);
			setContentView(webview);
		} catch (IOException e) {
			Log.d(TAG, "exception:" + e);
		}
	}
}