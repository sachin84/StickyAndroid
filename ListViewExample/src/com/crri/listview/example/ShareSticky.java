package com.crri.listview.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ShareSticky extends Activity {
	public static final String SHARE_STICKY = "New Sticky";
	static final int DATE_DIALOG_ID = 999;
	public int StickyId;
	public String StickyType;

	public String DueDate;
	public String Priority;
	public Bundle stickyDataBndl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sharesticky);
		 Intent intent = getIntent();
		stickyDataBndl = intent.getExtras();
		String action = intent.getAction();
		if (Intent.ACTION_SEND.equals(action))
	    {
			
//			EditText stickyEmailObj = (EditText) findViewById(R.id.shareEmail);
//			stickyEmailObj.setText(stickyDataBndl.getString("Text"));

			EditText stickyMsgObj = (EditText) findViewById(R.id.shareMessage);
			stickyMsgObj.setText(stickyDataBndl.getString("Text"));
			
		
	    }

		
		Button saveButton = (Button) findViewById(R.id.shareButton);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				createSticky();
				setResult(RESULT_OK);
				finish();
			}

		});
		
	}
	private boolean isNullOrBlank(String s) {
		return (s == null || s.trim().equals("") || s.trim().equals("null"));
	}
	
	
	public boolean createSticky() {

		boolean status = false;
		SharedPreferences settings = getSharedPreferences("StickySettings", 0);
		int loggedInUserId = settings.getInt("loggedInUserId", 0);

		try {
			// Add user name and password
			EditText stickyTextObj = (EditText) findViewById(R.id.stickyText);
			String stickyText = stickyTextObj.getText().toString();

			EditText stickyTitleObj = (EditText) findViewById(R.id.stickyTitle);
			String stickyTitle = stickyTitleObj.getText().toString();

			Spinner spnrStickyType = (Spinner) findViewById(R.id.stickyType);
			String stickyType = spnrStickyType.getSelectedItem().toString();

			Spinner spnrStickyPriority = (Spinner) findViewById(R.id.stickyPriority);
			String stickyPriority = spnrStickyPriority.getSelectedItem()
					.toString();

			TextView stickydueDateObj = (TextView) findViewById(R.id.dueDateText);
			String stickyDueDate = stickydueDateObj.getText().toString();

			Log.i(SHARE_STICKY, "stickyDueDate=" + stickyDueDate);
			Log.i(SHARE_STICKY, stickyTitle);
			Log.i(SHARE_STICKY, stickyPriority);
			Log.i(SHARE_STICKY, stickyText);
			Log.i(SHARE_STICKY, stickyType);
			Log.i(SHARE_STICKY, "loggedInUserId==" + loggedInUserId);

			if(stickyDueDate == "0-0-0")
				stickyDueDate = "null";
			
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			String uristr = "http://www.puskin.in/sticky/ajax/addSticky.php";

			HttpPost httppost = new HttpPost(uristr);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("color", "yellow"));

			nameValuePairs.add(new BasicNameValuePair("userId", Integer
					.toString(loggedInUserId)));

			nameValuePairs.add(new BasicNameValuePair("priority",stickyPriority));
			nameValuePairs.add(new BasicNameValuePair("text", stickyText));
			nameValuePairs.add(new BasicNameValuePair("title", stickyTitle));
			nameValuePairs
					.add(new BasicNameValuePair("filterType", stickyType));
			nameValuePairs
					.add(new BasicNameValuePair("dueDate", stickyDueDate));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			Log.i(SHARE_STICKY, "Execute HTTP Post Request");

			HttpResponse response = httpclient.execute(httppost);

			int responseStatus = parseResponse(response.getEntity()
					.getContent());

			Log.i(SHARE_STICKY, "" + responseStatus);
			if (responseStatus >= 1) {
				Log.w(SHARE_STICKY, "Sticky Updated successful");
				status = true;
			} else {
				Log.w(SHARE_STICKY, "FALSE");
				// saving Failed
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return status;
	}

	private int parseResponse(InputStream is) {
		int status = 0;
		String line = "";
		StringBuilder jsonResp = new StringBuilder();
		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		// Read response until the end
		try {
			while ((line = rd.readLine()) != null) {
				jsonResp.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject stickyrespjson;
		try {
			stickyrespjson = new JSONObject(jsonResp.toString());
			JSONObject respObj = stickyrespjson.getJSONObject("result");
			String message = (String) respObj.get("message");
			status = (Integer) respObj.get("status");

			Log.i(SHARE_STICKY, message);
			Log.i(SHARE_STICKY, "ststus=" + status);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Return full string
		return status;
	}
	
}
