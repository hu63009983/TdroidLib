package com.tab.tdroid.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.tab.tdroid.utils.FileCenterTab;
import com.tab.tdroid.utils.StringUtilsTab;
import com.tab.tdroid.utils.ULogger;

public class HttpCore implements HttpStatic {

	/**
	 * 
	 * @author tab
	 * 
	 */
	public interface onProgressLinstener {
		void progress(int progress);
	}

	private HttpURLConnection connection;
	private HashMap<String, Object> HttpUpMsg;// 上传参数
	private HashMap<String, String> HttpHead;
	private HttpRsq resultRsq;

	private String HttpUrl;
	/** Set size of every block for post */
	private int BlockSize = 256 * 1024;

	private final String POST = "POST";

	private final String GET = "GET";

	private int HttpType = TYPE_POST;
	private onProgressLinstener listener;// 进度监听
	private String uploadFileCode = "";

	private final static String boundary = "*****";
	private final static String lineEnd = "\r\n";
	private final static String twoHyphens = "--";

	private final String ContentType_UPLOAD = COTENT_TYPE_DATA;

	private String ContentType = CONTENT_TYPE_URLENCODED;

	private boolean isJson = false;

	/**
	 * 接口调用
	 * 
	 * @param URL
	 *            路径
	 * @param data
	 *            参数
	 */
	public HttpCore(String URL, HashMap<String, Object> data) {
		HttpUrl = URL;
		HttpUpMsg = data;
		resultRsq = new HttpRsq();
	}

	/**
	 * 上传文件调用
	 * 
	 * @param URL
	 *            路径
	 * @param data
	 *            参数
	 * @param fileCode
	 *            文件域
	 */
	public HttpCore(String URL, HashMap<String, Object> data, String fileCode) {
		HttpUrl = URL;
		HttpUpMsg = data;
		resultRsq = new HttpRsq();
		uploadFileCode = fileCode;
	}

	/**
	 * 设置头文件
	 * 
	 * @param data
	 * @return
	 */
	public HttpCore setHeadMap(HashMap<String, String> data) {
		HttpHead = data;
		return this;
	}

	/**
	 * 设置HttpURLConnection的ContentType {@link HttpStatic.CONTENT_TYPE_JSON
	 * ,HttpStatic.CONTENT_TYPE_URLENCODED,HttpStatic.CONTENT_TYPE_TEXT,
	 * HttpStatic.CONTENT_TYPE_STREAM,HttpStatic.COTENT_TYPE_DATA}
	 * 
	 * @param contenttype
	 * @return
	 */
	public HttpCore setContentType(String contenttype) {
		this.ContentType = contenttype;
		if (CONTENT_TYPE_JSON.indexOf(this.ContentType) > 0) {
			isJson = true;
		}
		return this;
	}

