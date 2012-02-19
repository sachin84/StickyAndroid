package com.puskin.sticky.home;

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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewSticky extends Activity {
	public static final String VIEW_STICKY = "View Sticky";
	static final int DATE_DIALOG_ID = 999;
	private int year = 0;
	private int month = 0;
	private int day = 0;
	public int StickyId;
	public String StickyType;

	public String DueDate;
	public String Priority;
	public Bundle stickyDataBndl;
	protected ProgressDialog m_ProgressDialog = null;
	AlertDialog.Builder confirmDialog = null;
	AlertDialog alertDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewsticky);

		stickyDataBndl = getIntent().getExtras();

		TextView stickyTextObj = (TextView) findViewById(R.id.stickyText);
		stickyTextObj.setText(stickyDataBndl.getString("Text"));

		TextView stickyTitleObj = (TextView) findViewById(R.id.stickyTitle);
		stickyTitleObj.setText(stickyDataBndl.getString("Title"));

		StickyId = stickyDataBndl.getInt("Id");
		StickyType = stickyDataBndl.getString("Type");
		Priority = stickyDataBndl.getString("Priority").toLowerCase();
		DueDate = stickyDataBndl.getString("DueDate");

		if (!isNullOrBlank(DueDate)) {
			if (DueDate != null) {
				String duedateArr[] = DueDate.split("-");
				year = Integer.parseInt(duedateArr[0]);
				month = Integer.parseInt(duedateArr[1]);
				day = Integer.parseInt(duedateArr[2]);
			}
		}

		// making string CamelCase StickyPriority_Array
		Priority = Priority.substring(0, 1).toUpperCase()
				+ Priority.substring(1).toLowerCase();

		Log.i(VIEW_STICKY, "Priority==>" + Priority);
		Log.i(VIEW_STICKY, "Type==>" + StickyType);

		TextView stickyTypeObj = (TextView) findViewById(R.id.stickyType);
		stickyTypeObj.setText(stickyDataBndl.getString("Type"));

		TextView stickyPriorityObj = (TextView) findViewById(R.id.stickyPriority);
		stickyPriorityObj.setText(Priority);

		// m_ProgressDialog = new ProgressDialog(getApplicationContext());

		createConfirmDialog();

		setShareClickListener();
		setEditClickListener();
		setDeleteClickListener();

		updateDate();

	}

	private void setEditClickListener() {
		ImageView editView = (ImageView) findViewById(R.id.PublicEdit);
		editView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(VIEW_STICKY, "OnClick is called");

				Intent editStickyIntent = new Intent(ViewSticky.this,
						EditSticky.class);
				editStickyIntent.putExtras(stickyDataBndl);
				startActivityForResult(editStickyIntent, 1);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

			}
		});
	}

	private void setShareClickListener() {
		ImageView shareView = (ImageView) findViewById(R.id.PublicShare);
		shareView.setImageResource(R.drawable.share);

		shareView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(VIEW_STICKY, "OnClick is called");
				ImageView sharebtn = (ImageView) findViewById(R.id.PublicShare);
				sharebtn.setImageResource(R.drawable.share_on);

				Intent shareIntent = new Intent(ViewSticky.this,
						ShareSticky.class);
				// Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						stickyDataBndl.getString("Title"));
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						stickyDataBndl.getString("Text"));
				startActivityForResult(
						Intent.createChooser(shareIntent, "Share via"), 2);

			}

		});
	}

	private void setDeleteClickListener() {
		ImageView deleteView = (ImageView) findViewById(R.id.PublicDelete);
		deleteView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(VIEW_STICKY, "OnClick is called");
				ImageView deletebtn = (ImageView) findViewById(R.id.PublicDelete);
				deletebtn.setImageResource(R.drawable.delete_on);

				alertDialog.show();
			}

		});
	}

	private void createConfirmDialog() {

		confirmDialog = new AlertDialog.Builder(ViewSticky.this);
		confirmDialog.setMessage("Are you sure you want to delete?");
		confirmDialog.setCancelable(false);
		confirmDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						 dialog.dismiss();
							new DeleteSticky().execute("");

					}
				});
		confirmDialog.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						ImageView deletebtn = (ImageView) findViewById(R.id.PublicDelete);
						deletebtn.setImageResource(R.drawable.delete);
					}
				});
		alertDialog = confirmDialog.create();
	}

	public void finishActivity() {

		setResult(2);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Log.i(VIEW_STICKY, "Edit Completed");
			this.finish();
		} else {
			Log.i(VIEW_STICKY, "Edit Completed");
		}
		ImageView sharebtn = (ImageView) findViewById(R.id.PublicShare);
		sharebtn.setImageResource(R.drawable.share);
	}

	@Override
	public void onBackPressed() {
		this.finish();
		// super.onBackPressed();
	}

	// @Override
	// public void onStop() {
	// super.onStop();
	// m_ProgressDialog = null;
	// }

	private boolean isNullOrBlank(String s) {
		return (s == null || s.trim().equals("") || s.trim().equals("null"));
	}

	private void updateDate() {
		TextView txt = (TextView) findViewById(R.id.dueDateText);
		txt.setText(new StringBuilder().append(day).append('-').append(month)
				.append('-').append(year));
	}

	private class DeleteSticky extends AsyncTask<String, Void, String> {

		private ProgressDialog prgDialog;

		public DeleteSticky() {
			m_ProgressDialog = new ProgressDialog(getApplicationContext());
		}

		@Override
		protected String doInBackground(String... params) {
			// perform long running operation operation

			deleteSticky();

			return "success";
		}

		@Override
		protected void onPreExecute() {
			// Things to be done before execution of long running operation. For
			m_ProgressDialog = ProgressDialog.show(ViewSticky.this,
					"Please wait...", "Connecting To Server...", true);
		}

		@Override
		protected void onPostExecute(String result) {
			// execution of result of Long time consuming operation
			m_ProgressDialog.setMessage("Record Deleted Successfully...");
			m_ProgressDialog.dismiss();
			finishActivity();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// Things to be done while execution of long running operation is in
			// progress. For example updating ProgessDialog
		}
		
	}

	public boolean deleteSticky() {

		boolean status = false;
		SharedPreferences settings = getSharedPreferences("StickySettings", 0);
		int loggedInUserId = settings.getInt("loggedInUserId", 0);

		try {

			Log.i(VIEW_STICKY, "loggedInUserId==" + loggedInUserId);
			Log.i(VIEW_STICKY, "StickyId==" + StickyId);

			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			String uristr = "http://www.puskin.in/sticky/ajax/delsticky.php";

			HttpPost httppost = new HttpPost(uristr);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("delStickyId", Integer
					.toString(StickyId)));
			nameValuePairs.add(new BasicNameValuePair("userId", Integer
					.toString(loggedInUserId)));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			Log.i(VIEW_STICKY, "Execute HTTP Post Request");

			HttpResponse response = httpclient.execute(httppost);

			int responseStatus = parseResponse(response.getEntity()
					.getContent());

			Log.i(VIEW_STICKY, "" + responseStatus);
			if (responseStatus >= 1) {
				Log.w(VIEW_STICKY, "Sticky Updated successful");
				status = true;
			} else {
				Log.w(VIEW_STICKY, "FALSE");
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

			Log.i(VIEW_STICKY, message);
			Log.i(VIEW_STICKY, "ststus=" + status);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Return full string
		return status;
	}
}
