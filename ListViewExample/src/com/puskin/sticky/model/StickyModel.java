package com.puskin.sticky.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.puskin.sticky.dao.Sticky;
import com.puskin.sticky.dbhelper.DatabaseHelper;
import com.puskin.sticky.dbhelper.StickyTblSchema;

public class StickyModel {
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	private StickyTblSchema stickySchema;
	public static final String STICKY_MODEL = "Sticky Model";

	public StickyModel(Context context)
	{
		dbHelper = new DatabaseHelper(context);
	}
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public int AddSticky(Sticky sticky) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		Log.i(STICKY_MODEL, "Insert Record in ");
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String duedt = sdfDate.format(sticky.getDueDate());
		String currdt = sdfDate.format(sticky.getCreatedAt());
		
		ContentValues cv = new ContentValues();
		cv.put(StickyTblSchema.STICKY_USER_ID, String.valueOf(sticky.getUserId()) );
		cv.put(StickyTblSchema.STICKY_TITLE, sticky.getTitle());
		cv.put(StickyTblSchema.STICKY_TEXT, sticky.getText());
		cv.put(StickyTblSchema.STICKY_TYPE, sticky.getStickyType());
		cv.put(StickyTblSchema.STICKY_PROGRESS, sticky.getProgress());
		cv.put(StickyTblSchema.STICKY_PRIORITY, sticky.getPriority());
		
		cv.put(StickyTblSchema.STICKY_DUEDATE, duedt);
		cv.put(StickyTblSchema.STICKY_CREATED_AT, currdt);
		cv.put(StickyTblSchema.STICKY_IS_ENABLED,"true");
		cv.put(StickyTblSchema.STICKY_IS_SYNCHED,"false");
		
		long stickyId = db.insert(StickyTblSchema.StickyTable, StickyTblSchema.STICKY_UPDATED_AT, cv);
		Log.i(STICKY_MODEL, "stickyId=="+stickyId);

		
//		String sql = "INSERT INTO sticky (";
//		sql += "_userId,_title,_text,_progress,_priority,_duedate,_created_at,)";
//		sql += "VALUES ("+ String.valueOf(sticky.getUserId())+",";
//		sql += sticky.getTitle()+",";		
//		sql += sticky.getText()+",";		
//		sql += sticky.getProgress()+",";		
//		sql += sticky.getPriority()+",";
//		sql += sticky.getDueDate()+",";
//		sql += sticky.getCreatedAt();
//		sql += ")";
//		
//		Log.i(STICKY_MODEL, sql);
//
//		db.execSQL(sql );
		db.close();
		return (int) stickyId;
	}

	public boolean EditSticky(Sticky sticky) {
		
		if(sticky.getId()<=0){
			Log.i(STICKY_MODEL, "Unbale to Edit Invalid Sticky ID!");
			return false;
		}
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		String stickyIdCol ="_id";
		String stickyId = String.valueOf(sticky.getId());
		
		Log.i(STICKY_MODEL, "Editing Record... ");
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String duedt = sdfDate.format(sticky.getDueDate());
		String currdt = sdfDate.format(sticky.getCreatedAt());
		
		ContentValues cv = new ContentValues();
		cv.put(StickyTblSchema.STICKY_USER_ID, String.valueOf(sticky.getUserId()) );
		cv.put(StickyTblSchema.STICKY_TITLE, sticky.getTitle());
		cv.put(StickyTblSchema.STICKY_TEXT, sticky.getText());
		cv.put(StickyTblSchema.STICKY_TYPE, sticky.getStickyType());
		cv.put(StickyTblSchema.STICKY_PROGRESS, sticky.getProgress());
		cv.put(StickyTblSchema.STICKY_PRIORITY, sticky.getPriority());
		
		cv.put(StickyTblSchema.STICKY_DUEDATE, duedt);
		cv.put(StickyTblSchema.STICKY_CREATED_AT, currdt);		
		cv.put(StickyTblSchema.STICKY_IS_ENABLED,"true");
		cv.put(StickyTblSchema.STICKY_IS_SYNCHED,"false");

		int status = db.update(StickyTblSchema.StickyTable, cv, stickyIdCol+"=?",new String []{stickyId} );
		Log.i(STICKY_MODEL, "update status=="+status);

		db.close();
		return true;
	}
	
	public int getStickyCount() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cur = db.rawQuery("Select * from " + StickyTblSchema.StickyTable, null);
		int count = cur.getCount();
		cur.close();
		return count;
	}

	public Cursor getAllStickys(int userId, String type) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String query = "SELECT * FROM " + StickyTblSchema.StickyTable;
		query += " where _userId = "+userId;
		query += " and _sticky_type = '"+type+"'";
		
		Cursor cur = db.rawQuery(query, null);
		return cur;

	}

	public Cursor getStickyData(int stickyId) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String query = "SELECT * FROM " + StickyTblSchema.StickyTable;
		query += " where _id = "+stickyId;
		
		Cursor cur = db.rawQuery(query, null);
		return cur;

	}
//	public int UpdateSticky(Sticky usr) {
//		SQLiteDatabase db = this.getWritableDatabase();
//		ContentValues cv = new ContentValues();
//		cv.put(colName, usr.getName());
//		cv.put(colAge, usr.getAge());
//		cv.put(colDept, usr.getDept());
//		return db.update(StickyTable, cv, colID + "=?",
//				new String[] { String.valueOf(usr.getID()) });
//
//	}

	public int deleteSticky(int stickyId) {
		if(stickyId<=0){
			Log.i(STICKY_MODEL, "Unbale to Delete Invalid Sticky ID!");
			return -1;
		}
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		return db.delete(StickyTblSchema.StickyTable, StickyTblSchema.STICKY_ID + "=?",
				new String[] { String.valueOf(stickyId) });
	}
}
