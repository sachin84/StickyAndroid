package com.puskin.sticky.model;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.puskin.sticky.dbhelper.DatabaseHelper;
import com.puskin.sticky.dbhelper.StickyTblReminderPeriod;

public class ReminderPeriodModel {
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	private final String TableName = "reminder_period";
	
	public ReminderPeriodModel(Context context)
	{
		dbHelper = new DatabaseHelper(context);
	}
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}

	public Cursor getAllReminderPeriods() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cur = db.rawQuery("SELECT * FROM " +TableName, null);
//		db.close();
		return cur;

	}

	public Cursor getReminderPeriod(String name) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String query = "SELECT * FROM " +TableName+" where "+StickyTblReminderPeriod.REMINDER_PERIOD_NAME +"= '"+name+"'";
		Cursor cur = db.rawQuery(query, null);
//		db.close();
		return cur;

	}
}
