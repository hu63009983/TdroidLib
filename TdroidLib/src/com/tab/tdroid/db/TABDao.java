package com.tab.tdroid.db;

import java.util.HashMap;

import android.content.Context;

public class TABDao {
	protected TabDBHelper dbHelper;

	public TABDao(Context context) {
		dbHelper = TabDBHelper.getInstance(context);
	}

	public void putDB(String key, String userid, String content, String hash) {
		dbHelper.put(key, userid, content, hash);
	}

	public HashMap<String, String> get(String key, String userid) {
		return dbHelper.get(key, userid);
	}
}
