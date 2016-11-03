package com.tab.tdroid.net;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.tab.tdroid.Tdroid;

/**
 * 任务核心：将task现场返回的数据进行Handler处理用回调的方式返回数据
 * 
 * @author tab
 * 
 */
public class HttpTaskManager implements HttpStatic {

	private static HttpTaskManager taskManager;
	/** 网络任务和其ID的映射MAP */
	private final HashMap<Integer, HttpTask> taskMap;
	/** 当前网络任务ID的游标 */
	private static int taskID;
	protected Handler handler;

	@SuppressLint("UseSparseArrays")
	private HttpTaskManager() {
		taskMap = new HashMap<Integer, HttpTask>();
		taskID = TASK_START;
	}

	public static HttpTaskManager getInstence() {
		if (taskManager == null) {
			synchronized (HttpTaskManager.class) {
				if (taskManager == null) {
					taskManager = new HttpTaskManager();
				}
			}
		}
		return taskManager;
	}

	public Handler getHandler() {
		if (handler == null) {
			handler = new Handler(Tdroid.app.getMainLooper()) {
				@Override
				public synchronized void handleMessage(Message msg) {
					HttpTask task = getTask(msg.arg1);
					if (msg.what == 1) {
						if (task.getRsq().httpcode != 200) {
						}
						if (task.getOnHttpBackLinstener() != null) {
							task.getOnHttpBackLinstener().OnBack(task.getRsq());
						}
						clearTask(task);
					} else if (msg.what == 2) {
						if (task.getOnProgressListener() != null) {
							task.getOnProgressListener().progress(msg.arg2);
						}
					} else if (msg.what == 3) {
						clearTask(task);
					}

				}
			};
		}
		return handler;
	}

	public void cancelAllTask() {
		int count = taskMap.size();
		for (int i = 0; i < count; i++) {
			HttpTask task = taskMap.get(i);
			task.finish();
			task = null;
		}
		taskMap.clear();
	}

	public synchronized HttpTask getTask(int taskID) {
		HttpTask task = taskMap.get(taskID);
		return task;
	}

	public synchronized int putTask(HttpTask task) {
		if (taskID > TASK_END) {
			taskID = TASK_START;
		}
		task.taskID = taskID;
		taskMap.put(taskID, task);
		return taskID++;
	}

	public synchronized void replace(int taskID, HttpTask task) {
		taskMap.remove(taskID);
		taskMap.put(taskID, task);
	}

	public void onDestory() {
		cancelAllTask();
		taskMap.clear();
	}

	private synchronized void removeTask(int taskID) {
		taskMap.remove(taskID);
	}

	private void clearTask(HttpTask task) {
		if (task != null) {
			removeTask(task.taskID);
			task.finish();
			task = null;
		}
	}

}
