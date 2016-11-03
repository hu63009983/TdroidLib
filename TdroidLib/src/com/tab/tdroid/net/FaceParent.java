package com.tab.tdroid.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import com.tab.tdroid.db.TabDBHelper;
import com.tab.tdroid.db.TabDbTask;
import com.tab.tdroid.json.JsonUtilsTab;
import com.tab.tdroid.net.HttpCore.onProgressLinstener;
import com.tab.tdroid.utils.StringUtilsTab;

import android.content.Context;
import android.os.CountDownTimer;

/**
 * 使用步骤: 1、调用createDate();<br>
 * 2、设置date参数 date.put(key,value);<br>
 * 3、new task HttpTask(MyApplication.rootPath + ADVERT + PROFIT,
 * getClientParam(data), 1); <br>
 * 4、设置监听(监听内调用解析)<br>
 * task.setOnHttpBackLinstener(new OnHttpbackLinstener() {<br>
 * <br>
 * 
 * <br>
 * public void onBack(HttpRsq rsq) {<br>
 * 
 * }); <br>
 * 5、调用startTask();<br>
 * 6、监听完毕调用stopTask();<br>
 * 作用:Http中转站
 * 
 * @author tab
 */
public class FaceParent {

	/** MD5转换、sign签名构造 */
	private HttpTask task;
	/** 参数 */
	private HashMap<String, Object> data;
	private HashMap<String, String> headMap;
	private OnHttpBackListener Parentlistener;
	private OnHttpBackListener tasklistener;
	private onProgressLinstener progresslistener;
	private MyCount mcTimer;
	private long allTime = 20 * 1000;
	private long time = 1000;
	private String httpUrl;
	private int type = 1;
	private String contentType = "";
	protected final int POST = 1;
	protected final int GET = 2;

	protected final String KEY_HASH = TabDBHelper.COLUMN_NAME_HASH;
	protected final String KEY_CONTENT = TabDBHelper.COLUMN_NAME_CONTENT;

