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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.puskin.sticky.model.StickyModel;

public class WorkActivity extends ListActivity {
	final int LOGIN_SUCCESS = 5;
	final int ADD_SUCCESS = 10;
	final int DELETE_SUCCESS = 15;
	final int EDIT_SUCCESS = 20;
	
	public static final String WORK_LISTING = "WorkActivity";
	private List<StickyData> stickyDataList = new ArrayList<StickyData>();
	private List<StickyData> storedDataList = new ArrayList<StickyData>();
	
	private StickyListAdapter stickyAdapter;
	private ProgressDialog m_ProgressDialog = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.work_layout);

		Date currentDate = new Date(System.currentTimeMillis());
		Log.i(WORK_LISTING, currentDate.toString());

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

				Intent editStickyIntent = new Intent(WorkActivity.this,
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
			Log.i(WORK_LISTING, "LOADING DATA....");
			new LoadWorkSticky().execute("");
		} else {
			// do something
			stickyDataList = loadedDataList.getStickyDataList();
			// stickyAdapter.notifyDataSetChanged();
		}

		setSearchClickListener();
		setRefreshClickListener();
		setAddClickListener();
		
	}

	private void setSearchClickListener() {
		ImageView searchView = (ImageView) findViewById(R.id.PublicSearch);
		searchView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(WORK_LISTING, "OnClick is called");
				Toast.makeText(v.getContext(), // <- Line changed
						"You Can Search Your Tasks Here", Toast.LENGTH_LONG)
						.show();

			}

		});
	}

	private void setRefreshClickListener() {
		ImageView refreshView = (ImageView) findViewById(R.id.PublicRefresh);
		refreshView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(WORK_LISTING, "OnClick is called");
				ImageView addViewl = (ImageView) findViewById(R.id.PublicRefresh);
				addViewl.setImageResource(R.drawable.refresh_on);

				new LoadWorkSticky().execute("");

				ImageView notfound = (ImageView) findViewById(R.id.PublicNotFound);
				if (stickyDataList.size() <= 0) {
					notfound.setVisibility(View.VISIBLE);
				}
				else{
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
				Log.d(WORK_LISTING, "Add Task is called");
				Intent newStickyIntent = new Intent(WorkActivity.this,
						NewSticky.class);

				startActivityForResult(newStickyIntent, 1);
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
			Log.i(WORK_LISTING, "Login Success...");
		} else if (resultCode == ADD_SUCCESS) {

			Log.i(WORK_LISTING, "Add Completed");
			int lastAddedStickyId = data.getIntExtra("lastAddedStickyId", 0);
			loadStickyDetail(lastAddedStickyId);
			stickyAdapter.notifyDataSetChanged();
		} else if (resultCode == EDIT_SUCCESS) {
			Log.i(WORK_LISTING, "Edit Completed...");
			new LoadWorkSticky().execute("");
		} else if (resultCode == DELETE_SUCCESS) {
			Log.i(WORK_LISTING, "Delete Completed...");
			new LoadWorkSticky().execute("");
		} else if (resultCode == RESULT_CANCELED) {
			Log.i(WORK_LISTING, "Add/Edit Cancelled...");
		} else {
			new LoadWorkSticky().execute("");
			Log.i(WORK_LISTING, "Edit Completed");
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
				Log.i(WORK_LISTING, "dueDate==>" + dueDate);

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
		bl.putString("Type", "Work");
		bl.putString("Progress", stickyObj.getProgress());

		Log.i(WORK_LISTING, "Data_ID" + stickyObj.getId());
		Log.i(WORK_LISTING, "Text" + stickyObj.getText());

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

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			int selecterId = R.drawable.list_item_selector_normal;
			convertView.setBackgroundResource(selecterId);
			StickyData dataObj = stickyDataList.get(position);

			Log.i(WORK_LISTING, "Data_ID" + dataObj.getId());
			Log.i(WORK_LISTING, "Progress" + dataObj.getProgress());
			Log.i(WORK_LISTING, "DueDate" + dataObj.getDueDate());
			Log.i(WORK_LISTING, "Priority" + dataObj.getPriority());

			holder.stickyId.setText(String.valueOf(dataObj.getId()));
			holder.stickyTitle.setText(dataObj.getName());
			// holder.stickyText.setText(dataObj.getText());
			holder.stickyProgress.setText(dataObj.getProgress());
			
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

		}
	}// close StickyListAdapter Class

	private class LoadWorkSticky extends AsyncTask<String, Void, String> {

		private ProgressDialog prgDialog;

		public LoadWorkSticky() {
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
			m_ProgressDialog = ProgressDialog.show(WorkActivity.this,
					"Please wait...", "Fetching Work Sticky...", true);
		}

		@Override
		protected void onPostExecute(String result) {
			// execution of result of Long time consuming operation
//			m_ProgressDialog.setMessage("Records Loaded Successfully...");

			m_ProgressDialog.dismiss();
			stickyAdapter.notifyDataSetChanged();
			ImageView notfound = (ImageView) findViewById(R.id.PublicNotFound);
			if (stickyDataList.size() <= 0) {
				notfound.setVisibility(View.VISIBLE);
			}
			else{
				notfound.setVisibility(View.GONE);
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
		Cursor stickyCur = stkyModel.getAllStickys(loggedInUserId, "Work");

		stickyDataList = new ArrayList<StickyData>();

		if (stickyCur.moveToFirst()) {
			do {
				String data = stickyCur.getString(stickyCur
						.getColumnIndex("_title"));
				Log.i(WORK_LISTING, "DB-data==>" + data);
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
					Log.i(WORK_LISTING, "dueDate==>" + dueDate);

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
					Log.i(WORK_LISTING, "dueDate==>" + dueDate);

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
		Log.w(WORK_LISTING, "currentDate==>" + currentDate + "  dueDate==>"
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
	
	
	
	
	
	

}
