package com.tab.tdroid.net;

import java.util.HashMap;

import android.os.Handler;
import android.os.Message;

import com.tab.tdroid.Tdroid;
import com.tab.tdroid.net.HttpCore.onProgressLinstener;
import com.tab.tdroid.utils.NetWorkUtils;
import com.tab.tdroid.utils.ULogger;

public class HttpTask {
	int taskID = 0;
	private Handler handler;
	private HttpRsq rsq;// 返回对象
	private String HttpUrl;

	private OnHttpBackListener listener;
	private HashMap<String, String> HeadMap;
	private HashMap<String, Object> JSONMap;

	private int Method = HttpStatic.TYPE_POST;

	private boolean isUPdateImg = false;
	private String uploatPath = "";
	private String fileCode = "";
	@SuppressWarnings("unused")
	private boolean isCompress = false;
	private onProgressLinstener progresslistener;

	private String ContentType = "";

	public HttpTask setOnHttpBackLinstener(OnHttpBackListener listener) {
		this.listener = listener;
		return this;
	}

	public HttpTask setOnProgressListener(onProgressLinstener listener) {
		this.progresslistener = listener;
		return this;
	}

	/**
	 * 设置头参数
	 * 
	 * @param headMap
	 */
	public HttpTask setHeadData(HashMap<String, String> headMap) {
		this.HeadMap = headMap;
		return this;
	}

	/**
	 * 设置上传文件参数
	 * 
	 * @param filePath
	 * @param fileCode
	 * @return
	 */
	public HttpTask setFileContent(String filePath, String fileCode,
			boolean isCompress) {
		uploatPath = filePath;
		this.fileCode = fileCode;
		isUPdateImg = true;
		this.isCompress = isCompress;
		return this;
	}

	/**
	 * 
	 * @param type
	 *            HttpStatic.TYPE_POST is POST or HttpStatic.TYPE_GET is GET
	 */
	public HttpTask setMethodType(int type) {
		Method = type;
		return this;
	}

	/**
	 * Http-ContenType
	 * 
	 * @param type
	 * @return
	 */
	public HttpTask setContentType(String type) {
		this.ContentType = type;
		return this;
	}

	public onProgressLinstener getOnProgressListener() {
		return this.progresslistener;
	}

	public OnHttpBackListener getOnHttpBackLinstener() {
		return this.listener;
	}

	public HttpRsq getRsq() {
		return rsq;
	}

	public HttpTask(String url, HashMap<String, Object> data) {
		iniHttp(url, data);
	}

	private void iniHttp(String url, HashMap<String, Object> data) {
		rsq = new HttpRsq();
		handler = HttpTaskManager.getInstence().getHandler();
		HttpUrl = url;
		JSONMap = data;
		HttpTaskManager.getInstence().putTask(this);
	}

	public void start() {
		try {
			Tdroid.service.execute(new HttpTaskRunnable());
		} catch (Exception e) {
			ULogger.e(e);
		}

	}

	public void destory() {
		Message msg = new Message();
		msg.what = 3;
		msg.arg1 = taskID;
		handler.sendMessage(msg);
	}

	/** 清理系统内存 */
	public void finish() {
		rsq = null;
		handler = null;
		HttpUrl = null;
		listener = null;
	}

	onProgressLinstener p = new onProgressLinstener() {

		@Override
		public void progress(int progress) {

			try {
				Message msg = new Message();
				msg.what = 2;
				msg.arg1 = taskID;
				msg.arg2 = progress;
				handler.sendMessage(msg);
			} catch (Exception e) {

			}

		}
	};

	/************************************ Core functions *********************************/

	private class HttpTaskRunnable implements Runnable {

		private HttpCore httpCore;

		public HttpTaskRunnable() {
			if (isUPdateImg) {
				httpCore = new HttpCore(HttpUrl, JSONMap, fileCode);
				if (progresslistener != null) {
					httpCore.setOnProgressLinstener(p);
				}
			} else {
				httpCore = new HttpCore(HttpUrl, JSONMap);
			}
			if (HeadMap != null) {
				httpCore.setHeadMap(HeadMap);
			}
			httpCore.setHttpType(Method);
			httpCore.setContentType(ContentType);
		}

		@Override
		public void run() {
			if (NetWorkUtils.isNetworkAvailable(Tdroid.app)) {
				if (isUPdateImg) {
					rsq = httpCore.upLoad(uploatPath);
				} else {
					rsq = httpCore.startMethod();
				}
			} else {
				rsq.result = false;
				rsq.errormsg = "请检查网络";
			}
			sendMsg(buildMsg(taskID));
		}

		/**
		 * 为便于管理Task
		 * 
		 * @param id
		 * @return
		 */
		protected Message buildMsg(int id) {
			Message msg = new Message();
			msg.what = 1;
			msg.arg1 = id;// 网络任务ID
			return msg;
		}

		/** 发送消息到TaskManager */
		void sendMsg(Message msg) {
			try {
				handler.sendMessage(msg);
			} catch (Exception e) {

			}
		}
	}

	public void sendMsgToListener(HttpRsq rsq) {
		if (listener != null) {
			listener.OnBack(rsq);
		}
	}
}
