package com.tab.tdroid.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;

public class StringUtilsTab {
	
	
	

	/**
	 * 判断字符串是否是null或者“”
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.equals("") ? true : false;
	}

	/**
	 * 获取activity的名称
	 * 
	 * @param name
	 * @return
	 */
	public static String getActivityName(String name) {
		String str;
		if (name == null) {
			str = "";
		}
		str = name.substring(name.lastIndexOf(".") + 1, name.length());
		return str;
	}

	/**
	 * 获取本地数据唯一key
	 * 
	 * @param data
	 *            所传参数
	 * @param userid
	 *            用户id
	 * @param httpUrl
	 *            接口路径
	 * @return
	 */
	public static String getKEY(HashMap<String, Object> data, String userid,
			String httpUrl) {
		String key = StringUtilsTab.isEmpty(httpUrl) ? "" : httpUrl;
		StringBuffer sb = new StringBuffer();
		sb.append(key);
		try {
			Set<String> keysSet = data.keySet();
			Iterator<String> iterator = keysSet.iterator();
			while (iterator.hasNext()) {
				Object value = iterator.next();
				if (!value.equals("hash")) {
					sb.append("[" + value.toString() + ":" + data.get(value)
							+ "]");
				}
			}
		} catch (Exception e) {
			ULogger.e(e);
		}
		sb.append(userid);
		return StringUtilsTab.encodeMD5(sb.toString());
	}

	/**
	 * 将String进行MD5编码
	 * 
	 * @param value
	 *            :待转换的流
	 */
	public static String encodeMD5(String value) {
		StringBuffer enStr = new StringBuffer();
		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(value.getBytes());
			byte[] b = algorithm.digest();
			for (int i = 0; i < b.length; i++) {
				String tmp = Integer.toHexString(b[i] & 0xFF);
				if (tmp.length() == 1) {
					enStr.append("0").append(tmp);
				} else {
					enStr.append(tmp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return enStr.toString();

	}

	/**
	 * 获取图片文件缓存名字.
	 * 
	 * 注意:在读写本地图片时最好都采用该方法,避免重复存储,和命名不规范.同时保证文件的有效性
	 * 
	 * @param picUrl
	 *            :图片的完整路径
	 */
	public static String getPicCacheName(String picUrl) {
		StringBuffer buffer = new StringBuffer();
		try {
			buffer.append(FileCenterTab.getInstence().getCacheTempDir());
			buffer.append(StringUtilsTab.encodeMD5(picUrl)).append(".j");
		} catch (Exception e) {
			ULogger.e(e);
		}
		return buffer.toString();
	}

	public static int StrToInteger(String str) {
		int num = -1;
		try {
			num = Integer.parseInt(str);
		} catch (Exception e) {
			return num;
		}
		return num;
	}

	public static double StrToDouble(String str) {
		double d = 0;
		try {
			d = Double.parseDouble(str);
		} catch (Exception e) {

		}
		return d;
	}

	public static float StrToFloat(String str) {
		float d = 0;
		try {
			d = Float.parseFloat(str);
		} catch (Exception e) {

		}
		return d;
	}

	/**
	 * 精确小数点两位
	 * 
	 * @param time
	 * @return
	 */
	public static String getDoubleTwo(String time) {
		if (time.indexOf(".") > 0
				&& (time.substring(time.indexOf(".")).length() > 3)) {

			return time.substring(0, time.indexOf(".") + 3);
		}
		return time;
	}

	/**
	 * 是否是电话号码
	 * 
	 * @param mobile
	 * @return
	 */
	public static boolean isPhoneNumber(String mobile) {
		Pattern p = Pattern.compile("^(13|14|15|18|17)\\d{9}$");
		Matcher m = p.matcher(mobile);
		if (!m.matches()) {
			return false;
		}
		return true;
	}

	@SuppressLint("SimpleDateFormat")
	public static String longToString(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

		java.util.Date dt = new Date();
		String sDateTime = sdf.format(dt); // 得到精确到秒的表示：08/31/2006 21:08:00
		return sDateTime;
	}

	/**
	 * 
	 * @param time
	 * @param simp
	 *            yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String longToString(long time, String simp) {
		SimpleDateFormat sdf = new SimpleDateFormat(simp);

		java.util.Date dt = new Date();
		dt.setTime(time);
		String sDateTime = sdf.format(dt); // 得到精确到秒的表示：08/31/2006 21:08:00
		return sDateTime;
	}

	/**
	 * 将Object对象保存到本地<br>
	 * 一般用于简单对象的保存
	 * 
	 * @param strs
	 *            对象
	 * @param path
	 *            路径
	 */
	public static void SaveObjectToSDcard(Object o, String path) {
		File f = new File(path.substring(0, path.lastIndexOf("/")));
		if (!f.exists()) {
			f.mkdirs();
		}
		FileOutputStream file = null;
		ObjectOutputStream obj = null;
		try {
			file = new FileOutputStream(path);
			obj = new ObjectOutputStream(file);
			obj.writeObject(o);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				obj.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			obj = null;
		}
	}

	/***
	 * 将本地保存的Object取出来
	 * 
	 * @param path
	 *            路径
	 * @return
	 */
	public static Object GetObjectFromSDcard(String path) {
		Object str = null;
		FileInputStream input = null;
		ObjectInputStream obj = null;
		try {
			input = new FileInputStream(path);
			obj = new ObjectInputStream(input);
			str = obj.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (obj != null) {
					obj.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			obj = null;
		}
		return str;
	}

	private static boolean hasFile(String path) {
		File file = new File(path);
		return file.exists();
	}

	/**
	 * 与Input一起使用判断是否有保存数据
	 * 
	 * @param path
	 * @return
	 */
	public static boolean haveFile(String path) {
		if (hasFile(path)) {
			if (GetObjectFromSDcard(path).toString().equals("")) {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}
	
	
	/**
	 * 校验银行卡卡号
	 * 
	 * @param cardId
	 * @return
	 */
	public static boolean checkBankCard(String cardId) {
		char bit = getBankCardCheckCode(cardId
				.substring(0, cardId.length() - 1));
		if (bit == 'N') {
			return false;
		}
		return cardId.charAt(cardId.length() - 1) == bit;
	}

	/**
	 * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
	 * 
	 * @param nonCheckCodeCardId
	 * @return
	 */
	private static char getBankCardCheckCode(String nonCheckCodeCardId) {
		if (nonCheckCodeCardId == null
				|| nonCheckCodeCardId.trim().length() == 0
				|| !nonCheckCodeCardId.matches("\\d+")) {
			// 如果传的不是数据返回N
			return 'N';
		}
		char[] chs = nonCheckCodeCardId.trim().toCharArray();
		int luhmSum = 0;
		for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
			int k = chs[i] - '0';
			if (j % 2 == 0) {
				k *= 2;
				k = k / 10 + k % 10;
			}
			luhmSum += k;
		}
		return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
	}
}
