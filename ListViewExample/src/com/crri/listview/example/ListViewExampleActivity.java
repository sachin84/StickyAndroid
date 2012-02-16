package com.crri.listview.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class ListViewExampleActivity extends TabActivity {

	public static final String LIST_EXAMPLE = "ListExample";

	private static List<StickyData> stickyDataList = new ArrayList<StickyData>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);

		// Context mContext = getApplicationContext();
		SystemVariable appState = ((SystemVariable) getApplicationContext());
		// appState.setLoginStatus("System Variable");
		Log.w(LIST_EXAMPLE, "STATE_VAR==>" + appState.getLoginStatus());

		setContentView(R.layout.main);

		// LayoutInflater inflater = LayoutInflater.from(getBaseContext());
		// View publicView = inflater.inflate(R.layout.public_layout,null);

		// ListView l1 = (ListView) publicView.findViewById(R.id.PublicListing);
		// ColorDrawable divcolor = new ColorDrawable(Color.DKGRAY);
		// l1.setDivider(divcolor);
		// l1.setDividerHeight(2);
		//
		// l1.setOnItemClickListener(new OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		// long arg3) {
		// // arg1.setBackgroundColor(Color.YELLOW);
		//
		// Toast.makeText(getBaseContext(),
		// "You clciked " + stickyDataList.get(arg2).getId(),
		// Toast.LENGTH_LONG).show();
		// }
		// });
		//
		// StickyListAdapter stickyAdapter = new StickyListAdapter(this);
		// l1.setAdapter(stickyAdapter);

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
			Intent newStickyIntent = new Intent(ListViewExampleActivity.this,
					NewSticky.class);
			// startActivity(newStickyIntent);
			startActivityForResult(newStickyIntent, 1);

			SharedPreferences settings = getSharedPreferences("StickySettings", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.remove("logged");
			editor.commit();
			// finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 1) {
			Log.i(LIST_EXAMPLE, "NEW NOTE ACTIVITY CLOSED");
		}
	}

}