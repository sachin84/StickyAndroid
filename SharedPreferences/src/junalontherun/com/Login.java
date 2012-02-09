package junalontherun.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
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
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity {
	public static final String LOGIN_AUTH = "LoginPrefs";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		/*
		 * Check if we successfully logged in before. If we did, redirect to
		 * home page
		 */
		SharedPreferences settings = getSharedPreferences(LOGIN_AUTH, 0);
		if (settings.getString("logged", "").toString().equals("logged")) {
			Intent intent = new Intent(Login.this, Home.class);
			startActivity(intent);
		}

		Button b = (Button) findViewById(R.id.loginbutton);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText username = (EditText) findViewById(R.id.login);
				EditText password = (EditText) findViewById(R.id.password);

				if (username.getText().toString().length() > 0
						&& password.getText().toString().length() > 0) {

					boolean loginStatus  = postLoginData();
					if(loginStatus)
					{
						Intent intent = new Intent(Login.this, Home.class);
						startActivity(intent);
						
					}
					
//					if (username.getText().toString().equals("test")
//							&& password.getText().toString().equals("test")) {
//						/*
//						 * So login information is correct, we will save the
//						 * Preference data and redirect to next class / home
//						 */
//						SharedPreferences settings = getSharedPreferences(
//								LOGIN_AUTH, 0);
//						SharedPreferences.Editor editor = settings.edit();
//						editor.putString("logged", "logged");
//						editor.commit();
//
//						Intent intent = new Intent(Login.this, Home.class);
//						startActivity(intent);
//					} else {
//						System.out.println("access Denied!");
//					}
				}
			}
		});
	}

	public boolean postLoginData() {
		
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		String uristr = "http://www.puskin.in/sticky/ajax/authenticate.php?requestType=UserAuthentication&userName=sachin&userPassword=sachin";

		/* login.php returns true if username and password is equal to saranga */
		HttpPost httppost = new HttpPost(uristr);
		boolean status = false;

		try {
			// Add user name and password
			EditText uname = (EditText) findViewById(R.id.login);
			String username = uname.getText().toString();

			EditText pword = (EditText) findViewById(R.id.password);
			String password = pword.getText().toString();

			Log.v(LOGIN_AUTH, username);
			Log.v(LOGIN_AUTH, password);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("username", username));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			Log.w(LOGIN_AUTH, "Execute HTTP Post Request");

			HttpResponse response = httpclient.execute(httppost);

			String str = inputStreamToString(response.getEntity().getContent())
					.toString();
			Log.w(LOGIN_AUTH, str);

			if (str.toString().contains("Success")) {
				Log.w(LOGIN_AUTH, "Login successful");
				System.out.println("Login successful");
				status = true;
			} else {
				Log.w(LOGIN_AUTH, "FALSE");
				System.out.println(str);
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