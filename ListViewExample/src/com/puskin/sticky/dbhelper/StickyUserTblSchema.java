package com.puskin.sticky.dbhelper;

import java.util.Date;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class StickyUserTblSchema {

	public static final String UserTable = "user";
	private SQLiteDatabase database;
	public static final String DB_USER_TBL_HELPER = "User Schema";

	public static final String USER_ID = "_id";
	public static final String USER_NAME = "_username";
	public static final String USER_EMAIL = "_email";
	public static final String USER_FIRSTNAME = "_firstname";
	public static final String USER_LASTNAME = "_lastname";
	public static final String USER_GENDER = "_gender";
	public static final String USER_REGISTER_AT = "_register_at";
	public static final String USER_UPDATED_AT = "_updated_at";
	public static final String USER_IS_ENABLED = "_is_enabled";
	public static final String USER_IS_SYNCHED = "_is_synched";

	
	public StickyUserTblSchema(SQLiteDatabase db) {
		this.database = db;
		Log.i(DB_USER_TBL_HELPER, "OnStart Called...");
	}

	public void onCreate() {

		String userTbl = " CREATE TABLE user ( ";
		userTbl += " _id integer NOT NULL PRIMARY KEY AUTOINCREMENT,";
		userTbl += " _username VARCHAR( 100 ) NULL,";
		userTbl += " _firstname VARCHAR( 100 ) NULL,";
		userTbl += " _lastname VARCHAR( 100 ) NULL,";
		userTbl += " _email VARCHAR( 60 ) NULL,";
		userTbl += " _created_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP,";
		userTbl += " _updated_at DATETIME,";
		userTbl += " _is_enabled BOOL, _is_synched BOOL, user_uuid INTEGER";
		userTbl += " )";

		database.execSQL(userTbl);
	}

	public void onUpgrade() {

		database.execSQL("DROP TABLE IF EXISTS " + UserTable);
		onCreate();
	}
}
