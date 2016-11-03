package com.tab.tdroid.db;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TabDBHelper extends SQLiteOpenHelper {
	public static String DBNAME = "tdroid.db";
	private final static int VERSION = 1;
	private static TabDBHelper instance = null;

	public static final String TABLE_NAME = "tabletab";

	public static final String COLUMN_NAME_CONTENT = "content";
	public static final String COLUMN_NAME_KEY = "key";
	public static final String COLUMN_NAME_HASH = "hash";
	public static final String COLUMN_NAME_ID = "id";
	public static final String COLUMN_NAME_TOKEN = "token";

	public static TabDBHelper getInstance(Context context) {
		if (instance == null) {
			synchronized (TabDBHelper.class) {
				if (instance == null) {
					instance = new TabDBHelper(context);
				}
			}
		}
		return instance;
	}

	private TabDBHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		AddDb(db);
	}

	private void AddDb(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		CreateDB(db, TABLE_NAME);
	}

	private void CreateDB(SQLiteDatabase db, String tableName) {
		String createStr = "CREATE TABLE " + tableName + " ("
				+ COLUMN_NAME_CONTENT + " TEXT, " + COLUMN_NAME_HASH
				+ " TEXT, " + COLUMN_NAME_KEY + " TEXT, " + COLUMN_NAME_TOKEN
				+ " TEXT, " + COLUMN_NAME_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT );";
		db.execSQL(createStr);

		String unique = "CREATE UNIQUE INDEX `unique-" + tableName + "` ON "
				+ tableName + " (" + COLUMN_NAME_KEY + "," + COLUMN_NAME_TOKEN
				+ ")";
		db.execSQL(unique);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	/**
	 * @param key
	 * @param token
	 * @param content
	 *            传进的参数为data里面的数据
	 * @param hash
	 */
	public void put(String key, String token, String content, String hash) {
		SQLiteDatabase db = instance.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(COLUMN_NAME_CONTENT, content);
		values.put(COLUMN_NAME_KEY, key);
		values.put(COLUMN_NAME_TOKEN, token);
		values.put(COLUMN_NAME_HASH, hash);
		if (db.isOpen()) {
			db.replace(TABLE_NAME, null, values);
		}
	}

	public HashMap<String, String> get(String key, String token) {
		SQLiteDatabase db = instance.getReadableDatabase();
		HashMap<String, String> map = null;
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME
					+ " where " + COLUMN_NAME_KEY + " = ? AND "
					+ COLUMN_NAME_TOKEN + " = ? ", new String[] { key, token });
			if (cursor.moveToNext()) {
				map = new HashMap<String, String>();
				map.put(COLUMN_NAME_KEY, cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_KEY)));
				map.put(COLUMN_NAME_CONTENT, cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_CONTENT)));
				map.put(COLUMN_NAME_HASH, cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_HASH)));
				map.put(COLUMN_NAME_TOKEN, cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_TOKEN)));
			}
			cursor.close();
		}
		return map;
	}

}
