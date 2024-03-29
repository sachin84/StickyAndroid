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

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
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

import com.puskin.sticky.dao.Reminder;
import com.puskin.sticky.dao.Sticky;
import com.puskin.sticky.model.ReminderModel;
import com.puskin.sticky.model.ReminderPeriodModel;
import com.puskin.sticky.model.StickyModel;

public class EditSticky extends Activity {
	final int EDIT_SUCCESS = 20;
	static final int DATE_DIALOG_ID = 999;

	public static final String EDIT_STICKY = "Edit Sticky";
	private int year = 0;
	private int month = 0;
	private int day = 0;
	public int StickyId;
	public String StickyType;
	private int selectedReminderPeriod = 0;

	// public String Name;
	// public String Text;
	public String DueDate;
	public String Priority;
	public String Progress;
	public Bundle stickyDataBndl;
	
	private String ReminderPeriodName = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.editsticky);

		stickyDataBndl = getIntent().getExtras();

		EditText stickyTextObj = (EditText) findViewById(R.id.stickyText);
		stickyTextObj.setText(stickyDataBndl.getString("Text"));

		EditText stickyTitleObj = (EditText) findViewById(R.id.stickyTitle);
		stickyTitleObj.setText(stickyDataBndl.getString("Title"));

		StickyId = stickyDataBndl.getInt("Id");
		StickyType = stickyDataBndl.getString("Type");
		Priority = stickyDataBndl.getString("Priority").toLowerCase();
		DueDate = stickyDataBndl.getString("DueDate");
		Progress = stickyDataBndl.getString("Progress");

		Log.i(EDIT_STICKY, "StickyId==>" + StickyId);

		// if (!isNullOrBlank(DueDate)) {
		// String duedateArr[] = DueDate.split("-");
		// year = Integer.parseInt(duedateArr[0]);
		// month = Integer.parseInt(duedateArr[1])-1;
		// day = Integer.parseInt(duedateArr[2]);
		//
		// }
		if (!isNullOrBlank(DueDate)) {
			try {
				Date duDate = new SimpleDateFormat("yyyy-MM-dd HH:mm")
						.parse(DueDate);
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(duDate);
				year = cal1.get(Calendar.YEAR);
				month = cal1.get(Calendar.MONTH);// added 1 because strt
				day = cal1.get(Calendar.DAY_OF_MONTH);

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		// show in main screen
		updateDate(year, month + 1, day);

		// making string CamelCase StickyPriority_Array
		Priority = Priority.substring(0, 1).toUpperCase()
				+ Priority.substring(1).toLowerCase();
		Log.i(EDIT_STICKY, "Priority==>" + Priority);
		Log.i(EDIT_STICKY, "Type==>" + StickyType);

		Resources res = getResources();
		String[] stickyTypeArr = res.getStringArray(R.array.StickyType_Array);

		// setting up forms elements
		Spinner spinnerType = (Spinner) findViewById(R.id.stickyType);
		ArrayAdapter<CharSequence> adapterType = ArrayAdapter
				.createFromResource(this, R.array.StickyType_Array,
						android.R.layout.simple_spinner_item);

		adapterType
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinnerType.setAdapter(adapterType);
		int typePos = adapterType.getPosition(StickyType);
		spinnerType.setSelection(typePos);
		Log.i(EDIT_STICKY, "StickyTypePOS==>" + typePos);

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
		int priorityPos = adapterPriority.getPosition(Priority);
		spinnerPriority.setSelection(priorityPos);

		Log.i(EDIT_STICKY, "StickyPriorityPOS==>" + priorityPos);

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

		setDueDateButtonListener();
		setSaveButtonListener();
		setReminderPeriodClickListener();
		setstickyProgressClickListener();
	}

	private boolean loadReminderDetails() {

		ReminderModel reminderModel = new ReminderModel(this);

		Cursor reminderCur = reminderModel.getReminderDatafrmStickyId(StickyId);

		if (reminderCur.moveToFirst()) {
			do {
				selectedReminderPeriod = reminderCur.getInt(reminderCur
						.getColumnIndex("_id"));
				ReminderPeriodName = reminderCur.getString(reminderCur
						.getColumnIndex("_period_name"));
			} while (reminderCur.moveToNext());
			reminderCur.close();
		}
		reminderModel.close();
		return true;
	}
	
	private void setstickyProgressClickListener() {
		Spinner spinnerProgress = (Spinner) findViewById(R.id.stickyProgress);
		ArrayAdapter<CharSequence> adapterPriority = ArrayAdapter
				.createFromResource(this, R.array.StickyProgressArray,
						android.R.layout.simple_spinner_item);

		adapterPriority
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerProgress.setAdapter(adapterPriority);
		int progressPos = adapterPriority.getPosition(Progress);
		spinnerProgress.setSelection(progressPos);
		// Progress
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

	private void setDueDateButtonListener() {
		Button dueDatebtn = (Button) findViewById(R.id.dueDateButton);
		dueDatebtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}

		});
	}

	private void setSaveButtonListener() {
		Button saveButton = (Button) findViewById(R.id.editButton);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// boolean status = updateSticky();
				boolean status = EditStickyInLocalDB();
				if (status) {
					setResult(EDIT_SUCCESS);
					finish();
				}
			}
		});
	}

	private boolean isNullOrBlank(String s) {
		return (s == null || s.trim().equals("") || s.trim().equals("null"));
	}

	private void updateDate(int year, int month, int day) {
		Log.i(EDIT_STICKY, "Current Month==>" + month);

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
			Log.i(EDIT_STICKY, "Selected Month==>" + monthOfYear);

		}
	};

	protected Dialog onCreateDialog(int id) {
		final Calendar cal = Calendar.getInstance();
		if (year == 0) {
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH) + 1;
			day = cal.get(Calendar.DAY_OF_MONTH);
		}
		return new DatePickerDialog(this, dateListener, year, month, day);
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		this.finish();
		// super.onBackPressed();
	}

	public boolean EditStickyInLocalDB() {
		boolean status = false;
		SharedPreferences settings = getSharedPreferences("StickySettings", 0);
		int loggedInUserId = settings.getInt("loggedInUserId", 0);

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
		}

		Sticky stickyObj = new Sticky();

		stickyObj.setId(StickyId);
		stickyObj.setUserId(loggedInUserId);
		stickyObj.setTitle(stickyTitle);
		stickyObj.setText(stickyText);
		stickyObj.setPriority(stickyPriority);
		stickyObj.setProgress(stickyProgress);
		stickyObj.setDueDate(duedt);
		stickyObj.setCreatedAt(currdt);
		stickyObj.setStickyType(stickyType);

		StickyModel stickyModel = new StickyModel(this);
		status = stickyModel.EditSticky(stickyObj);
		stickyModel.close();

		updateReminderInLocal(currdt, duedt);
		return status;
	}

	private boolean updateReminderInLocal(Date currdt, Date duedt) {
		Log.i(EDIT_STICKY, "Reminder Update Called");
		String reminderId = "";
		Reminder reminder = new Reminder();
		reminder.setAddedDate(currdt);
		reminder.setStickyId(StickyId);
		reminder.setPeriodId(selectedReminderPeriod);
		reminder.setEnabled(true);
		reminder.setDueDate(duedt);

		ReminderModel reminderModel = new ReminderModel(this);


		Log.i(EDIT_STICKY, "selectedReminderPeriod=="+selectedReminderPeriod);
		Log.i(EDIT_STICKY, "StickyId=="+StickyId);

		if (selectedReminderPeriod == 1) {
			reminderModel.deleteStickyReminder(StickyId);
		} else {
			Cursor reminderCur = reminderModel.getReminderDatafrmStickyId(reminder
					.getStickyId());
			
			if (reminderCur.moveToFirst()) {
				do {
					reminderId = reminderCur.getString(reminderCur
							.getColumnIndex("_id"));
				} while (reminderCur.moveToNext());
				reminderCur.close();
				
				reminder.setId(Integer.parseInt(reminderId));
				reminderModel.EditReminder(reminder);
			} else {
				reminderModel.AddReminder(reminder);
			}

		}
		reminderModel.close();
		return true;
	}

	public boolean updateSticky() {

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

			// SimpleDateFormat curFormater = new
			// SimpleDateFormat("dd-MM-yyyy");
			// java.util.Date dateObj = null;
			// dateObj = curFormater.parse(stickyDueDate);
			// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// stickyDueDate = sdf.format(dateObj);

			Log.i(EDIT_STICKY, "stickyDueDate=" + stickyDueDate);
			Log.i(EDIT_STICKY, stickyTitle);
			Log.i(EDIT_STICKY, stickyPriority);
			Log.i(EDIT_STICKY, stickyText);
			Log.i(EDIT_STICKY, stickyType);
			Log.i(EDIT_STICKY, "loggedInUserId==" + loggedInUserId);
			Log.i(EDIT_STICKY, "StickyId==" + StickyId);

			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			String uristr = "http://www.puskin.in/sticky/ajax/updateSticky.php";

			HttpPost httppost = new HttpPost(uristr);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("color", "yellow"));
			nameValuePairs.add(new BasicNameValuePair("stickyId", Integer
					.toString(StickyId)));
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
			Log.i(EDIT_STICKY, "Execute HTTP Post Request");

			HttpResponse response = httpclient.execute(httppost);

			int responseStatus = parseResponse(response.getEntity()
					.getContent());

			Log.i(EDIT_STICKY, "" + responseStatus);
			if (responseStatus >= 1) {
				Log.w(EDIT_STICKY, "Sticky Updated successful");
				status = true;
			} else {
				Log.w(EDIT_STICKY, "FALSE");
				// saving Failed
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// catch (ParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

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

			Log.i(EDIT_STICKY, message);
			Log.i(EDIT_STICKY, "ststus=" + status);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Return full string
		return status;
	}
}
