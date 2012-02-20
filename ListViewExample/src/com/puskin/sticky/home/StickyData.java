package com.puskin.sticky.home;

import java.io.Serializable;

public class StickyData implements Serializable {

	private static final long serialVersionUID = 1L;
	private int Id;
	private String Text;
	private String DueDate;
	private String Name;
	private boolean isSelected;
	private String remainingDays;
	private String Priority;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getText() {
		return Text;
	}

	public void setText(String text) {
		Text = text;
	}

	public String getDueDate() {
		return DueDate;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public void setDueDate(String dueDate) {
		DueDate = dueDate;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getPriority() {
		return Priority;
	}

	public void setPriority(String priority) {
		Priority = priority;
	}

	public String getRemainingDays() {
		return remainingDays;
	}

	public void setRemainingDays(String remainingDays) {
		this.remainingDays = remainingDays;
	}


}
