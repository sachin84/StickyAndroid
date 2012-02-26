package com.puskin.sticky.home;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.puskin.sticky.dao.User;

public class Login extends Activity {
	public static final String STICKY_LOGIN = "Login Page";
	final int LOGIN_SUCCESS = 5;
	final int LOGIN_CANCELLED = -5;
	private boolean loginStatus = false;
	private String respMessage = "";
	protected ProgressDialog login_ProgressDialog = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		Button b = (Button) findViewById(R.id.loginbutton);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doLogin();
			}
		});
	}

	@Override
	public void onBackPressed() {
		setResult(LOGIN_CANCELLED);
		this.finish();
		// super.onBackPressed();
	}

	private boolean doLogin() {
		EditText username = (EditText) findViewById(R.id.login);
		EditText password = (EditText) findViewById(R.id.password);

		if (username.getText().toString().length() > 0
				&& password.getText().toString().length() > 0) {

			new AuthenticateUser().execute("");

		} else {
			if (isNullOrBlank(username.getText().toString())) {
				Toast.makeText(getBaseContext(), "Invalid UserName",
						Toast.LENGTH_LONG).show();
			}
			if (isNullOrBlank(password.getText().toString())) {
				Toast.makeText(getBaseContext(), "Invalid Password",
						Toast.LENGTH_LONG).show();
			}
		}
		return true;
	}

	private void processServerResponse() {
		if (this.loginStatus) {
			SharedPreferences settings = getSharedPreferences("StickySettings",
					0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("loginstatus", "true");
			editor.putString("loggedInUserId", "2"); // NEED TO MODIFY HERE

			editor.commit();
			setResult(LOGIN_SUCCESS);
			finish();
		} else {
			Log.w(STICKY_LOGIN, "Unable to login...");
			if (isNullOrBlank(respMessage))
				respMessage = "Unable to Connect To Server. Please Try After Some Time!";
			Toast.makeText(getBaseContext(), respMessage, Toast.LENGTH_LONG)
					.show();

		}
	}

	private class AuthenticateUser extends AsyncTask<String, Void, String> {

		public AuthenticateUser() {
			login_ProgressDialog = new ProgressDialog(getApplicationContext());
		}

		@Override
		protected String doInBackground(String... params) {
			// perform long running operation operation
			postLoginData();
			return null;
		}

		@Override
		protected void onPreExecute() {
			// Things to be done before execution of long running operation. For
			login_ProgressDialog = ProgressDialog.show(Login.this,
					"Please wait...", "Connecting To Server...", true);
		}

		@Override
		protected void onPostExecute(String result) {
			// execution of result of Long time consuming operation
			login_ProgressDialog.setMessage("Validation Response...");
			login_ProgressDialog.dismiss();
			processServerResponse();

		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// Things to be done while execution of long running operation is in
			// progress. For example updating ProgessDialog
		}

	}

	private boolean postLoginData() {
		// Add user name and password
		EditText uname = (EditText) findViewById(R.id.login);
		String username = uname.getText().toString();

		EditText pword = (EditText) findViewById(R.id.password);
		String password = pword.getText().toString();

		// Create a new HttpClient and Post Header

		// Create a new HttpClient and Post Header

		boolean status = false;

		try {

			Log.v(STICKY_LOGIN, username);
			Log.v(STICKY_LOGIN, password);
			HttpClient httpclient = new DefaultHttpClient();
			String uristr = "http://www.puskin.in/sticky/ajax/login.php";
			HttpPost httppost = new HttpPost(uristr);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("userName", username));
			nameValuePairs
					.add(new BasicNameValuePair("userPassword", password));
			nameValuePairs.add(new BasicNameValuePair("getUserInfo", "true"));

			Log.w(STICKY_LOGIN, "Execute HTTP Post Request");

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);

			status = inputStreamToString(response.getEntity().getContent());

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return status;
	}

	private boolean inputStreamToString(InputStream is) {
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

		Log.w(STICKY_LOGIN, "Response==" + jsonResp.toString());

		boolean status = false;
		JSONObject stickyrespjson;
		try {
			stickyrespjson = new JSONObject(jsonResp.toString());
			JSONObject respJsonObj = stickyrespjson.getJSONObject("result");

			User user = new User();
			status = respJsonObj.getBoolean("status");
			String code = respJsonObj.getString("code");
			respMessage = respJsonObj.getString("message");
			String dueDate = "";

			if (respJsonObj.has("User")) {

				JSONObject userJsonObj = respJsonObj.getJSONObject("User");
				user.setId(userJsonObj.getInt("id"));
				user.setUsername(userJsonObj.getString("usr"));
				user.setPassword(userJsonObj.getString("pass"));
				user.setEmail(userJsonObj.getString("email"));
				user.setFirstname(userJsonObj.getString("firstname"));
				user.setLastname(userJsonObj.getString("lastname"));
				user.setGender(userJsonObj.getString("gender"));
				dueDate = userJsonObj.getString("dt");

			}

			if (!isNullOrBlank(dueDate)) {
				Log.i(STICKY_LOGIN, "dueDate==>" + dueDate);

				SimpleDateFormat curFormater = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				java.util.Date dateObj = null;
				try {

					dateObj = curFormater.parse(dueDate);
					user.setRegisterAt(dateObj);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Return full string
		return status;
	}

	private boolean isNullOrBlank(String s) {
		return (s == null || s.trim().equals("") || s.trim().equals("null"));
	}
}