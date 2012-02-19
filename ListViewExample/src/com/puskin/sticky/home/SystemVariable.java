package com.puskin.sticky.home;

import android.app.Application;
import com.puskin.sticky.home.R;

public class SystemVariable extends Application {

	private String myState;
	private String loginStatus;
	private String sesionId;

	public SystemVariable() {
		super();
	}

	public String getLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(String loginStatus) {
		this.loginStatus = loginStatus;
	}

	public String getSesionId() {
		return sesionId;
	}

	public void setSesionId(String sesionId) {
		this.sesionId = sesionId;
	}

	public String getMyState() {
		return myState;
	}

	public void setMyState(String myState) {
		this.myState = myState;
	}

}
