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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

public class PrivateActivity extends ListActivity {

	public static final String LIST_EXAMPLE = "WorkActivity";
	private List<StickyData> stickyDataList = new ArrayList<StickyData>();
	private StickyListAdapter stickyAdapter;
	private ProgressDialog m_ProgressDialog = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.private_layout);

		stickyAdapter = new StickyListAdapter(this);
		setListAdapter(stickyAdapter);

		ListView list = getListView();
		list.setSelector(getResources().getDrawable(R.drawable.list_selector));

		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// arg1.setBackgroundColor(Color.YELLOW);

				Bundle bunData = prepareEditStickyData(stickyDataList
						.get(position));

				Intent editStickyIntent = new Intent(PrivateActivity.this,
						ViewSticky.class);
				editStickyIntent.putExtras(bunData);
				startActivityForResult(editStickyIntent, 1);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});
		list.setClickable(true);

		setSearchClickListener();
		setRefreshClickListener();
		setAddClickListener();
		// loading Async Sticky
		new LoadPrivateSticky().execute("");
	}

	private void setSearchClickListener() {
		ImageView searchView = (ImageView) findViewById(R.id.PublicSearch);
		searchView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(LIST_EXAMPLE, "OnClick is called");
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
				Log.d(LIST_EXAMPLE, "OnClick is called");
				// Toast.makeText(v.getContext(), // <- Line changed
				// "Sync Your Task With Server.", Toast.LENGTH_LONG)
				// .show();

				new LoadPrivateSticky().execute("");

			}

		});
	}

	private void setAddClickListener() {
		ImageView addView = (ImageView) findViewById(R.id.PublicAdd);
		addView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(LIST_EXAMPLE, "Add Task is called");
				Intent newStickyIntent = new Intent(PrivateActivity.this,
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

		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Log.i(LIST_EXAMPLE, "Edit Completed");
		} else {
			Log.i(LIST_EXAMPLE, "Edit Completed");
		}
	}

	private Bundle prepareEditStickyData(StickyData stickyObj) {
		Bundle bl = new Bundle();
		bl.putInt("Id", stickyObj.getId());
		bl.putString("Title", stickyObj.getName());
		bl.putString("Priority", stickyObj.getPriority());
		bl.putString("Text", stickyObj.getText());
		bl.putString("DueDate", stickyObj.getDueDate());
		bl.putString("Type", "Public");

		Log.i(LIST_EXAMPLE, "Data_ID" + stickyObj.getId());
		Log.i(LIST_EXAMPLE, "Text" + stickyObj.getText());

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

				holder.stickyPriority = (ImageView) convertView
						.findViewById(R.id.stickyPriority);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			int selecterId = R.drawable.list_item_selector_normal;
			convertView.setBackgroundResource(selecterId);
			StickyData dataObj = stickyDataList.get(position);

			Log.i(LIST_EXAMPLE, "Data_ID" + dataObj.getId());
			Log.i(LIST_EXAMPLE, "Data_Text" + dataObj.getText());
			Log.i(LIST_EXAMPLE, "DueDate" + dataObj.getDueDate());
			Log.i(LIST_EXAMPLE, "Priority" + dataObj.getPriority());

			holder.stickyId.setText(String.valueOf(dataObj.getId()));
			holder.stickyTitle.setText(dataObj.getName());
			// holder.stickyText.setText(dataObj.getText());
			holder.stickyDueDate.setText(dataObj.getDueDate());
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

			return convertView;
		}

		class ViewHolder {
			TextView stickyId;
			TextView stickyTitle;
			TextView stickyText;
			TextView stickyDueDate;
			ImageView stickyPriority;
		}
	}// close StickyListAdapter Class

	private class LoadPrivateSticky extends AsyncTask<String, Void, String> {

		public LoadPrivateSticky() {
			m_ProgressDialog = new ProgressDialog(getApplicationContext());
		}

		@Override
		protected String doInBackground(String... params) {
			// perform long running operation operation
			getStickyData();

			return null;
		}

		@Override
		protected void onPreExecute() {
			// Things to be done before execution of long running operation. For
			m_ProgressDialog = ProgressDialog.show(PrivateActivity.this,
					"Please wait...", "Connecting To Server...", true);
		}

		@Override
		protected void onPostExecute(String result) {
			// execution of result of Long time consuming operation
			m_ProgressDialog.setMessage("Records Loaded Successfully...");

			m_ProgressDialog.dismiss();
			stickyAdapter.notifyDataSetChanged();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// Things to be done while execution of long running operation is in
			// progress. For example updating ProgessDialog
		}
	}

	public boolean getStickyData() {

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		String uristr = "http://puskin.in/sticky/ajax/getsticky.php";

		/* login.php returns true if username and password is equal to saranga */
		HttpPost httppost = new HttpPost(uristr);
		boolean status = false;

		try {

			// Add user name and password
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("stickyFilterType",
					"private"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			Log.w(LIST_EXAMPLE, "Execute HTTP Post Request");

			HttpResponse response = httpclient.execute(httppost);

			String str = parseAndPrepareData(response.getEntity().getContent())
					.toString();
			Log.w(LIST_EXAMPLE, str);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return status;
	}

	private StringBuilder parseAndPrepareData(InputStream is) {
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
			JSONArray stickyJsonArray = stickyrespjson.getJSONArray("result");

			String returnString = "";

			for (int i = 0; i < stickyJsonArray.length(); i++) {
				StickyData stkData = new StickyData();

				stkData.setId(Integer.parseInt(stickyJsonArray.getJSONObject(i)
						.getString("id")));
				stkData.setText(stickyJsonArray.getJSONObject(i).getString(
						"text"));
				stkData.setDueDate(stickyJsonArray.getJSONObject(i).getString(
						"due_date"));
				stkData.setName(stickyJsonArray.getJSONObject(i).getString(
						"name"));
				stkData.setPriority(stickyJsonArray.getJSONObject(i).getString(
						"priority"));

				stickyDataList.add(stkData);

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Return full string
		return jsonResp;
	}
}
