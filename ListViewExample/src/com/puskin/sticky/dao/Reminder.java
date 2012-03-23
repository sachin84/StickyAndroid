package com.puskin.sticky.dao;

import java.util.Date;

public class Reminder {
	
	private int id;
	private int stickyId;
	private int periodId;
	private Date addedDate;
	private Date dueDate;
	private boolean isEnabled;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getStickyId() {
		return stickyId;
	}
	public void setStickyId(int stickyId) {
		this.stickyId = stickyId;
	}
	public int getPeriodId() {
		return periodId;
	}
	public void setPeriodId(int periodId) {
		this.periodId = periodId;
	}
	public Date getAddedDate() {
		return addedDate;
	}
	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public boolean isEnabled() {
		return isEnabled;
	}
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
}
