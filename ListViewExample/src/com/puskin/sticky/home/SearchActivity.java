package com.puskin.sticky.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class SearchActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchsticky);
	
	}
	
}
