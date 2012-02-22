package com.puskin.sticky.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.puskin.sticky.dao.User;
import com.puskin.sticky.dbhelper.DatabaseHelper;

public class UserModel {
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
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
	
	void AddUser(User usr) {

		ContentValues cv = new ContentValues();
		cv.put(dbHelper.USER_NAME, usr.getUsername());
		cv.put(dbHelper.USER_EMAIL, usr.getEmail());
		cv.put(dbHelper.USER_FIRSTNAME, usr.getFirstname());
		cv.put(dbHelper.USER_LASTNAME, usr.getLastname());
		cv.put(dbHelper.USER_GENDER, usr.getGender());
		cv.put(dbHelper.USER_REGISTER_AT, usr.getRegister_at().toString());

		database.insert(dbHelper.UserTable, dbHelper.USER_UPDATED_AT, cv);
		
		this.close();

	}

	public int getUserCount() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cur = db.rawQuery("Select * from " + dbHelper.UserTable, null);
		int x = cur.getCount();
		cur.close();
		return x;
	}

	public Cursor getAllUsers() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		Cursor cur = db.rawQuery("SELECT * FROM " + dbHelper.UserTable, null);
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
		db.delete(dbHelper.UserTable, dbHelper.USER_ID + "=?",
				new String[] { String.valueOf(usr.getId()) });
		db.close();

	}
}