	class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			stopTask(new HttpRsq("获取数据超时"));
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}
	}

	/**
	 * 设置HttpContentType
	 * 
	 * @param type
	 */
	protected void setContentType(String type) {
		contentType = type;
	}

	protected FaceParent addPamameter(String key, Object value) {
		isOnCreate();
		data.put(key, value);
		return this;
	}

	/**
	 * 设置接口POST or GET
	 * 
	 * @param type
	 */
	protected void setMehtodType(int type) {
		this.type = type;
	}

	/**
	 * 设置接口回调
	 * 
	 * @param listener
	 */
	protected void setOnHttpBackListener(OnHttpBackListener listener) {
		this.Parentlistener = listener;
	}

	/**
	 * 实现接口 解析JSON
	 * 
	 * @param listener
	 */
	protected void setOnJsonBackListener(OnHttpBackListener listener) {
		this.tasklistener = listener;
	}

	/**
	 * 上传文件进度监听
	 * 
	 * @param listener
	 */
	protected void setOnProgressLinstener(onProgressLinstener listener) {
		progresslistener = listener;
	}

	/**
	 * 上传文件
	 * 
	 * @param path
	 * @param code
	 */
	protected void start(String path, String code) {
		if (StringUtilsTab.isEmpty(httpUrl)) {
			stopTask();
			return;
		}
		isOnCreate();
		task = new HttpTask(httpUrl, data);
		task.setFileContent(path, code, false);
		if (tasklistener != null) {
			task.setOnHttpBackLinstener(tasklistener);
		}
		if (progresslistener != null) {
			task.setOnProgressListener(progresslistener);
		}
		task.start();
	}

	/**
	 * please use setFaceUrl();
	 */
	protected void start(final Context context, final String userid) {
		startTask(context, userid);

	}

	protected void start(final Context context) {
		startTask(context, "-1");
	}

	private void saveDate(Context context, HttpRsq rsq, String userid) {
		if (!userid.equals("-1")) {
			TabDbTask tabtask = new TabDbTask(context);
			tabtask.put(rsq, StringUtilsTab.getKEY(data, userid, httpUrl),
					userid);
		}
	}

	private void startTask(final Context context, final String userid) {
		if (StringUtilsTab.isEmpty(httpUrl)) {
			stopTask();
			return;
		}
		isOnCreate();
		task = new HttpTask(httpUrl, data);
		task.setMethodType(type == 1 ? HttpStatic.TYPE_POST
				: HttpStatic.TYPE_GET);
		task.setHeadData(headMap);
		if (!StringUtilsTab.isEmpty(contentType)) {
			task.setContentType(contentType);
		}

		if (tasklistener != null) {
			task.setOnHttpBackLinstener(new OnHttpBackListener() {

				@Override
				public void OnBack(HttpRsq rsq) {
					saveDate(context, rsq, userid);
					tasklistener.OnBack(rsq);
				}
			});
		} else {
			task.setOnHttpBackLinstener(Parentlistener);
		}

		task.start();
		mcTimer.start();
	}

	/**
	 * 设置超时
	 * 
	 * @param time
	 */
	protected void setOutTime(long time) {
		allTime = time;
	}

	/**
	 * 构造方法调用可以不调用
	 */
	protected void OnCreate() {
		data = new HashMap<String, Object>();
		headMap = new HashMap<String, String>();
		mcTimer = new MyCount(allTime, time);
	}

	/**
	 * 设置接口路径
	 * 
	 * @param url
	 */
	protected void setFaceUrl(String url) {
		httpUrl = url;
	}

	/**
	 * 获取本地数据 返回的参数有两个key: hash,content
	 * 
	 * @param context
	 * @param userid
	 * @return
	 */
	protected HashMap<String, String> getLocationHashMap(Context context,
			String userid) {
		TabDbTask tabtask = new TabDbTask(context);
		if (StringUtilsTab.isEmpty(httpUrl)) {
			return null;
		}
		return tabtask
				.get(StringUtilsTab.getKEY(data, userid, httpUrl), userid);
	}

	/**
	 * 设置HTTP Head
	 * 
	 * @param d
	 */
	protected void setHeadData(HashMap<String, String> d) {
		isOnCreate();
		headMap = d;
	}

	/**
	 * 停止
	 * 
	 * @param rsq
	 */
	protected void stopTask(HttpRsq rsq) {

		if (Parentlistener != null) {
			Parentlistener.OnBack(rsq);
		}
		if (mcTimer != null) {
			mcTimer.cancel();
			mcTimer = null;
		}
		if (Parentlistener != null) {
			Parentlistener = null;
		}

		if (tasklistener != null) {
			tasklistener = null;
		}
		data = null;
		headMap = null;
		task.destory();
	}

	/**
	 * 停止
	 */
	protected void stopTask() {
		if (mcTimer != null) {
			mcTimer.cancel();
			mcTimer = null;
		}
		if (Parentlistener != null) {
			Parentlistener = null;
		}
		if (tasklistener != null) {
			tasklistener = null;
		}
		data = null;
		headMap = null;
		task.destory();
	}

	/**
	 * 把数据源HashMap转换成json
	 * 
	 * @param map
	 */
	public static String hashMapToJson(HashMap<String, Object> map) {
		JSONObject obj = new JSONObject();
		for (Iterator<?> it = map.entrySet().iterator(); it.hasNext();) {
			@SuppressWarnings("unchecked")
			Entry<String, Object> e = (Entry<String, Object>) it.next();
			try {
				obj.put(e.getKey(), e.getValue());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return obj.toString();
	}

	/**
	 * 解析Class数据
	 * 
	 * @param rsq
	 * @param cls
	 * @param parent
	 * @param child
	 * @return
	 */
	protected HttpRsq getHttpRsqJsonCls(HttpRsq rsq, Class<?> cls,
			String parent, String child) {
		if (rsq.result) {
			if (JsonUtilsTab.isTrueJSON(rsq.data.toString(), parent)) {
				try {
					rsq.data = JsonUtilsTab.getClassInfo(rsq.data.toString(),
							cls, parent, child);
				} catch (Exception e) {
					e.printStackTrace();
					setRsqError(rsq);
				}
			}
		}
		return rsq;
	}

	/**
	 * 解析Class数据
	 * 
	 * @param rsq
	 * @param cls
	 * @param parent
	 * @return
	 */
	protected HttpRsq getHttpRsqJsonCls(HttpRsq rsq, Class<?> cls, String parent) {
		if (rsq.result) {
			try {
				rsq.data = JsonUtilsTab.getClassInfo(rsq.data.toString(), cls,
						parent);
			} catch (Exception e) {
				e.printStackTrace();
				setRsqError(rsq);
			}
		}
		return rsq;
	}

	/**
	 * 解析Array数据
	 * 
	 * @param rsq
	 * @param cls
	 * @param parent
	 * @param child
	 * @return
	 */
	protected HttpRsq getHttpRsqJsonArray(HttpRsq rsq, Class<?> cls,
			String parent, String child) {
		if (rsq.result) {
			try {
				rsq.data = JsonUtilsTab.getArrayList(rsq.data.toString(), cls,
						parent, child);
			} catch (Exception e) {
				e.printStackTrace();
				setRsqError(rsq);
			}
		}
		return rsq;
	}

	protected ArrayList<?> getHttpRsqJsonArray(String content, Class<?> cls,
			String parent, String child) {

		try {
			return JsonUtilsTab.getArrayList(content, cls, parent, child);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ArrayList<Object>();
	}

	/**
	 * 解析Array数据
	 * 
	 * @param rsq
	 * @param cls
	 * @param key
	 * @return
	 */
	protected HttpRsq getHttpRsqJsonArray(HttpRsq rsq, Class<?> cls, String key) {
		if (rsq.result) {
			try {
				rsq.data = JsonUtilsTab.getArrayList(rsq.data.toString(), cls,
						key);
			} catch (Exception e) {
				e.printStackTrace();
				setRsqError(rsq);
			}
		}
		return rsq;
	}

	/**
	 * 解析Object数据
	 * 
	 * @param rsq
	 * @param cls
	 * @param parent
	 * @param child
	 * @return
	 */
	protected HttpRsq getHttpRsqJsonObj(HttpRsq rsq, String parent, String child) {
		if (rsq.result) {
			try {
				JSONObject obj = new JSONObject(rsq.data.toString());
				JSONObject cobj = JsonUtilsTab.getJsonObject(obj, parent);
				if (cobj != null) {
					rsq.data = JsonUtilsTab.getString(cobj, child);
				} else {
					setRsqError(rsq);
				}

			} catch (Exception e) {
				e.printStackTrace();
				setRsqError(rsq);
			}
		}
		return rsq;
	}

	private void setRsqError(HttpRsq rsq) {
		rsq.result = false;
		rsq.errormsg = "解析参数失败";
	}

	private void isOnCreate() {
		if (mcTimer == null) {
			OnCreate();
		}
	}
}
