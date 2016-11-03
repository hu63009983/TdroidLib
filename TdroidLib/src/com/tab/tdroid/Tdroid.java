package com.tab.tdroid;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;

import com.tab.tdroid.net.HttpTaskManager;
import com.tab.tdroid.utils.FileCenterTab;
import com.tab.tdroid.utils.KScreen;

/**
 * init something
 * 
 * @see 2016年6月18日09:46:47 添加universal-image-loader作为图片加载的核心源码地址 {@link https
 *      ://github.com/nostra13/Android-Universal-Image-Loader}<br>
 *      混淆： {@code -keepattributes Signature}<br>
 *      {@code -keepattributes *Annotation*} <br>
 *      {@code -keep class sun.misc.Unsafe *; } <br>
 *      {@code -keep class com.google.gson.examples.android.model.** *; } <br>
 * @author tab
 * 
 */
public class Tdroid {
	public static Application app;
	static int cpuNums = Runtime.getRuntime().availableProcessors();

	public static final ExecutorService service = Executors
			.newFixedThreadPool(cpuNums * 50);

	/**
	 * get in application
	 * 
	 * @param context
	 */
	public static void onIniLib(Application context) {
		app = context;
		KScreen.initialize(app);
		HttpTaskManager.getInstence();
		FileCenterTab.getInstence();
	}

	public static void onDestory() {
		app = null;
		HttpTaskManager.getInstence().onDestory();
	}
}