	/**
	 * 设置POST提交或者GET提交
	 * 
	 * @param type
	 *            HttpStatic.TYPE_POST is POST or HttpStatic.TYPE_GET is GET
	 * @return
	 */
	public HttpCore setHttpType(int type) {
		HttpType = type;
		if (HttpType == TYPE_GET && HttpUpMsg != null) {
			HttpUrl = HttpUrl + "?";
			Set<String> keysSet = HttpUpMsg.keySet();
			Iterator<String> iterator = keysSet.iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				// httpRequest.setHeader(key, data.get(key).toString());
				HttpUrl = HttpUrl + key + "=" + HttpUpMsg.get(key).toString()
						+ "&";
			}

			HttpUrl = HttpUrl.substring(0, HttpUrl.lastIndexOf("&"));
		}
		return this;
	}

	/**
	 * 设置上传监听
	 * 
	 * @param onLinstener
	 */
	public void setOnProgressLinstener(onProgressLinstener onLinstener) {
		listener = onLinstener;
	}

	/**
	 * 上传文件
	 * 
	 * @param path
	 *            文件路径
	 * @return
	 */
	public HttpRsq upLoad(String path) {
		long length = 0;
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = BlockSize;// 256KB

		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File(path));
		} catch (FileNotFoundException e) {
			ExceptionError(ERROR_CODE_IOERROR, e);
			return resultRsq;
		}

		initHttpRequest(true);
		try {
			DataOutputStream outputStream = new DataOutputStream(
					connection.getOutputStream());
			try {
				if (HttpUpMsg != null) {
					Iterator<?> iter = HttpUpMsg.entrySet().iterator();
					while (iter.hasNext()) {
						@SuppressWarnings("rawtypes")
						Map.Entry entry = (Map.Entry) iter.next();
						String key = entry.getKey().toString();
						String val = entry.getValue().toString();
						outputStream
								.writeBytes(twoHyphens + boundary + lineEnd);
						outputStream
								.writeBytes("Content-Disposition: form-data; name=\""
										+ key + "\"" + lineEnd);
						outputStream.writeBytes(lineEnd);

						outputStream.writeBytes(URLEncoder.encode(val, "utf-8")
								+ lineEnd);
					}
				}

			} catch (Exception e) {
				ExceptionError(ERROR_CODE_IOERROR, e);
			}
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream
					.writeBytes("Content-Disposition: form-data; name=\""
							+ uploadFileCode + "\";filename=\"" + path + "\""
							+ lineEnd);
			outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
			outputStream.writeBytes(lineEnd);

			bytesAvailable = inputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			bytesRead = inputStream.read(buffer, 0, bufferSize);

			File uploadFile = new File(path);
			long totalSize = uploadFile.length() + 10 * 1024; // Get size of
																// file, bytes
			while (bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				length += bufferSize;
				int progress = (int) ((length * 100) / totalSize);
				if (listener != null) {
					listener.progress(progress <= 95 ? progress : 95);
				}

				bytesAvailable = inputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = inputStream.read(buffer, 0, bufferSize);
			}
			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens
					+ lineEnd);

			inputStream.close();
			outputStream.flush();
			outputStream.close();

			getResultInServer();
		} catch (Exception e) {
			ExceptionError(ERROR_CODE_FILEERROR, e);
		}
		return resultRsq;
	}

	/**
	 * 接口调用
	 * 
	 * @return
	 */
	public HttpRsq startMethod() {
		initHttpRequest(false);
		try {
			connection.connect();
			if (HttpType == TYPE_POST) {
				initHttpOutputStream();
			}
		} catch (Exception e) {
			ExceptionError(ERROR_CODE_IOERROR, e);
		}

		getResultInServer();

		return resultRsq;
	}

	/***
	 * 下载文件 data返回本地路径
	 * 
	 * @return
	 */
	public HttpRsq downLoad() {
		initConnection();
		try {
			connection.connect();
			initHttpInputStream(HttpUrl);
		} catch (Exception e) {
			ExceptionError(ERROR_CODE_IOERROR, e);
		}
		return resultRsq;
	}

	private void getResultInServer() {
		int serverResponseCode = 200;
		try {
			serverResponseCode = connection.getResponseCode();

			String resultString = "";
			// ULogger.e("" + serverResponseCode);
			// ULogger.e("getRequestMethod:" + connection.getRequestMethod());
			// ULogger.e("getResponseMessage:" +
			// connection.getResponseMessage());
			// ULogger.e("getHead:" + connection.getHeaderFields().toString());
			// ULogger.e("getContentType:" + connection.getContentType());
			// ULogger.e("getContentEncoding:" +
			// connection.getContentEncoding());
			// ULogger.e("getURL:" + connection.getURL());

			if (serverResponseCode == 200) {
				InputStream is = connection.getInputStream();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				resultString = sb.toString();
				br.close();
				is.close();

				getResultRsq(serverResponseCode, "", resultString);
			} else {
				getResultRsq(serverResponseCode, "", resultString);
			}

		} catch (IOException e) {
			ExceptionError(ERROR_CODE, e);
		}

		if (listener != null) {
			listener.progress(100);
		}
	}

	private void initHttpOutputStream() throws Exception {

		DataOutputStream outputStream = new DataOutputStream(
				connection.getOutputStream());

		outputStream.write(isJson ? getMethodJsonString().getBytes()
				: getMethodString().getBytes());
		outputStream.flush();
		outputStream.close();
	}

	private void initHttpInputStream(String url) throws Exception {
		long length = 0;
		String path = StringUtilsTab.getPicCacheName(url);
		InputStream is = connection.getInputStream();
		int countentLength = connection.getContentLength();
		byte[] b = new byte[1024];
		int len;
		File file = FileCenterTab.getInstence().getFileByPath(
				StringUtilsTab.getPicCacheName(url));
		OutputStream out = new FileOutputStream(file);
		while ((len = is.read(b)) != -1) {
			out.write(b, 0, len);
			length += len;
			int progress = (int) ((length * 100) / countentLength);
			if (listener != null) {
				listener.progress(progress <= 95 ? progress : 95);
			}
		}

		getResultRsq(200, "", path);
		out.close();
		is.close();
	}

	private void initHttpRequest(boolean isUpload) {
		try {
			initHttpConnectionFirst(isUpload);
			initHttpConnectionHead();

		} catch (Exception e) {
			ExceptionError(ERROR_CODE_IOERROR, e);
		}
	}

	private void initConnection() {
		try {
			URL url = new URL(HttpUrl);
			connection = (HttpURLConnection) url.openConnection();
		} catch (Exception e) {
			ExceptionError(ERROR_CODE_IOERROR, e);
		}

	}

	private void initHttpConnectionFirst(boolean isUpload) throws Exception {
		initConnection();
		connection.setChunkedStreamingMode(BlockSize);// 256KB

		// Allow Inputs & Outputs
		connection.setDoInput(true);
		connection.setDoOutput(HttpType == TYPE_POST);
		connection.setUseCaches(false);
		// Enable POST method
		connection.setRequestMethod(HttpType == TYPE_POST ? POST : GET);

		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestProperty("Charset", "utf-8");
		if (isUpload) {
			ContentType = ContentType_UPLOAD;
		}
		if (!StringUtilsTab.isEmpty(ContentType)) {
			connection.setRequestProperty("Content-Type", ContentType);
		}
	}

	private void initHttpConnectionHead() {
		if (HttpHead == null) {
			return;
		}
		try {
			Set<String> keysSet = HttpHead.keySet();
			Iterator<String> iterator = keysSet.iterator();
			while (iterator.hasNext()) {
				Object key = iterator.next();
				connection.setRequestProperty(key.toString(), HttpHead.get(key)
						.toString());
			}
		} catch (Exception e) {
			ExceptionError(e);
		}
	}

	private String getMethodJsonString() {
		JSONObject obj = new JSONObject();
		if (HttpUpMsg != null) {

			try {
				Set<String> keysSet = HttpUpMsg.keySet();
				Iterator<String> iterator = keysSet.iterator();
				while (iterator.hasNext()) {
					Object key = iterator.next();
					obj.put(key.toString(), HttpUpMsg.get(key).toString());
				}
			} catch (Exception e) {
				ExceptionError(e);
			}

		}

		return obj.toString();
	}

	private String getMethodString() {
		StringBuffer obj = new StringBuffer();
		if (HttpUpMsg != null) {
			try {
				Set<String> keysSet = HttpUpMsg.keySet();
				Iterator<String> iterator = keysSet.iterator();
				while (iterator.hasNext()) {
					Object key = iterator.next();
					obj.append(key.toString())
							.append("=" + HttpUpMsg.get(key).toString())
							.append("&");
				}
			} catch (Exception e) {
				ExceptionError(e);
			}
		}
		return obj.length() > 1 ? obj.toString().substring(0,
				obj.toString().length() - 1) : obj.toString();
	}

	/**
	 * 无法连接异常
	 * 
	 * @param e
	 */
	private void ExceptionError(Exception e) {
		ULogger.e(e);
	}

	/**
	 * 连接通畅情况下报异常
	 * 
	 * @param code
	 * @param e
	 */
	private void ExceptionError(int code, Exception e) {
		ULogger.e(e);
		getResultRsq(code,
				code == ERROR_CODE_FILEERROR ? "上传文件异常!" : "网络连接异常!", "");
	}

	private HttpRsq getResultRsq(int code, String errormsg, Object data) {
		resultRsq.httpcode = code;
		resultRsq.result = code == 200 ? true : false;
		resultRsq.data = data;
		resultRsq.errormsg = code >= 400 && code <= 500 ? "服务器异常!" : errormsg;
		return resultRsq;
	}
}
