package com.puskin.sticky.dbhelper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class StickyReminderTblSchema {
	
	private SQLiteDatabase database;
	public static final String REMINDER_TABLE = "sticky_reminder";
	public static final String REMINDER_ID = "_id";
	public static final String REMINDER_STICKY_ID = "_stickyId";
	public static final String REMINDER_PERIOD_ID = "_periodId";
	public static final String REMINDER_ADDED_DATE = "_added_date";
	public static final String REMINDER_DUE_DATE = "_due_date";
	public static final String REMINDER_IS_ENABLED = "_is_enabled";
	
	public static final String REMINDER_DB_TBL_HELPER = "DB-HELPER Reminder";
	
	public StickyReminderTblSchema(SQLiteDatabase db) {
		this.database = db;
		Log.i(REMINDER_DB_TBL_HELPER, "OnStart Called...");
	}
	
	public void onCreate() {

		Log.i(REMINDER_DB_TBL_HELPER, "onCreate Called...");

		String reminderTbl = "CREATE TABLE sticky_reminder ( ";
		reminderTbl += "_id integer PRIMARY KEY  AUTOINCREMENT  NOT NULL,_stickyId INT NULL,";
		reminderTbl += "_periodId INT NULL,_added_date DATETIME NULL,";
		reminderTbl += "_due_date DATETIME NULL,_is_enabled BOOL NULL)";
		
		this.database.execSQL(reminderTbl);

	}

	public void onUpgrade() {

		database.execSQL("DROP TABLE IF EXISTS " + REMINDER_TABLE);
		onCreate();
	}

}
