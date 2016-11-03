package com.tab.tdroid.utils;

import android.util.Log;

/**
 * android.util.Log的封装
 * 
 * @author tab_tang
 * 
 */
public class ULogger {
	public static final int LEVEL_ALL = 0;
	public static final int LEVEL_DEBUG = 2;
	public static final int LEVEL_INFO = 3;
	public static final int LEVEL_ERROR = 5;

	/**
	 * 是否显示log信息
	 */

	public static String TAG = "Tdroidlib";
	private static int level = LEVEL_ALL;

	// public static int level = LEVEL_ERROR;

	/**
	 * LEVEL_ALL,LEVEL_DEBUG,LEVEL_INFO,LEVEL_ERROR
	 * 
	 * @param l
	 */
	public static void setLevel(int l) {
		level = l;
	}

	public static void d(String msg) {
		if (level <= LEVEL_DEBUG) {
			Log.d(TAG, msg);
		}
	}

	public static void d(Throwable tr) {
		if (level <= LEVEL_DEBUG) {
			Log.d(TAG, "", tr);
		}
	}

	public static void d(String msg, Throwable tr) {
		if (level <= LEVEL_DEBUG) {
			Log.d(TAG, msg, tr);
		}
	}

	public static void i(String msg) {
		if (level <= LEVEL_INFO) {
			Log.i(TAG, msg);
		}
	}

	public static void i(Throwable tr) {
		if (level <= LEVEL_INFO) {
			Log.i(TAG, "", tr);
		}
	}

	public static void i(String msg, Throwable tr) {
		if (level <= LEVEL_INFO) {
			Log.i(TAG, msg, tr);
		}
	}

	public static void e(String msg) {
		if (level <= LEVEL_ERROR) {
			Log.e(TAG, msg);
		}
	}

	public static void e(Throwable tr) {
		if (level <= LEVEL_ERROR) {
			Log.e(TAG, "", tr);
		}
	}

	public static void e(String msg, Throwable tr) {
		if (level <= LEVEL_ERROR) {
			Log.e(TAG, msg, tr);
		}
	}
}
