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

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WorkActivity extends ListActivity {

	public static final String LIST_EXAMPLE = "WorkActivity";
	private static List<StickyData> stickyDataList = new ArrayList<StickyData>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.public_layout);
		//
		// ListView l1 = (ListView) findViewById(R.id.PublicListing);
		// ColorDrawable divcolor = new ColorDrawable(Color.DKGRAY);
		// l1.setDivider(divcolor);
		// l1.setDividerHeight(2);
		//
		// l1.setOnItemClickListener(new OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		// long arg3) {
		// // arg1.setBackgroundColor(Color.YELLOW);
		//
		// Toast.makeText(getBaseContext(),
		// "You clciked " + stickyDataList.get(arg2).getId(),
		// Toast.LENGTH_LONG).show();
		// }
		// });
		//
		// StickyListAdapter stickyAdapter = new StickyListAdapter(this);
		// l1.setAdapter(stickyAdapter);

		StickyListAdapter stickyAdapter = new StickyListAdapter(this);
		setListAdapter(stickyAdapter);

		ListView list = getListView();
		ColorDrawable divcolor = new ColorDrawable(Color.DKGRAY);
		list.setDivider(divcolor);
		list.setDividerHeight(2);

		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// arg1.setBackgroundColor(Color.YELLOW);

				Toast.makeText(getBaseContext(),
						"You clciked " + stickyDataList.get(arg2).getId(),
						Toast.LENGTH_LONG).show();
			}
		});

		// String[] values = new String[] { "Android", "iPhone",
		// "WindowsMobile",
		// "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
		// "Linux", "OS/2" };
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, values);
		// setListAdapter(adapter);

	}

	public WorkActivity() {
		// loading public data from web
		getStickyData();
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
				convertView = mInflater.inflate(R.layout.listviewwork, null);
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

	public boolean getStickyData() {

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		// String uristr = "http://10.0.2.2/sticky/ajax/getsticky.php";
		String uristr = "http://puskin.in/sticky/ajax/getsticky.php?stickyFilterType=work";

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
