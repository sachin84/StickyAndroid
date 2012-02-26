package com.puskin.sticky.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String dbName = "stickyDB";
	

	private static final int DATABASE_VERSION = 35;
	public static final String DB_HELPER = "DataBase Helper";

	public DatabaseHelper(Context context) {
		super(context, dbName, null, DATABASE_VERSION);

		Log.i(DB_HELPER, "OnStart Called...");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(DB_HELPER, "onCreate Called...");

//		String userTbl = " CREATE TABLE user (id integer NOT NULL PRIMARY KEY AUTOINCREMENT,username text NOT NULL,";
//		userTbl += " password text,email text,firstname text,lastname text,'register_at' date,gender text,'updated_at' date)";
//
//		db.execSQL(userTbl);

		StickyTblReminderPeriod.onCreate(db);
		StickyUserTblSchema usersch = new StickyUserTblSchema(db);
		usersch.onCreate();
		StickyTblSchema stickySchema = new StickyTblSchema(db);
		stickySchema.onCreate();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		StickyUserTblSchema usersch = new StickyUserTblSchema(db);
		usersch.onUpgrade();
		
//		db.execSQL("DROP TABLE IF EXISTS " + UserTable);
//		onCreate(db);
	}

}
