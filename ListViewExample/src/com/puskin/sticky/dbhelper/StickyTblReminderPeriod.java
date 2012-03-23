package com.puskin.sticky.dbhelper;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class StickyTblReminderPeriod {

	public static final String REMINDER_PERIOD_TABLE = "reminder_period";
	public static final String REMINDER_PERIOD_ID = "_id";
	public static final String REMINDER_PERIOD_NAME = "_period_name";
	public static final String REMINDER_PERIOD_IS_ENABLE = "_is_enabled";
	
	public static final String DB_TBL_HELPER = "DB-HELPER Reminder Period";

	public static void onCreate(SQLiteDatabase database) {

		Log.i(DB_TBL_HELPER, "onCreate Called...");

		String reminderTbl = "CREATE TABLE \"reminder_period\" (\"_id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"_period_name\" CHAR NOT NULL,";
		reminderTbl += "  \"_is_enabled\" BOOL DEFAULT false)";

		database.execSQL(reminderTbl);

		InsertRecord( database);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {

		database.execSQL("DROP TABLE IF EXISTS " + REMINDER_PERIOD_TABLE);
		onCreate(database);
	}

	public static void InsertRecord(SQLiteDatabase database) {

		ContentValues cv1 = new ContentValues();
		cv1.put(REMINDER_PERIOD_NAME, "No Repeat");
		cv1.put(REMINDER_PERIOD_IS_ENABLE, "true");
		database.insert(REMINDER_PERIOD_TABLE, null, cv1);
		
		ContentValues cv2 = new ContentValues();
		cv2.put(REMINDER_PERIOD_NAME, "Daily");
		cv2.put(REMINDER_PERIOD_IS_ENABLE, "true");
		database.insert(REMINDER_PERIOD_TABLE, null, cv2);


		ContentValues cv3 = new ContentValues();
		cv3.put(REMINDER_PERIOD_NAME, "Bi Weekly");
		cv3.put(REMINDER_PERIOD_IS_ENABLE, "true");
		database.insert(REMINDER_PERIOD_TABLE, null, cv3);

		ContentValues cv4 = new ContentValues();
		cv4.put(REMINDER_PERIOD_NAME, "Weekly");
		cv4.put(REMINDER_PERIOD_IS_ENABLE, "true");
		database.insert(REMINDER_PERIOD_TABLE, null, cv4);
		
		ContentValues cv5 = new ContentValues();
		cv5.put(REMINDER_PERIOD_NAME, "Monthly");
		cv5.put(REMINDER_PERIOD_IS_ENABLE, "true");
		database.insert(REMINDER_PERIOD_TABLE, null, cv5);

		ContentValues cv6 = new ContentValues();
		cv6.put(REMINDER_PERIOD_NAME, "Yearly");
		cv6.put(REMINDER_PERIOD_IS_ENABLE, "true");
		database.insert(REMINDER_PERIOD_TABLE, null, cv6);


	}
}
