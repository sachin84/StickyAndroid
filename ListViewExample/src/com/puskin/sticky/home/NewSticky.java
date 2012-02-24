package com.puskin.sticky.home;

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

public class NewSticky extends Activity {
	public static final String NEW_STICKY = "New Sticky";
	static final int DATE_DIALOG_ID = 999;
	private int year = 0;
	private int month = 0;
	private int day = 0;
	public int NewStickyId;
	public String StickyType;

	// public String Name;
	// public String Text;
	public String DueDate;
	public String Priority;
	public Bundle stickyDataBndl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.newsticky);

		Spinner spinnerType = (Spinner) findViewById(R.id.stickyType);
		ArrayAdapter<CharSequence> adapterType = ArrayAdapter
				.createFromResource(this, R.array.StickyType_Array,
						android.R.layout.simple_spinner_item);

		adapterType
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerType.setAdapter(adapterType);

		spinnerType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// parent.getItemAtPosition(pos).toString()
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		Spinner spinnerPriority = (Spinner) findViewById(R.id.stickyPriority);
		ArrayAdapter<CharSequence> adapterPriority = ArrayAdapter
				.createFromResource(this, R.array.StickyPriority_Array,
						android.R.layout.simple_spinner_item);

		adapterPriority
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerPriority.setAdapter(adapterPriority);

		spinnerPriority.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				parent.getItemAtPosition(pos).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		Button dueDatebtn = (Button) findViewById(R.id.dueDateButton);
		dueDatebtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}

		});

		final Calendar cal = Calendar.getInstance();
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		day = cal.get(Calendar.DAY_OF_MONTH);

		updateDate();

		Button saveButton = (Button) findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				createSticky();
				setResult(RESULT_OK);
				finish();
			}

		});

		//setReminderPeriodClickListener();
		setstickyProgressClickListener();
	}

	private void setstickyProgressClickListener() {
		Spinner spinnerProgress = (Spinner) findViewById(R.id.stickyProgress);
		ArrayAdapter<CharSequence> adapterPriority = ArrayAdapter
				.createFromResource(this, R.array.StickyProgressArray,
						android.R.layout.simple_spinner_item);

		adapterPriority
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerProgress.setAdapter(adapterPriority);

		spinnerProgress.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				parent.getItemAtPosition(pos).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

	}

	private void setReminderPeriodClickListener() {
//		ReminderPeriodModel period = new ReminderPeriodModel(this);
//		Cursor periodCur = period.getAllReminderPeriods();
//		
//		Spinner spinnerPriority = (Spinner) findViewById(R.id.reminderPeriod);
//		//startManagingCursor(periodCur);
//
//		// create an array to specify which fields we want to display
//		String[] from = new String[] { "_period_name" };
//		// create an array of the display item we want to bind our data to
//		int[] to = new int[] { android.R.id.text1 };
//
//		SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this,
//				android.R.layout.simple_spinner_item, periodCur, from, to);
//		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//		spinnerPriority.setAdapter(mAdapter);
//		period.close();

	}


	private boolean isNullOrBlank(String s) {
		return (s == null || s.trim().equals("") || s.trim().equals("null"));
	}

	private void updateDate() {
		TextView txt = (TextView) findViewById(R.id.dueDateText);
		txt.setText(new StringBuilder().append(day).append('-').append(month)
				.append('-').append(year));

	}

	private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int yr, int monthOfYear,
				int dayOfMonth) {
			year = yr;
			month = monthOfYear;
			day = dayOfMonth;
			updateDate();
		}
	};

	protected Dialog onCreateDialog(int id) {
		return new DatePickerDialog(this, dateListener, year, month, day);
	}

	@Override
	public void onBackPressed() {
		this.finish();
		// super.onBackPressed();
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

			Log.i(NEW_STICKY, "stickyDueDate=" + stickyDueDate);
			Log.i(NEW_STICKY, stickyTitle);
			Log.i(NEW_STICKY, stickyPriority);
			Log.i(NEW_STICKY, stickyText);
			Log.i(NEW_STICKY, stickyType);
			Log.i(NEW_STICKY, "loggedInUserId==" + loggedInUserId);

			if (stickyDueDate == "0-0-0")
				stickyDueDate = "null";

			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			String uristr = "http://www.puskin.in/sticky/ajax/addSticky.php";

			HttpPost httppost = new HttpPost(uristr);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("color", "yellow"));

			nameValuePairs.add(new BasicNameValuePair("userId", Integer
					.toString(loggedInUserId)));

			nameValuePairs.add(new BasicNameValuePair("priority",
					stickyPriority));
			nameValuePairs.add(new BasicNameValuePair("text", stickyText));
			nameValuePairs.add(new BasicNameValuePair("title", stickyTitle));
			nameValuePairs
					.add(new BasicNameValuePair("filterType", stickyType));
			nameValuePairs
					.add(new BasicNameValuePair("dueDate", stickyDueDate));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			Log.i(NEW_STICKY, "Execute HTTP Post Request");

			HttpResponse response = httpclient.execute(httppost);

			int responseStatus = parseResponse(response.getEntity()
					.getContent());

			Log.i(NEW_STICKY, "" + responseStatus);
			if (responseStatus >= 1) {
				Log.w(NEW_STICKY, "Sticky Updated successful");
				status = true;
			} else {
				Log.w(NEW_STICKY, "FALSE");
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

			Log.i(NEW_STICKY, message);
			Log.i(NEW_STICKY, "ststus=" + status);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Return full string
		return status;
	}

}
