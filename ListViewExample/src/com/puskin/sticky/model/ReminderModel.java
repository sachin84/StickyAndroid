package com.puskin.sticky.model;

import java.text.SimpleDateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.puskin.sticky.dao.Reminder;
import com.puskin.sticky.dao.Sticky;
import com.puskin.sticky.dbhelper.DatabaseHelper;
import com.puskin.sticky.dbhelper.StickyReminderTblSchema;
import com.puskin.sticky.dbhelper.StickyTblReminderPeriod;
import com.puskin.sticky.dbhelper.StickyTblSchema;

public class ReminderModel {
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	public static final String REMINDER_MODEL = "Reminder Model";

	public ReminderModel(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public int AddReminder(Reminder reminder) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		Log.i(REMINDER_MODEL, "Insert Record in ");
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String duedt = sdfDate.format(reminder.getDueDate());
		String addeddt = sdfDate.format(reminder.getAddedDate());

		ContentValues cv = new ContentValues();
		cv.put(StickyReminderTblSchema.REMINDER_STICKY_ID,
				reminder.getStickyId());
		cv.put(StickyReminderTblSchema.REMINDER_PERIOD_ID,
				reminder.getPeriodId());
		cv.put(StickyReminderTblSchema.REMINDER_DUE_DATE, duedt);
		cv.put(StickyReminderTblSchema.REMINDER_ADDED_DATE, addeddt);
		cv.put(StickyReminderTblSchema.REMINDER_IS_ENABLED, "true");

		long reminderId = db.insert(StickyReminderTblSchema.REMINDER_TABLE, "",
				cv);
		Log.i(REMINDER_MODEL, "reminderId==" + reminderId);

		db.close();
		return (int) reminderId;
	}

	public boolean EditReminder(Reminder reminder) {
//		String reminderId = "";
		if (reminder.getId() <= 0) {
			Log.i(REMINDER_MODEL, "Unbale to Edit Invalid Reminder ID!");
			return false;
		}
//		Cursor reminderCur = getReminderDatafrmStickyId(reminder.getStickyId());
//		if (reminderCur.moveToFirst()) {
//			do {
//				reminderId = reminderCur.getString(reminderCur
//						.getColumnIndex("_id"));
//			} while (reminderCur.moveToNext());
//			reminderCur.close();
//		} else {
//			return true;
//		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String reminderIdCol = "_id";
		Log.i(REMINDER_MODEL, "Editing Record... ");
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String duedt = sdfDate.format(reminder.getDueDate());
		String addeddt = sdfDate.format(reminder.getAddedDate());
		
		String reminderId = String.valueOf(reminder.getId());

		ContentValues cv = new ContentValues();
		// cv.put(StickyReminderTblSchema.REMINDER_STICKY_ID,
		// reminder.getStickyId() );
		cv.put(StickyReminderTblSchema.REMINDER_PERIOD_ID,
				reminder.getPeriodId());
		cv.put(StickyReminderTblSchema.REMINDER_DUE_DATE, duedt);
		// cv.put(StickyReminderTblSchema.REMINDER_ADDED_DATE, addeddt);
		cv.put(StickyReminderTblSchema.REMINDER_IS_ENABLED, "true");

		int status = db.update(StickyReminderTblSchema.REMINDER_TABLE, cv,
				reminderIdCol + "=?", new String[] { reminderId });
		Log.i(REMINDER_MODEL, "update status==" + status);

		db.close();
		return true;
	}

	public int getReminderCount() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cur = db.rawQuery("Select * from "
				+ StickyReminderTblSchema.REMINDER_TABLE, null);
		int count = cur.getCount();
		cur.close();
		return count;
	}

	public int isValidReminder(int reminderId) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String query = "SELECT * FROM "
				+ StickyReminderTblSchema.REMINDER_TABLE;
		query += " where _id = " + reminderId;

		Cursor cur = db.rawQuery(query, null);
		int count = cur.getCount();
		cur.close();
		return count;
	}

	public Cursor getReminderDatafrmStickyId(int stickyId) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String query = "SELECT * FROM "
				+ StickyReminderTblSchema.REMINDER_TABLE;
		query += " as sr inner join "+StickyTblReminderPeriod.REMINDER_PERIOD_TABLE+" as rp on(sr._periodId = rp._id)";
		query += " where _stickyId = " + stickyId;

		Cursor cur = db.rawQuery(query, null);
		return cur;

	}

	public Cursor getReminderData(int reminderId) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String query = "SELECT * FROM "
				+ StickyReminderTblSchema.REMINDER_TABLE;
		query += " where _id = " + reminderId;

		Cursor cur = db.rawQuery(query, null);
		return cur;

	}

	public int deleteStickyReminder(int stickyId) {
		if (stickyId <= 0) {
			Log.i(REMINDER_MODEL, "Unbale to Delete Invalid Sticky ID!");
			return -1;
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();

		int status = db.delete(StickyReminderTblSchema.REMINDER_TABLE,
				StickyReminderTblSchema.REMINDER_STICKY_ID + "=?",
				new String[] { String.valueOf(stickyId) });
		
		return status;
	}
	
	public int deleteReminder(int reminderId) {
		if (reminderId <= 0) {
			Log.i(REMINDER_MODEL, "Unbale to Delete Invalid Sticky ID!");
			return -1;
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();

		return db.delete(StickyReminderTblSchema.REMINDER_TABLE,
				StickyReminderTblSchema.REMINDER_ID + "=?",
				new String[] { String.valueOf(reminderId) });
	}
}
