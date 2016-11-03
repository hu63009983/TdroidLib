package com.tab.tdroid.net;

public interface HttpStatic {

	/* 任务编码范围规划 */
	int SUBTASK_START = 100;
	int SUBTASK_END = 999;
	int TASK_START = 1000;
	int TASK_END = 100000;

	int ERROR_CODE = 10000;
	/** JSON解析错误的时候 */
	int ERROR_CODE_JSONERROR = ERROR_CODE + 1;
	/** Http接口错误的时候 */
	int ERROR_CODE_IOERROR = ERROR_CODE + 2;
	/** 上传文件错误的时候 */
	int ERROR_CODE_FILEERROR = ERROR_CODE + 3;

	final int TYPE_GET = 10001;
	final int TYPE_POST = 10000;

	/**
	 * application/json; charset=UTF-8
	 */
	final String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";
	/**
	 * application/x-www-form-urlencoded; charset=UTF-8
	 */
	final String CONTENT_TYPE_URLENCODED = "application/x-www-form-urlencoded; charset=UTF-8";
	/**
	 * text/html;charset=utf-8
	 */
	final String CONTENT_TYPE_TEXT = "text/html;charset=utf-8";
	/**
	 * application/octet-stream
	 */
	final String CONTENT_TYPE_STREAM = "application/octet-stream";
	/**
	 * multipart/form-data;boundary=*****
	 */
	final String COTENT_TYPE_DATA = "multipart/form-data;boundary=*****";

}
