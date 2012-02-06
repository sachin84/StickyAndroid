package com.crri.listview.example;

import android.app.Application;

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
