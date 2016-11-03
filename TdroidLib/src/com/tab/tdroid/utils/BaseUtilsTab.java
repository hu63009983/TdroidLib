package com.tab.tdroid.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class BaseUtilsTab {

	/**
	 * start Activity
	 * 
	 * @param context
	 * @param cls
	 */
	public static void startActivity(Context context, Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(context, cls);
		context.startActivity(intent);
	}

	/**
	 * start call phone
	 * 
	 * @param context
	 * @param phone
	 */
	public static void startPhonePc(Context context, String phone) {
		Intent intent = new Intent(Intent.ACTION_DIAL,
				Uri.parse("tel:" + phone));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);

	}

	/**
	 * start Message
	 * 
	 * @param context
	 * @param phone
	 *            number
	 * @param body
	 *            content
	 */
	public static void startPhoneMsg(Context context, String phone, String body) {
		Intent intent = new Intent(android.content.Intent.ACTION_SENDTO,
				Uri.parse("smsto:" + phone));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("sms_body", body);
		context.startActivity(intent);
	}

	/**
	 * start home
	 * 
	 * @param context
	 */
	public static void startHome(Context context) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		context.startActivity(intent);
	}

}
