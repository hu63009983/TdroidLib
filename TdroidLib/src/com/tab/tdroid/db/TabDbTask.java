package com.tab.tdroid.db;

import java.util.HashMap;

import org.json.JSONObject;

import com.tab.tdroid.json.JsonUtilsTab;
import com.tab.tdroid.net.HttpRsq;

import android.content.Context;

public class TabDbTask {
	final TABDao dao;

	public TabDbTask(Context context) {
		dao = new TABDao(context);
	}

	public HashMap<String, String> get(String key, String userid) {
		return dao.get(key, userid);
	}

	/**
	 * 请调用StringUtilsTab{@link getKEY}
	 * 
	 * @param rsq
	 * @param key
	 * @param userid
	 */
	public void put(HttpRsq rsq, String key, String userid) {
		if (rsq.result) {
			int size = 0;
			try {
				JSONObject obj = new JSONObject(rsq.toString());
				size = JsonUtilsTab.getString(obj, "data").length();
			} catch (Exception e) {
			}
			if (size > 2) {
				String hash = "";
				try {
					JSONObject obj = new JSONObject(rsq.data.toString());
					hash = JsonUtilsTab.getString(obj, "hash");
				} catch (Exception e) {
				}
				dao.putDB(key, userid, rsq.data.toString(), hash);
			}
		}

	}
}
