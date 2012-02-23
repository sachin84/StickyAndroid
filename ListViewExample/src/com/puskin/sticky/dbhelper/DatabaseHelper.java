package com.puskin.sticky.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String dbName = "stickyDB";
	public static final String UserTable = "user";
	public static final String USER_ID = "id";
	public static final String USER_NAME = "username";
	public static final String USER_EMAIL = "email";
	public static final String USER_FIRSTNAME = "firstname";
	public static final String USER_LASTNAME = "lastname";
	public static final String USER_GENDER = "gender";
	public static final String USER_REGISTER_AT = "register_at";
	public static final String USER_UPDATED_AT = "updated_at";

	private static final int DATABASE_VERSION = 35;
	public static final String DB_HELPER = "DataBase Helper";

	public DatabaseHelper(Context context) {
		super(context, dbName, null, DATABASE_VERSION);

		Log.i(DB_HELPER, "OnStart Called...");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.i(DB_HELPER, "onCreate Called...");

		String userTbl = " CREATE TABLE user (id integer NOT NULL PRIMARY KEY AUTOINCREMENT,username text NOT NULL,";
		userTbl += " password text,email text,firstname text,lastname text,'register_at' date,gender text,'updated_at' date)";

		db.execSQL(userTbl);

		StickyTblReminderPeriod.onCreate(db);
		// Inserts pre-defined departments
		// InsertSampleUser(db);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

		db.execSQL("DROP TABLE IF EXISTS " + UserTable);
		onCreate(db);
	}

}
