package com.tab.tdroid.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tab.tdroid.utils.ULogger;

/**
 * 
 * @author tab
 * @see 2016-6-13 16:27:30
 */
public class JsonUtilsTab {

	/**
	 * 从数据中得到list
	 * 
	 * @param rootStr
	 *            服务端返回的json
	 * @param cls
	 *            list里面的对象
	 * @param parentkey
	 *            data
	 * @param childkey
	 *            list
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<?> getArrayList(String rootStr, Class<?> cls,
			String parentkey, String childkey) throws Exception {
		ArrayList<?> list = null;

		JSONObject dataobj = JsonUtilsTab.getJsonObject(
				new JSONObject(rootStr), parentkey);

		if (dataobj.toString().length() > 2) {
			Json2ObjectTab obj = new Json2ObjectTab();
			list = (ArrayList<?>) obj.JsonTArray(cls, dataobj, childkey);
		} else {
			list = new ArrayList<Object>();
		}

		return list;
	}

	/**
	 * 从数据中得到list
	 * 
	 * @param rootStr
	 * @param cls
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<?> getArrayList(String rootStr, Class<?> cls,
			String key) throws Exception {
		JSONObject obj = new JSONObject(rootStr);
		Json2ObjectTab j2o = new Json2ObjectTab();
		ArrayList<?> list = null;
		list = (ArrayList<?>) j2o.JsonTArray(cls, obj, key);
		return list;
	}

	/**
	 * 从数据中得到list
	 * 
	 * @param cls
	 * @param array
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<?> getArrayList(Class<?> cls, JSONArray array)
			throws Exception {
		Json2ObjectTab j2o = new Json2ObjectTab();
		ArrayList<?> list = null;
		list = (ArrayList<?>) j2o.JsonTArray(cls, array);
		return list;
	}

	public static Object getClassInfo(String rootStr, Class<?> cls,
			String parentkey, String childkey) throws Exception {

		JSONObject parentobj = JsonUtilsTab.getJsonObject(new JSONObject(
				rootStr), parentkey);

		Json2ObjectTab obj = new Json2ObjectTab();

		return obj.JsonTObject(cls, parentobj, childkey);

	}

	public static Object getClassInfo(String rootStr, Class<?> cls,
			String parentkey) throws Exception {
		JSONObject obj = new JSONObject(rootStr);
		Json2ObjectTab jobj = new Json2ObjectTab();
		return jobj.JsonTObject(cls, obj, parentkey);
	}

	/**
	 * JSON数组是否有值
	 * 
	 * @return
	 */
	public static boolean isTrueJSON(String str, String key) {
		try {
			JSONObject json = new JSONObject(str);
			String value = json.getString(key);
			if (value.length() > 5) {
				ULogger.d("数组有数据");
				return true;
			}
		} catch (JSONException e) {
		}

		return false;
	}

	public static Integer getInt(JSONObject obj, String key)
			throws JSONException {
		return obj.isNull(key) ? 0 : obj.getInt(key);
	}

	public static double getDouble(JSONObject obj, String key)
			throws JSONException {
		return obj.isNull(key) ? 0 : obj.getDouble(key);
	}

	public static boolean getBoolean(JSONObject obj, String key)
			throws JSONException {
		return obj.isNull(key) ? false : obj.getBoolean(key);
	}

	public static String getString(JSONObject obj, String key)
			throws JSONException {
		return obj.isNull(key) ? "" : obj.getString(key);
	}

	public static JSONObject getJsonObject(JSONObject obj, String key)
			throws JSONException {
		return obj.isNull(key) ? new JSONObject() : obj.getJSONObject(key);
	}

	public static JSONArray getJSONArray(JSONObject obj, String key)
			throws JSONException {
		return obj.isNull(key) ? new JSONArray() : obj.getJSONArray(key);
	}

	public static Long getLong(JSONObject obj, String key) throws JSONException {
		return obj.isNull(key) ? 0 : obj.getLong(key);
	}

}
