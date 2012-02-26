package com.puskin.sticky.dao;

public class ReminderPeriod {
	private int id;
	private String ReminderPeriod;
	private boolean isEnable;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getReminderPeriod() {
		return ReminderPeriod;
	}

	public void setReminderPeriod(String reminderPeriod) {
		ReminderPeriod = reminderPeriod;
	}

	public boolean isEnable() {
		return isEnable;
	}

	public void setEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}
}
