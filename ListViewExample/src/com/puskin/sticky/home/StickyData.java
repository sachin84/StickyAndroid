package com.puskin.sticky.home;

import com.puskin.sticky.home.R;

public class StickyData {
private int Id;
private String Text;
private String DueDate;
private String Name;
private boolean isSelected;
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
private String Priority;

}
