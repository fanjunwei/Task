package com.baoxue.task.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.baoxue.task.CrashApplication;

public class dbHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "sec_db";
	private final static int DATABASE_VERSION = 9;
	private static dbHelper instance = null;
	AssetManager _asset;

	public static dbHelper getInstance()
	{
		initDB();
		return instance;
	}
	private dbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		_asset = context.getAssets();
	}

	public static void initDB() {
		if (instance == null) {
			instance = new dbHelper(CrashApplication.getCurrent());
		}
	}

	private void exe_sql_from_assets(SQLiteDatabase db, String name) {
		InputStream input = null;
		try {
			StringBuffer buffer = new StringBuffer();
			input = _asset.open(name);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					input));
			String line;

			while ((line = reader.readLine()) != null) {
				buffer.append(line + "\n");
				if (line.indexOf(";") != -1) {
					String sql = buffer.toString();
					buffer.delete(0, buffer.length());
					db.execSQL(sql);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		exe_sql_from_assets(db, "db_create_sql");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		exe_sql_from_assets(db, "db_update_sql");
		onCreate(db);
	}

	public Cursor select() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query("book_mark", null, null, null, null, null,
				"[_id] desc");
		return cursor;
	}

	public long insert(String Title) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("name", Title);
		long row = db.insert("book_mark", null, cv);
		return row;
	}

	public void delete(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = "_id" + "=?";
		String[] whereValue = { Integer.toString(id) };
		db.delete("book_mark", where, whereValue);
	}

	public void update(int id, String Title) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = "_id" + "=?";
		String[] whereValue = { Integer.toString(id) };
		ContentValues cv = new ContentValues();
		cv.put("name", Title);
		db.update("book_mark", cv, where, whereValue);
	}

}
