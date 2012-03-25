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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.puskin.sticky.model.StickyModel;

public class PublicActivity extends ListActivity {
	final int LOGIN_SUCCESS = 5;
	final int ADD_SUCCESS = 10;
	final int DELETE_SUCCESS = 15;
	final int EDIT_SUCCESS = 20;
	final int SEARCH_DONE = 25;

	public static final String PUBLIC_LISTING = "PublicActivity";
	private List<StickyData> stickyDataList = new ArrayList<StickyData>();
	private List<StickyData> storedDataList = new ArrayList<StickyData>();

	private StickyListAdapter stickyAdapter;
	protected ProgressDialog m_ProgressDialog = null;
	private PopupWindow popUp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Collections.synchronizedList(stickyDataList);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.public_layout);

		Date currentDate = new Date(System.currentTimeMillis());
		Log.i(PUBLIC_LISTING, currentDate.toString());

		stickyAdapter = new StickyListAdapter(this);
		setListAdapter(stickyAdapter);

		final ListView list = getListView();
		// Setting Custom Selector
		list.setSelector(getResources().getDrawable(R.drawable.list_selector));
		list.setFocusableInTouchMode(true);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		list.setFocusable(true);

		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// arg1.setBackgroundColor(Color.YELLOW);

				Bundle bunData = prepareEditStickyData(stickyDataList
						.get(position));

				Intent editStickyIntent = new Intent(PublicActivity.this,
						ViewSticky.class);
				editStickyIntent.putExtras(bunData);
				startActivityForResult(editStickyIntent, 1);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

			}

		});

		list.setClickable(true);

		LoadedStickyData loadedDataList = (LoadedStickyData) getLastNonConfigurationInstance();
		if (loadedDataList == null) {
			Log.i(PUBLIC_LISTING, "LOADING DATA....");
			new LoadPublicSticky().execute("");
		} else {
			// do something
			stickyDataList = loadedDataList.getStickyDataList();
			// stickyAdapter.notifyDataSetChanged();
		}

		setSearchClickListener();
		setRefreshClickListener();
		setAddClickListener();

	}

	// @Override
	// public void onStart() {
	// try {
	// super.onStart();
	// Log.i(PUBLIC_LISTING, "OnStart Called...");
	//
	// UserModel uerModel = new UserModel(this);
	// uerModel.open();
	// Log.i(PUBLIC_LISTING, "User Count==" + uerModel.getUserCount());
	// uerModel.close();
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// }

	@Override
	public Object onRetainNonConfigurationInstance() {
		LoadedStickyData loadedDataList = new LoadedStickyData();
		loadedDataList.setStickyDataList(stickyDataList);
		return loadedDataList;
	}

	private void setSearchClickListener() {
		ImageView searchView = (ImageView) findViewById(R.id.PublicSearch);
		searchView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(PUBLIC_LISTING, "Add Task is called");
//				Intent searchStickyIntent = new Intent(PublicActivity.this,
//						SearchActivity.class);
//				startActivityForResult(searchStickyIntent, SEARCH_DONE);
				displayPopup();
			}

		});
	}

	private void setRefreshClickListener() {
		ImageView refreshView = (ImageView) findViewById(R.id.PublicRefresh);
		refreshView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(PUBLIC_LISTING, "OnClick is called");
				ImageView addViewl = (ImageView) findViewById(R.id.PublicRefresh);
				addViewl.setImageResource(R.drawable.refresh_on);

				new LoadPublicSticky().execute("");
				ImageView notfound = (ImageView) findViewById(R.id.PublicNotFound);
				if (stickyDataList.size() <= 0) {
					notfound.setVisibility(View.VISIBLE);
				} else {
					notfound.setVisibility(View.GONE);
				}
			}

		});
	}

	private void setAddClickListener() {
		ImageView addView = (ImageView) findViewById(R.id.PublicAdd);
		addView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				Log.d(PUBLIC_LISTING, "Add Task is called");
				ImageView addViewl = (ImageView) findViewById(R.id.PublicAdd);
				addViewl.setImageResource(R.drawable.add_on);
				Intent newStickyIntent = new Intent(PublicActivity.this,
						NewSticky.class);
				newStickyIntent.putExtra("Type", "Public");
				startActivityForResult(newStickyIntent, ADD_SUCCESS);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}

		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		ImageView addView = (ImageView) findViewById(R.id.PublicAdd);
		addView.setImageResource(R.drawable.add);

		ImageView addViewl = (ImageView) findViewById(R.id.PublicRefresh);
		addViewl.setImageResource(R.drawable.refresh);

		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == LOGIN_SUCCESS) {
			Log.i(PUBLIC_LISTING, "Login Success...");
		} else if (resultCode == ADD_SUCCESS) {

			Log.i(PUBLIC_LISTING, "Add Completed");
			int lastAddedStickyId = data.getIntExtra("lastAddedStickyId", 0);
			loadStickyDetail(lastAddedStickyId);
			stickyAdapter.notifyDataSetChanged();
		} else if (resultCode == EDIT_SUCCESS) {
			Log.i(PUBLIC_LISTING, "Edit Completed...");
			new LoadPublicSticky().execute("");
		} else if (resultCode == DELETE_SUCCESS) {
			Log.i(PUBLIC_LISTING, "Delete Completed...");
			new LoadPublicSticky().execute("");
		} else if (resultCode == RESULT_CANCELED) {
			Log.i(PUBLIC_LISTING, "Add/Edit Cancelled...");
		} else {
			new LoadPublicSticky().execute("");
			Log.i(PUBLIC_LISTING, "Edit Completed");
		}
	}

	private boolean loadStickyDetail(int stickyId) {

		StickyModel stkyModel = new StickyModel(this);
		Cursor stickyCur = stkyModel.getStickyData(stickyId);

		if (stickyCur.moveToFirst()) {
			String data = stickyCur.getString(stickyCur
					.getColumnIndex("_title"));

			StickyData stkData = new StickyData();

			String dueDate = stickyCur.getString(stickyCur
					.getColumnIndex("_duedate"));

			int stkId = Integer.parseInt(stickyCur.getString(stickyCur
					.getColumnIndex("_id")));

			stkData.setId(stkId);
			stkData.setDueDate(dueDate);

			stkData.setText(stickyCur.getString(stickyCur
					.getColumnIndex("_text")));
			stkData.setName(stickyCur.getString(stickyCur
					.getColumnIndex("_title")));

			stkData.setPriority(stickyCur.getString(stickyCur
					.getColumnIndex("_priority")));

			stkData.setProgress(stickyCur.getString(stickyCur
					.getColumnIndex("_progress")));

			if (!isNullOrBlank(dueDate)) {
				Log.i(PUBLIC_LISTING, "dueDate==>" + dueDate);

				SimpleDateFormat curFormater = new SimpleDateFormat(
						"yyyy-MM-dd");
				java.util.Date dateObj = null;
				String days = "Not Set";

				try {
					dateObj = curFormater.parse(dueDate);
					int pendingdays = daysRemaining(dateObj);
					if (pendingdays < 0)
						days = Math.abs(pendingdays) + " Days Ago";
					else if (pendingdays == 0)
						days = "Today";
					else
						days = pendingdays + " Days Remaining";
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				stkData.setRemainingDays(days);
			}
			stickyDataList.add(stkData);
			storedDataList.add(stkData);
			// do what ever you want here
		}

		stickyCur.close();
		stkyModel.close();
		return true;
	}

	private Bundle prepareEditStickyData(StickyData stickyObj) {
		Bundle bl = new Bundle();
		bl.putInt("Id", stickyObj.getId());
		bl.putString("Title", stickyObj.getName());
		bl.putString("Priority", stickyObj.getPriority());
		bl.putString("Text", stickyObj.getText());
		bl.putString("DueDate", stickyObj.getDueDate());
		bl.putString("Type", "Public");
		bl.putString("Progress", stickyObj.getProgress());
		bl.putString("ReminderPeriod", stickyObj.getReminderPeriod());

		Log.i(PUBLIC_LISTING, "Data_ID" + stickyObj.getId());
		Log.i(PUBLIC_LISTING, "Text" + stickyObj.getText());

		return bl;

	}

	private class StickyListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public StickyListAdapter(Context context) {
			mInflater = LayoutInflater.from(context);

		}

		public int getCount() {
			return stickyDataList.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public void setSelected(int position) {
			int count = 0;
			for (StickyData data : stickyDataList) {
				if (count == position) {
					data.setSelected(true);
				} else {
					data.setSelected(false);
				}
				count++;
			}
		}

		public StickyData getSelectedItem() {
			for (StickyData data : stickyDataList) {
				if (data.isSelected()) {
					return data;
				}
			}
			return null;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.listview, parent,
						false);

				holder = new ViewHolder();
				holder.stickyId = (TextView) convertView
						.findViewById(R.id.StickyId);
				holder.stickyTitle = (TextView) convertView
						.findViewById(R.id.StickyTitle);
				holder.stickyDueDate = (TextView) convertView
						.findViewById(R.id.StickyDueDate);
				holder.stickyProgress = (TextView) convertView
						.findViewById(R.id.StickyProgress);

				holder.stickyPriority = (ImageView) convertView
						.findViewById(R.id.stickyPriority);

				holder.ReminderPeriodId = (TextView) convertView
						.findViewById(R.id.ReminderPeriodId);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			int selecterId = R.drawable.list_item_selector_normal;
			convertView.setBackgroundResource(selecterId);
			StickyData dataObj = stickyDataList.get(position);

			Log.i(PUBLIC_LISTING, "Data_ID" + dataObj.getId());
			Log.i(PUBLIC_LISTING, "Progress" + dataObj.getProgress());
			Log.i(PUBLIC_LISTING, "DueDate" + dataObj.getDueDate());
			Log.i(PUBLIC_LISTING, "Priority" + dataObj.getPriority());

			holder.stickyId.setText(String.valueOf(dataObj.getId()));
			holder.stickyTitle.setText(dataObj.getName());
			// holder.stickyText.setText(dataObj.getText());
			holder.stickyProgress.setText(dataObj.getProgress());
			holder.ReminderPeriodId.setText(dataObj.getReminderPeriod());

			holder.stickyDueDate.setText(dataObj.getRemainingDays());
			if (dataObj.getPriority().contains("high")
					|| dataObj.getPriority().contains("High")) {
				holder.stickyPriority.setImageResource(R.drawable.pri_high1);
			} else if (dataObj.getPriority().contains("medium")
					|| dataObj.getPriority().contains("Medium")) {
				holder.stickyPriority.setImageResource(R.drawable.pri_medium);
			} else if (dataObj.getPriority().contains("low")
					|| dataObj.getPriority().contains("Low")) {
				holder.stickyPriority.setImageResource(R.drawable.pri_low);
			} else if (dataObj.getPriority().contains("Urgent")
					|| dataObj.getPriority().contains("urgent")) {
				holder.stickyPriority.setImageResource(R.drawable.pri_urg);
			}
			// holder.stickyPriority.setText(dataObj.getPriority());
			// editView.setImageResource(R.drawable.edit_on);

			return convertView;
		}

		class ViewHolder {
			TextView stickyId;
			TextView stickyTitle;
			TextView stickyText;
			TextView stickyDueDate;
			TextView stickyProgress;
			ImageView stickyPriority;
			TextView ReminderPeriodId;

		}
	}// close StickyListAdapter Class

	private class LoadPublicSticky extends AsyncTask<String, Void, String> {

		private ProgressDialog prgDialog;

		public LoadPublicSticky() {
			m_ProgressDialog = new ProgressDialog(getApplicationContext());
		}

		@Override
		protected String doInBackground(String... params) {
			// perform long running operation operation
			// getStickyData();
			loadStickyFromDB();
			return null;
		}

		@Override
		protected void onPreExecute() {
			// Things to be done before execution of long running operation. For
			m_ProgressDialog = ProgressDialog.show(PublicActivity.this,
					"Please wait...", "Fetching Public Sticky...", true);
		}

		@Override
		protected void onPostExecute(String result) {
			// execution of result of Long time consuming operation
			// m_ProgressDialog.setMessage("Records Loaded Successfully...");

			m_ProgressDialog.dismiss();
			stickyAdapter.notifyDataSetChanged();
			if (stickyDataList.size() <= 0) {
				ImageView notfound = (ImageView) findViewById(R.id.PublicNotFound);
				notfound.setVisibility(View.VISIBLE);

			}
			ImageView addViewl = (ImageView) findViewById(R.id.PublicRefresh);
			addViewl.setImageResource(R.drawable.refresh);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// Things to be done while execution of long running operation is in
			// progress. For example updating ProgessDialog
		}

	}

	public boolean loadStickyFromDB() {
		SharedPreferences settings = getSharedPreferences("StickySettings", 0);
		int loggedInUserId = settings.getInt("loggedInUserId", 0);

		StickyModel stkyModel = new StickyModel(this);
		Cursor stickyCur = stkyModel.getAllStickys(loggedInUserId, "Public");

		stickyDataList = new ArrayList<StickyData>();

		if (stickyCur.moveToFirst()) {
			do {
				String data = stickyCur.getString(stickyCur
						.getColumnIndex("_title"));
				Log.i(PUBLIC_LISTING, "DB-data==>" + data);
				StickyData stkData = new StickyData();

				String dueDate = stickyCur.getString(stickyCur
						.getColumnIndex("_duedate"));

				int stkId = Integer.parseInt(stickyCur.getString(stickyCur
						.getColumnIndex("_id")));

				stkData.setId(stkId);
				stkData.setDueDate(dueDate);

				stkData.setText(stickyCur.getString(stickyCur
						.getColumnIndex("_text")));
				stkData.setName(stickyCur.getString(stickyCur
						.getColumnIndex("_title")));

				stkData.setPriority(stickyCur.getString(stickyCur
						.getColumnIndex("_priority")));

				String progress = stickyCur.getString(stickyCur.getColumnIndex("_progress"));
				progress += " (Repeat: "+stickyCur.getString(stickyCur.getColumnIndex("_period_name"))+")";
				stkData.setProgress(progress);


				if (!isNullOrBlank(dueDate)) {
					Log.i(PUBLIC_LISTING, "dueDate==>" + dueDate);

					SimpleDateFormat curFormater = new SimpleDateFormat(
							"yyyy-MM-dd");
					java.util.Date dateObj = null;
					String days = "Not Set";

					try {
						dateObj = curFormater.parse(dueDate);
						int pendingdays = daysRemaining(dateObj);
						if (pendingdays < 0)
							days = Math.abs(pendingdays) + " Days Ago";
						else if (pendingdays == 0)
							days = "Today";
						else
							days = pendingdays + " Days Remaining";
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					stkData.setRemainingDays(days);
				}
				stickyDataList.add(stkData);
				storedDataList.add(stkData);
				// do what ever you want here
			} while (stickyCur.moveToNext());
		}
		stickyCur.close();
		stkyModel.close();

		return false;
	}

	public boolean getStickyData11() {

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		// String uristr = "http://10.0.2.2/sticky/ajax/getsticky.php";
		String uristr = "http://puskin.in/sticky/ajax/getsticky.php?stickyFilterType=public";

		HttpPost httppost = new HttpPost(uristr);
		boolean status = false;

		try {

			// Add user name and password
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("stickyFilterType",
					"public"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			Log.w(PUBLIC_LISTING, "Execute HTTP Post Request");

			HttpResponse response = httpclient.execute(httppost);

			String str = parseResponseAndPrepareData(
					response.getEntity().getContent()).toString();
			Log.w(PUBLIC_LISTING, str);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return status;
	}

	private StringBuilder parseResponseAndPrepareData(InputStream is) {
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

		stickyDataList = new ArrayList<StickyData>();

		JSONObject stickyrespjson;
		try {
			stickyrespjson = new JSONObject(jsonResp.toString());
			JSONArray stickyJsonArray = stickyrespjson.getJSONArray("result");

			for (int i = 0; i < stickyJsonArray.length(); i++) {
				StickyData stkData = new StickyData();

				String dueDate = stickyJsonArray.getJSONObject(i).getString(
						"due_date");
				stkData.setId(Integer.parseInt(stickyJsonArray.getJSONObject(i)
						.getString("id")));
				stkData.setText(stickyJsonArray.getJSONObject(i).getString(
						"text"));
				stkData.setDueDate(dueDate);
				stkData.setName(stickyJsonArray.getJSONObject(i).getString(
						"name"));
				stkData.setPriority(stickyJsonArray.getJSONObject(i).getString(
						"priority"));

				if (!isNullOrBlank(dueDate)) {
					Log.i(PUBLIC_LISTING, "dueDate==>" + dueDate);

					SimpleDateFormat curFormater = new SimpleDateFormat(
							"yyyy-MM-dd");
					java.util.Date dateObj = null;
					String days = "Not Set";

					try {
						dateObj = curFormater.parse(dueDate);
						int pendingdays = daysRemaining(dateObj);
						if (pendingdays < 0)
							days = Math.abs(pendingdays) + " Days Ago";
						else if (pendingdays == 0)
							days = "Today";
						else
							days = pendingdays + " Days Remaining";
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					stkData.setRemainingDays(days);
				}
				stickyDataList.add(stkData);
				storedDataList.add(stkData);

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Return full string
		return jsonResp;
	}

	private boolean isNullOrBlank(String s) {
		return (s == null || s.trim().equals("") || s.trim().equals("null"));
	}

	public int daysRemaining(Date endDate) {
		Date currentDate = new Date(System.currentTimeMillis());
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(currentDate);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(endDate);
		Log.w(PUBLIC_LISTING, "currentDate==>" + currentDate + "  dueDate==>"
				+ endDate);

		long curTimeMilSec = cal1.getTimeInMillis();
		long stickyTimeMilSec = cal2.getTimeInMillis();
		long diff = stickyTimeMilSec - curTimeMilSec;

		// long diffSeconds = diff / 1000;
		// long diffMinutes = diff / (60 * 1000);
		// long diffHours = diff / (60 * 60 * 1000);
		int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
		return diffDays;
	}

	public void displayPopup()
	{
//        LayoutInflater inflater = (LayoutInflater) PublicActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        PopupWindow popupWindow = new PopupWindow(inflater.inflate(R.layout.searchsticky,null, false),300,100,true);
//RelativeLayout01 is Main Activity Root Layout
//        popupWindow.showAtLocation(findViewById(R.id.PublicRefresh), Gravity.CENTER, 0,0);
		

		popUp = new PopupWindow(this);
        LayoutInflater inflater = (LayoutInflater) PublicActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


		View layout = inflater.inflate(R.layout.searchsticky, null, true);
		
		popUp = new PopupWindow(layout,300,80,true);
		popUp.setTouchable(true);
		popUp.setFocusable(true);
		popUp.setBackgroundDrawable(new BitmapDrawable());
		popUp.setOutsideTouchable(false);
		// popUp.setAnimationStyle(R.style.Animations_GrowFromBottom);
		popUp.setTouchInterceptor(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					popUp.dismiss();
					return true;
				}
				return false;
			}

		});

		final EditText searchCriteria = (EditText) layout
				.findViewById(R.id.searchText);
		Button searchButton = (Button) layout.findViewById(R.id.searchButton);
		searchButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				String searchFor = searchCriteria.getText().toString();
				//performSearch(searchFor, Constants.CNX_SEARCH, context);
				popUp.dismiss();
				
			}
		});

		popUp.showAtLocation(layout, Gravity.TOP, 0, 0);

	}
}
