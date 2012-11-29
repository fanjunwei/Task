package com.baoxue.task.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Profile {
	final static String TABLE_NAME = "profile";
	private static Profile instance = null;
	private SQLiteDatabase db = null;

	public static Profile getProfile() {
		if (instance == null) {
			instance = new Profile();
		}
		return instance;
	}

	private Profile() {
		db = dbHelper.getInstance().getWritableDatabase();
	}

	public void setValue(String key, String value) {
		if (key == null) {
			return;
		}
		if (value == null) {
			db.delete(TABLE_NAME, "[_key]=?", new String[] { key });
		}
		Cursor cursor = db.rawQuery("select _id from " + TABLE_NAME
				+ " where [_key]=?", new String[] { key });
		ContentValues cv = new ContentValues();
		cv.put("_key", key);
		cv.put("_value", value);
		if (cursor.getCount() == 0) {
			db.insert(TABLE_NAME, null, cv);
		} else {
			cursor.moveToNext();
			int id = cursor.getInt(0);
			db.update(TABLE_NAME, cv, "_id=?",
					new String[] { Integer.toString(id) });
		}
		cursor.close();
	}

	public String getValue(String key) {
		String value = null;
		Cursor cursor = db.rawQuery("select [_value] from " + TABLE_NAME
				+ " where [_key]=?", new String[] { key });
		try {
			if (cursor.getCount() == 0) {
				value = null;
			} else {
				cursor.moveToNext();
				value = cursor.getString(0);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return value;
	}
}
