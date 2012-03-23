package com.puskin.sticky.home;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import com.puskin.sticky.dao.Reminder;
import com.puskin.sticky.dao.Sticky;
import com.puskin.sticky.model.ReminderModel;
import com.puskin.sticky.model.ReminderPeriodModel;
import com.puskin.sticky.model.StickyModel;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewSticky extends Activity {
	public static final String NEW_STICKY = "New Sticky";
	static final int DATE_DIALOG_ID = 999;
	private int year = 0;
	private int month = 0;
	private int day = 0;
	public int NewStickyId;
	public String StickyType;
	private int selectedReminderPeriod = 0;

	// public String Name;
	// public String Text;
	public String DueDate;
	public String Priority;
	public String Progress;

	public Bundle stickyDataBndl;
	public int lastAddedStickyId = 0;

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

		updateDate(year, month + 1, day);

		Button saveButton = (Button) findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// createSticky();
				boolean status = createStickyInLocal();
				if (status) {

					Intent intent = new Intent();
					intent.putExtra("lastAddedStickyId", lastAddedStickyId);
					setResult(10, intent);
					finish();
				} else {
					setResult(RESULT_OK);
					finish();
				}
			}

		});

		setReminderPeriodClickListener();
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
		ReminderPeriodModel period = new ReminderPeriodModel(this);
		Cursor periodCur = period.getAllReminderPeriods();

		Spinner reminderPeriod = (Spinner) findViewById(R.id.reminderPeriod);
		// startManagingCursor(periodCur);

		// create an array to specify which fields we want to display
		String[] from = new String[] { "_period_name" };
		// create an array of the display item we want to bind our data to
		int[] to = new int[] { android.R.id.text1 };

		SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, periodCur, from, to);
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		reminderPeriod.setAdapter(mAdapter);
		period.close();

		reminderPeriod.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				Cursor mCursor = (Cursor) parent.getItemAtPosition(pos);
				selectedReminderPeriod = Integer.parseInt(mCursor.getString(0));

				// Toast.makeText(view.getContext(), mCursor.getString(0),
				// Toast.LENGTH_LONG).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

	}

	private boolean isNullOrBlank(String s) {
		return (s == null || s.trim().equals("") || s.trim().equals("null"));
	}

	private void updateDate(int year, int month, int day) {

		TextView txt = (TextView) findViewById(R.id.dueDateText);
		txt.setText(new StringBuilder().append(day).append('-').append(month)
				.append('-').append(year));
	}

	private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int yr, int monthOfYear,
				int dayOfMonth) {
			year = yr;
			month = monthOfYear + 1;
			day = dayOfMonth;
			updateDate(year, month, day);
		}
	};

	protected Dialog onCreateDialog(int id) {
		return new DatePickerDialog(this, dateListener, year, month, day);
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);

		this.finish();
		// super.onBackPressed();
	}

	public boolean createStickyInLocal() {
		boolean status = true;

		SharedPreferences settings = getSharedPreferences("StickySettings", 0);
		int loggedInUserId = settings.getInt("loggedInUserId", 0);

		// Add user name and password
		EditText stickyTextObj = (EditText) findViewById(R.id.stickyText);
		String stickyText = stickyTextObj.getText().toString();

		EditText stickyTitleObj = (EditText) findViewById(R.id.stickyTitle);
		String stickyTitle = stickyTitleObj.getText().toString();

		Spinner spnrStickyType = (Spinner) findViewById(R.id.stickyType);
		String stickyType = spnrStickyType.getSelectedItem().toString();

		Spinner spnrStickyPriority = (Spinner) findViewById(R.id.stickyPriority);
		String stickyPriority = spnrStickyPriority.getSelectedItem().toString();

		Spinner spnrstickyProgress = (Spinner) findViewById(R.id.stickyProgress);
		String stickyProgress = spnrstickyProgress.getSelectedItem().toString();

		TextView stickydueDateObj = (TextView) findViewById(R.id.dueDateText);
		String stickyDueDate = stickydueDateObj.getText().toString();

		// SimpleDateFormat dtFormater = new SimpleDateFormat(
		// "yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dtFormater = new SimpleDateFormat("dd-MM-yyyy");
		Date currdt = new java.util.Date();

		Date duedt = null;

		try {
			duedt = dtFormater.parse(stickyDueDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			status = false;
		}

		Sticky stickyObj = new Sticky();

		stickyObj.setUserId(loggedInUserId);
		stickyObj.setTitle(stickyTitle);
		stickyObj.setText(stickyText);
		stickyObj.setPriority(stickyPriority);
		stickyObj.setProgress(stickyProgress);
		stickyObj.setDueDate(duedt);
		stickyObj.setCreatedAt(currdt);
		stickyObj.setStickyType(stickyType);

		StickyModel stickyModel = new StickyModel(this);
		lastAddedStickyId = stickyModel.AddSticky(stickyObj);

		Log.i(NEW_STICKY, "lastAddedStickyId=="+lastAddedStickyId);

		if (selectedReminderPeriod>0) {
			createReminderInLocal(currdt, duedt);
		}
		return status;
	}

	private int createReminderInLocal(Date currdt, Date duedt) {

		Reminder reminder = new Reminder();
		reminder.setAddedDate(currdt);
		reminder.setStickyId(lastAddedStickyId);
		reminder.setPeriodId(selectedReminderPeriod);
		reminder.setEnabled(true);
		reminder.setDueDate(duedt);

		ReminderModel reminderModel = new ReminderModel(this);

		return reminderModel.AddReminder(reminder);
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
