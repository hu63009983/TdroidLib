package com.tab.tdroid.json;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;

/**
 * 
 * @author tab
 * @see 2016-6-13 16:25:43
 * 
 */
@SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
public class Json2ObjectTab {

	/**
	 * get json array and to ArrayList
	 * 
	 * @param cls
	 * @param json
	 * @param key
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes" })
	public ArrayList JsonTArray(Class<?> cls, JSONObject json, String key)
			throws Exception {
		return JsonTArray(cls, json.getJSONArray(key));
	}

	/**
	 * Json To Class
	 * 
	 * @param cls
	 *            to class
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public Object JsonTObject(Class<?> cls, JSONObject json) throws Exception {
		Class<?> realClass = cls;
		Object tempObject = realClass.newInstance();
		invokeMethod(tempObject, realClass, json);
		return tempObject;
	}

	/**
	 * 
	 * @param cls
	 * @param json
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public Object JsonTObject(Class<?> cls, JSONObject json, String key)
			throws Exception {
		Class<?> realClass = cls;
		Object tempObject = realClass.newInstance();
		boolean isJsonObject = false;
		try {
			json.getJSONObject(key);
			isJsonObject = true;
		} catch (Exception e) {
			
		}
		if (isJsonObject) {
			invokeMethod(tempObject, realClass, json.getJSONObject(key));
		} else {
			JSONArray array = json.getJSONArray(key);
			for (int i = 0; i < array.length(); i++) {
				invokeMethod(tempObject, realClass, array.getJSONObject(i));
			}
		}

		return tempObject;
	}

	/**
	 * JSONArray to ArrayList
	 * 
	 * @param cls
	 *            to class
	 * @param jsonArray
	 *            Json
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList JsonTArray(Class<?> cls, JSONArray jsonArray)
			throws Exception {
		return invokeArrayCode(cls, jsonArray);
	}

	/**
	 * set value to Method what is not ArrayList
	 * 
	 * @param toObj
	 * @param cls
	 * @param json
	 * @return
	 * @throws Exception
	 */
	private Object invokeMethod(Object toObj, Class<?> cls, JSONObject json)
			throws Exception {
		Iterator<String> objectKeys = json.keys();
		while (objectKeys.hasNext()) {
			String keyString = objectKeys.next();
			Method[] m = cls.getDeclaredMethods();
			for (int i = 0; i < m.length; i++) {
				if (m[i].getGenericReturnType() == void.class) {
					String name = m[i].getName().toLowerCase();
					Object value = json.get(keyString);
					if (name.toLowerCase().indexOf(keyString.toLowerCase()) != -1) {
						Type[] t = m[i].getGenericParameterTypes();
						for (int j = 0; j < t.length; j++) {
							if (t[j].toString().indexOf("List") > 0) {
								if (t[j] instanceof ParameterizedType) {
									ParameterizedType pt = (ParameterizedType) t[j];
									Class<?> chlid = (Class<?>) pt
											.getActualTypeArguments()[0];
									invokeArray(toObj, chlid, m[i], json,
											keyString);
								}

							} else if (t[j] == String.class) {
								m[i].invoke(toObj, value.toString());
							} else if (t[j] == int.class) {
								m[i].invoke(toObj, value);
							} else if (t[j] == boolean.class) {
								m[i].invoke(toObj, value);
							} else if (t[j] == double.class) {
								m[i].invoke(toObj, value);
							} else if (t[j] == float.class) {
								m[i].invoke(toObj, value);
							} else if (t[j] == long.class) {
								m[i].invoke(toObj, value);
							} else if (t[j] == Date.class) {
								SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm:ss");
								Date date = simpleDateFormat.parse(value
										.toString());
								m[i].invoke(toObj, date);
							}
						}
					}
				}
			}
		}
		return toObj;
	}

	/**
	 * Json to child ArrayList
	 * 
	 * @param toObj
	 * @param cls
	 * @param m
	 * @param json
	 * @param key
	 * @throws Exception
	 */
	private void invokeArray(Object toObj, Class<?> cls, Method m,
			JSONObject json, String key) throws Exception {
		boolean isArray = false;
		try {
			json.getJSONArray(key);
			isArray = true;
		} catch (Exception e) {
		}
		if (isArray) {
			m.invoke(toObj, invokeArrayCode(cls, json.getJSONArray(key)));
		}

	}

	private ArrayList<Object> invokeArrayCode(Class<?> cls, JSONArray jsonArray)
			throws Exception {
		int listLength = jsonArray.length();
		ArrayList<Object> list = new ArrayList<Object>();
		for (int i = 0; i < listLength; i++) {
			JSONObject object = jsonArray.getJSONObject(i);
			Object tempObject = cls.newInstance();
			list.add(invokeMethod(tempObject, cls, object));
		}
		return list;
	}
}
