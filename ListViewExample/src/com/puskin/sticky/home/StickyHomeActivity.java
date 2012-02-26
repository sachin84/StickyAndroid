package com.puskin.sticky.home;

import java.util.ArrayList;
import java.util.List;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class StickyHomeActivity extends TabActivity {

	public static final String STICKY_HOME = "ListExample";
	final int LOGIN_SUCCESS = 5;
	final int LOGIN_CANCELLED = -5;

	private static List<StickyData> stickyDataList = new ArrayList<StickyData>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);

		// SystemVariable appState = ((SystemVariable) getApplicationContext());
		// Log.w(STICKY_HOME, "STATE_VAR==>" + appState.getLoginStatus());

		setContentView(R.layout.main);

		SharedPreferences settings = getSharedPreferences("StickySettings", 0);
		int loggedInUserId = settings.getInt("loggedInUserId", 0);

		if (loggedInUserId >= 0) {
			Intent editStickyIntent = new Intent(StickyHomeActivity.this,
					Login.class);

			startActivityForResult(editStickyIntent, LOGIN_SUCCESS);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
		} else {
			Log.w(STICKY_HOME, "loggedInUserId==>" + loggedInUserId);

			// creating main screen
			createMainTab();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == LOGIN_SUCCESS) {
			Log.i(STICKY_HOME, "NEW NOTE ACTIVITY CLOSED");
			createMainTab();
		} else if (resultCode == LOGIN_CANCELLED) {
			Log.i(STICKY_HOME, "Login Cancelled Quiting App.");
			this.finish();
		} else {
			Log.i(STICKY_HOME, "Starting Tab Atcivity...");
		}
	}

	private void createMainTab() {

		SharedPreferences settings = getSharedPreferences("StickySettings", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("loggedInUserId", 2);
		editor.commit();

		Resources res = getResources(); // Resource object to get Drawables
		// Setting Up Tabs
		TabHost tabHost = getTabHost();

		// Tab for Photos
		TabSpec publicspec = tabHost.newTabSpec("Public");
		publicspec.setIndicator("",
				getResources().getDrawable(R.drawable.icon_public_tab));
		Intent publicIntent = new Intent(this, PublicActivity.class);
		publicspec.setContent(publicIntent);

		// Tab for Works
		TabSpec workspec = tabHost.newTabSpec("Work");
		// setting Title and Icon for the Tab
		workspec.setIndicator("",
				getResources().getDrawable(R.drawable.icon_work_tab));
		Intent workIntent = new Intent(this, WorkActivity.class);
		workspec.setContent(workIntent);

		// Tab for Videos
		TabSpec privatespec = tabHost.newTabSpec("Private");
		privatespec.setIndicator("",
				getResources().getDrawable(R.drawable.icon_private_tab));

		Intent privateIntent = new Intent(this, PrivateActivity.class);
		privatespec.setContent(privateIntent);

		// Adding all TabSpec to TabHost
		tabHost.addTab(publicspec); // Adding public tab
		tabHost.addTab(workspec); // Adding work tab
		tabHost.addTab(privatespec); // Adding private tab
		tabHost.setCurrentTab(0);
	}

	// To Create MENU
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater Inflater = getMenuInflater();
		Inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.newsticky) {
			Intent newStickyIntent = new Intent(StickyHomeActivity.this,
					NewSticky.class);
			// startActivity(newStickyIntent);
			startActivityForResult(newStickyIntent, 1);

			SharedPreferences settings = getSharedPreferences("StickySettings",
					0);
			SharedPreferences.Editor editor = settings.edit();
			editor.remove("logged");
			editor.commit();
			// finish();
		}
		return super.onOptionsItemSelected(item);
	}

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// super.onActivityResult(requestCode, resultCode, data);
	// if (resultCode == RESULT_OK && requestCode == 1) {
	// }
	// }

}