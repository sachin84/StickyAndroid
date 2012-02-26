package com.puskin.sticky.model;

import java.text.SimpleDateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.puskin.sticky.dao.User;
import com.puskin.sticky.dbhelper.DatabaseHelper;
import com.puskin.sticky.dbhelper.StickyUserTblSchema;

public class UserModel {
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	private StickyUserTblSchema userSchema;
	public UserModel(Context context)
	{
		dbHelper = new DatabaseHelper(context);
	}
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	@SuppressWarnings("static-access")
	public int AddUser(User usr) {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String duedt = sdfDate.format(usr.getRegisterAt());
		String currdt = sdfDate.format(usr.getIsSynched());

		
		ContentValues cv = new ContentValues();
		cv.put(userSchema.USER_NAME, usr.getUsername());
		cv.put(userSchema.USER_EMAIL, usr.getEmail());
		cv.put(userSchema.USER_FIRSTNAME, usr.getFirstname());
		cv.put(userSchema.USER_LASTNAME, usr.getLastname());
		cv.put(userSchema.USER_GENDER, usr.getGender());
		cv.put(userSchema.USER_REGISTER_AT, currdt);
		cv.put(userSchema.USER_IS_ENABLED,"true");
		cv.put(userSchema.USER_IS_SYNCHED,"false");

		SQLiteDatabase db = dbHelper.getWritableDatabase();

		long userId = db.insert(userSchema.UserTable, userSchema.USER_UPDATED_AT, cv);
		db.close();
		
		return (int) userId;
	}

	public int getUserCount() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cur = db.rawQuery("Select * from " + userSchema.UserTable, null);
		int x = cur.getCount();
		cur.close();
		return x;
	}

	public Cursor getAllUsers() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		Cursor cur = database.rawQuery("SELECT * FROM " + userSchema.UserTable, null);
		return cur;

	}

//	public int UpdateUser(User usr) {
//		SQLiteDatabase db = this.getWritableDatabase();
//		ContentValues cv = new ContentValues();
//		cv.put(colName, usr.getName());
//		cv.put(colAge, usr.getAge());
//		cv.put(colDept, usr.getDept());
//		return db.update(UserTable, cv, colID + "=?",
//				new String[] { String.valueOf(usr.getID()) });
//
//	}

	public void DeleteEmp(User usr) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		database.delete(userSchema.UserTable, userSchema.USER_ID + "=?",
				new String[] { String.valueOf(usr.getId()) });
		database.close();

	}
}
