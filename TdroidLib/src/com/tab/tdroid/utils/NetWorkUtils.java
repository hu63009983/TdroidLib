package com.tab.tdroid.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkUtils {
    /**
     * 判断网络是否连通 静态方法
     * 
     * @param ctx
     * @return boolean true:连通
     */
    public static boolean isNetworkAvailable(Context ctx) {
	ConnectivityManager cm = (ConnectivityManager) ctx
		.getSystemService(Context.CONNECTIVITY_SERVICE);
	NetworkInfo info = cm.getActiveNetworkInfo();
	return (info != null && info.isConnected());
    }

    /**
     * 判断是否是wifi连接
     */
    public static boolean isWifi(Context context) {
	ConnectivityManager cm = (ConnectivityManager) context
		.getSystemService(Context.CONNECTIVITY_SERVICE);

	if (cm == null)
	    return false;
	return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

    }

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
	Intent intent = new Intent("/");
	ComponentName cm = new ComponentName("com.android.settings",
		"com.android.settings.WirelessSettings");
	intent.setComponent(cm);
	intent.setAction("android.intent.action.VIEW");
	activity.startActivityForResult(intent, 0);
    }

}
