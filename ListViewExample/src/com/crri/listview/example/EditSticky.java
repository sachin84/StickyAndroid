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

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditSticky extends Activity {
	public static final String EDIT_STICKY = "New Sticky";
	static final int DATE_DIALOG_ID = 999;
	private int year;
	private int month;
	private int day;
//	public int Id;
//	public String Name;
//	public String Text;
//	public String DueDate;
//	public String Priority;
	public Bundle stickyDataBndl;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editsticky);

		stickyDataBndl = getIntent().getExtras();
		
		EditText stickyTextObj = (EditText) findViewById(R.id.stickyText);
		stickyTextObj.setText(stickyDataBndl.getString("Text"));

		Log.i(EDIT_STICKY, "Data_ID" + stickyDataBndl.getString("Id"));
		Log.i(EDIT_STICKY, "Text" + stickyDataBndl.getString("Text"));

		
		
		
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
				 //parent.getItemAtPosition(pos).toString()
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
		
		Button saveButton = (Button) findViewById(R.id.editButton);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				postLoginData();
			}

		});
		
	}

	private void updateDate() {
		TextView txt = (TextView) findViewById(R.id.TextView6);
		txt.setText(new StringBuilder().append(day).append('-')
				.append(month + 1).append('-').append(year));

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

	public boolean postLoginData() {


		boolean status = false;

		try {
			// Add user name and password
			EditText stickyTextObj = (EditText) findViewById(R.id.stickyText);
			String stickyText = stickyTextObj.getText().toString();

			EditText stickyTitleObj = (EditText) findViewById(R.id.stickyTitle);
			String stickyTitle = stickyTitleObj.getText().toString();

			Spinner spnrStickyType = (Spinner) findViewById(R.id.stickyType);
			String stickyType = spnrStickyType.getSelectedItem().toString();

			Spinner spnrStickyPriority = (Spinner) findViewById(R.id.stickyType);
			String stickyPriority = spnrStickyPriority.getSelectedItem().toString();

			EditText stickydueDateObj = (EditText) findViewById(R.id.stickyText);
			String stickyDueDate = stickydueDateObj.getText().toString();
			
			Log.v(EDIT_STICKY, stickyText);
			Log.v(EDIT_STICKY, stickyTitle);
			Log.v(EDIT_STICKY, stickyType);

			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			String uristr = "http://www.puskin.in/sticky/ajax/post.php";
//			String urlParams = "?color=yello&zindex=123&body="+stickyText+"&author="+stickyTitle+"&filterType="+stickyType;
//			urlParams += "&priority="+stickyPriority+"&dueDate="+stickyDueDate;
//			uristr = uristr+urlParams;
			
			HttpPost httppost = new HttpPost(uristr);
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("color", "yello"));
			nameValuePairs.add(new BasicNameValuePair("zindex", "123"));
			nameValuePairs.add(new BasicNameValuePair("body", stickyText));
			nameValuePairs.add(new BasicNameValuePair("author", stickyTitle));
			nameValuePairs.add(new BasicNameValuePair("filterType", stickyType));
			nameValuePairs.add(new BasicNameValuePair("priority", stickyPriority));
			nameValuePairs.add(new BasicNameValuePair("dueDate", stickyDueDate));
			
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			Log.w(EDIT_STICKY, "Execute HTTP Post Request");

			HttpResponse response = httpclient.execute(httppost);

			String responseStr = inputStreamToString(response.getEntity().getContent())
					.toString();
			Log.w(EDIT_STICKY, responseStr);

			if (responseStr.toString().contains("Success")) {
				Log.w(EDIT_STICKY, "Login successful");
				System.out.println("Login successful");
				status = true;
			} else {
				Log.w(EDIT_STICKY, "FALSE");
				System.out.println(responseStr);
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return status;
	}

	private StringBuilder inputStreamToString(InputStream is) {
		String line = "";
		StringBuilder total = new StringBuilder();
		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		// Read response until the end
		try {
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Return full string
		return total;
	}
}
