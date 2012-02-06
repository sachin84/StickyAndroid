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

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class ListViewExampleActivity extends TabActivity {

	public static final String LIST_EXAMPLE = "ListExample";

	// private static final String[] country = { "Iceland", "India",
	// "Indonesia",
	// "Iran", "Iraq", "Ireland", "Israel", "Italy", "Laos", "Latvia",
	// "Lebanon", "Lesotho ", "Liberia", "Libya", "Lithuania",
	// "Luxembourg" };
	// private static final String[] curr = { "ISK", "INR", "IDR", "IRR", "IQD",
	// "EUR", "ILS", "EUR", "LAK", "LVL", "LBP", "LSL ", "LRD", "LYD",
	// "LTL ", "EUR"
	//
	// };

	private static List<StickyData> stickyDataList = new ArrayList<StickyData>();

	private static String[] sampleStr3 = { "sampleText03" };
	private static String[] sampleStr4 = { "sampleText04" };

	public ListViewExampleActivity() {
		getStickyData();
		Log.i(LIST_EXAMPLE, "SIZE===>" + stickyDataList.size());
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
				convertView = mInflater.inflate(R.layout.listview, null);
				holder = new ViewHolder();
				holder.text = (TextView) convertView
						.findViewById(R.id.TextView01);
				holder.text2 = (TextView) convertView
						.findViewById(R.id.TextView02);
				holder.text3 = (TextView) convertView
						.findViewById(R.id.TextView03);
				holder.text4 = (TextView) convertView
						.findViewById(R.id.TextView04);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			convertView.setBackgroundColor((position & 1) == 1 ? Color.WHITE
					: Color.LTGRAY);

			StickyData dataObj = stickyDataList.get(position);

			Log.i(LIST_EXAMPLE, "Data_ID" + dataObj.getId());
			Log.i(LIST_EXAMPLE, "Data_ID" + dataObj.getText());
			Log.i(LIST_EXAMPLE, "DueDate" + dataObj.getDueDate());

			// convertView.setBackgroundDrawable
			holder.text.setText(String.valueOf(dataObj.getId()));
			holder.text2.setText(dataObj.getText());

			holder.text3.setText(dataObj.getPriority());
			holder.text4.setText(dataObj.getDueDate());

			return convertView;
		}

		class ViewHolder {
			TextView text;
			TextView text2;
			TextView text3;
			TextView text4;
		}
	}// close StickyListAdapter Class

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Context mContext = getApplicationContext();
		SystemVariable appState = ((SystemVariable) getApplicationContext());

		// appState.setLoginStatus("System Variable");
		Log.w(LIST_EXAMPLE, "STATE_VAR==>" + appState.getLoginStatus());

		setContentView(R.layout.main);

		//Setting Up Tabs
		TabHost tabHost = getTabHost();

		// Tab for Photos
		TabSpec publicspec = tabHost.newTabSpec("Public");
		publicspec.setIndicator("Public",
				getResources().getDrawable(R.drawable.icon_public_tab));
		Intent publicIntent = new Intent(this, PublicActivity.class);
		publicspec.setContent(publicIntent);

		// Tab for Songs
		TabSpec workspec = tabHost.newTabSpec("Work");
		// setting Title and Icon for the Tab
		workspec.setIndicator("Work",
				getResources().getDrawable(R.drawable.icon_work_tab));
		Intent workIntent = new Intent(this, WorkActivity.class);
		workspec.setContent(workIntent);

		// Tab for Videos
		TabSpec privatespec = tabHost.newTabSpec("Private");
		privatespec.setIndicator("Private",
				getResources().getDrawable(R.drawable.icon_private_tab));
		
		Intent privateIntent = new Intent(this, PrivateActivity.class);
		privatespec.setContent(privateIntent);

		// Adding all TabSpec to TabHost
		tabHost.addTab(publicspec); // Adding photos tab
		tabHost.addTab(workspec); // Adding songs tab
		tabHost.addTab(privatespec); // Adding videos tab
		
		
		
		
//
//		ListView l1 = (ListView) findViewById(R.id.PublicListing);
//		ColorDrawable divcolor = new ColorDrawable(Color.DKGRAY);
//		l1.setDivider(divcolor);
//		l1.setDividerHeight(2);
//
//		l1.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				// arg1.setBackgroundColor(Color.YELLOW);
//
//				Toast.makeText(getBaseContext(),
//						"You clciked " + stickyDataList.get(arg2).getId(),
//						Toast.LENGTH_LONG).show();
//			}
//		});
//
//		StickyListAdapter stickyAdapter = new StickyListAdapter(this);
//		l1.setAdapter(stickyAdapter);

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

			SharedPreferences settings = getSharedPreferences(LIST_EXAMPLE, 0);
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

	public boolean getStickyData() {

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		// String uristr = "http://10.0.2.2/sticky/ajax/getsticky.php";
		String uristr = "http://puskin.in/sticky/ajax/getsticky.php?stickyFilterType=public";

		/* login.php returns true if username and password is equal to saranga */
		HttpPost httppost = new HttpPost(uristr);
		boolean status = false;

		try {

			// Add user name and password
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("stickyFilterType",
					"public"));
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