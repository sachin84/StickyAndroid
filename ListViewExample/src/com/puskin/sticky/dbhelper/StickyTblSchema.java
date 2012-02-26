package com.puskin.sticky.dbhelper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class StickyTblSchema {
	public static final String StickyTable = "sticky";
	private SQLiteDatabase database;
	public static final String DB_STICKY_TBL_HELPER = "STICKY Schema";
	
	public static final String STICKY_ID = "_id";
	public static final String STICKY_USER_ID = "_userId";
	public static final String STICKY_TITLE = "_title";
	public static final String STICKY_TEXT = "_text";
	public static final String STICKY_PROGRESS= "_progress";
	public static final String STICKY_PRIORITY= "_priority";
	public static final String STICKY_DUEDATE = "_duedate";
	public static final String STICKY_TYPE = "_sticky_type";
	public static final String STICKY_CREATED_AT = "_created_at";
	public static final String STICKY_UPDATED_AT = "_updated_at";
	public static final String STICKY_IS_ENABLED = "_is_enabled";
	public static final String STICKY_IS_SYNCHED = "_is_synched";
	
	
	public StickyTblSchema(SQLiteDatabase db) {
		this.database = db;
		Log.i(DB_STICKY_TBL_HELPER, "OnStart Called...");
	}

	public void onCreate() {

		String StickyTbl = " CREATE TABLE sticky ( ";
		StickyTbl += " _id integer NOT NULL PRIMARY KEY, _userId integer NOT NULL, _title VARCHAR( 150 ),";
		StickyTbl += " _text VARCHAR( 500 ), _priority VARCHAR( 20 ),";
		StickyTbl += " _duedate DATETIME, _created_at DATETIME,";
		StickyTbl += " _updated_at DATETIME, _progress varchar(20),";
		StickyTbl += " _is_enabled BOOL, _is_synched BOOL, sticky_uuid INTEGER, _sticky_type CHAR) ";
//
		database.execSQL(StickyTbl);
	}

	public void onUpgrade() {

		database.execSQL("DROP TABLE IF EXISTS " + StickyTable);
		onCreate();
	}
}
