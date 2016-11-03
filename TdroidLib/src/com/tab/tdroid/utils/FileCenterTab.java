package com.tab.tdroid.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class FileCenterTab {

	private static FileCenterTab instence;
	/**
	 * 获取文件大小单位为B的double值
	 */
	public static final int SIZETYPE_B = 1;

	/**
	 * 获取文件大小单位为KB的double值
	 */
	public static final int SIZETYPE_KB = 2;

	/**
	 * 获取文件大小单位为MB的double值
	 */
	public static final int SIZETYPE_MB = 3;

	/**
	 * 获取文件大小单位为GB的double值
	 */
	public static final int SIZETYPE_GB = 4;

	private boolean stop; // 是否停止写入

	private static String CacheTempDir = "com/tdroid/cache/";

	private FileCenterTab() {
		createCacheTempDir();
	}

	public static FileCenterTab getInstence() {
		if (instence == null) {
			synchronized (FileCenterTab.class) {
				if (instence == null) {
					instence = new FileCenterTab();
				}
			}
		}
		return instence;
	}

	/**
	 * 得到SD卡根目录
	 * 
	 * @return
	 */
	public String getSDRoot() {
		return Environment.getExternalStorageDirectory() + "/";
	}

	/**
	 * 设置缓存目录路径 <br>
	 * 默认：com/tdroid/cache/
	 * 
	 * @param path
	 */
	public void setCacheTempDir(String path) {

		CacheTempDir = path;
	}

	/**
	 * 获取缓存目录路径<br>
	 * 默认：com/tdroid/cache/
	 * 
	 * @return
	 */
	public String getCacheTempDir() {

		return getSDRoot() + CacheTempDir;
	}

	public void createCacheTempDir() {
		// create(getCacheTempDir(), true);
		createDir(getCacheTempDir());
	}

	// 创建文件夹 存在则不创建
	private void createDir(String dirName) {
		File file = new File(dirName);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 创建文件或文件夹 全地址 存在则不创建
	 * 
	 * @param fileName
	 * @param isDir
	 *            是否是文件
	 * @return
	 */
	public File create(String fileName, boolean isDir) {
		File file = null;
		String[] fileLink = fileName.split("/");
		String path = "";
		int count = 0;
		if (isDir) {
			count = fileLink.length;
		} else {
			count = fileLink.length - 1;
		}
		for (int i = 0; i < count; i++) {
			path = path.concat(fileLink[i]).concat("/");
			createDir(path);
		}
		if (!isDir)
			file = createFile(fileName);
		return file;
	}

	// 创建文件或文件夹 全地址 存在先删除再创建
	public File createAbs(String fileName, boolean isDir) {
		delete(fileName);
		return create(fileName, isDir);
	}

	private static File createFile(String fileName, boolean isDel) {
		File file = null;
		file = new File(fileName);
		if (file.exists()) {
			file.setLastModified(new Date().getTime());
			if (isDel) {
				file.delete();
			}
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
		}
		return file;
	}

	// 创建文件 存在则不创建
	private static File createFile(String fileName) {
		File file = null;
		try {
			file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			} else {
				file.setLastModified(new Date().getTime());
			}
		} catch (Exception e) {
			ULogger.e(e);
		}
		return file;
	}

	// 删除文件
	public boolean delete(String fileName) {
		File file = new File(fileName);
		// 文件或文件夹是否存在
		if (!file.exists()) {
			return false;
		}
		// 是否为文件
		if (file.isFile()) {
			file.delete();
			return true;
		}
		// 是否为文件夹
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return true;
			}
			for (File f : childFile) {
				delete(f.getAbsolutePath());
			}
			file.delete();
		}
		return false;
	}

	// 写入
	public File wrtie(String fileName, InputStream input) {
		File file = null;
		if (input == null) {
			return file;
		}
		OutputStream output = null;
		try {
			// 在SD卡上创建
			file = createAbs(fileName, false);
			// 得到一个文件对象
			output = new FileOutputStream(file);
			// 定义每次读取和写入数据的大小
			byte buffer[] = new byte[4 * 1024];
			int numread;
			// 循环读取数据，并写入到文件中
			while (!stop && (numread = input.read(buffer)) != -1) {
				output.write(buffer, 0, numread);
				// writeSize += numread;// 更新完成数
			}
			if (stop) {
				delete(fileName);
			}
			// 释放output对象
			output.flush();
		} catch (Exception e) {
			ULogger.e(e.toString());
		} finally {
			try {
				input.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				// 关闭文件写入对象
				if (output != null)
					output.close();
			} catch (IOException e) {
				ULogger.e(e.toString());
			}
		}
		return file;
	}

	/**
	 * 
	 * 获取文件指定文件的指定单位的大小
	 * 
	 * @param filePath
	 *            文件路径
	 * 
	 * @param sizeType
	 *            获取大小的类型1为B、2为KB、3为MB、4为GB
	 * 
	 * @return double值的大小
	 */

	public double getFileOrFilesSize(String filePath, int sizeType) {

		File file = new File(filePath);

		long blockSize = 0;

		try {

			if (file.isDirectory()) {

				blockSize = getFileSizes(file);

			} else {

				blockSize = getFileSize(file);

			}

		} catch (Exception e) {

			e.printStackTrace();

			ULogger.e("获取文件大小  获取失败!");

		}

		return FormetFileSize(blockSize, sizeType);

	}

	/**
	 * 
	 * 调用此方法自动计算指定文件或指定文件夹的大小
	 * 
	 * @param filePath
	 *            文件路径
	 * 
	 * @return 计算好的带B、KB、MB、GB的字符串
	 */

	public String getAutoFileOrFilesSize(String filePath) {

		File file = new File(filePath);

		long blockSize = 0;

		try {

			if (file.isDirectory()) {

				blockSize = getFileSizes(file);

			} else {

				blockSize = getFileSize(file);

			}

		} catch (Exception e) {

			e.printStackTrace();

			Log.e("获取文件大小", "获取失败!");

		}

		return FormetFileSize(blockSize);

	}

	/**
	 * 
	 * 获取指定文件大小
	 * 
	 * @param file
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */

	@SuppressWarnings("resource")
	private long getFileSize(File file) throws Exception

	{

		long size = 0;

		if (file.exists()) {

			FileInputStream fis = null;

			fis = new FileInputStream(file);

			size = fis.available();

		}

		else {

			// file.createNewFile();

			Log.e("获取文件大小", "文件不存在!");

		}

		return size;

	}

	/**
	 * 
	 * 获取指定文件夹
	 * 
	 * @param f
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */

	private long getFileSizes(File f) throws Exception

	{

		long size = 0;

		File flist[] = f.listFiles();

		for (int i = 0; i < flist.length; i++) {

			if (flist[i].isDirectory()) {

				try {
					size = size + getFileSizes(flist[i]);
				} catch (Exception e) {
					ULogger.e(e);
				}

			}

			else {

				try {
					size = size + getFileSize(flist[i]);
				} catch (Exception e) {
					ULogger.e(e);
				}

			}

		}

		return size;

	}

	/**
	 * 得到File
	 * 
	 * @param filePath
	 *            文 件路径
	 * @return
	 */
	public File getFilePath(String filePath) {
		File file = null;
		String path = filePath.substring(0, filePath.lastIndexOf("/"));
		String name = filePath.substring(filePath.lastIndexOf("/") + 1);
		try {
			file = new File(path, name);
		} catch (Exception e) {
			ULogger.e(e);
		}
		return file;
	}

	/**
	 * 创建并获取文件对象
	 * 
	 * @param filePath
	 * @return
	 */
	public File getFileByPath(String filePath) {

		return createFile(filePath,true);

	}

	/**
	 * 
	 * 转换文件大小
	 * 
	 * @param fileS
	 * 
	 * @return
	 */

	private String FormetFileSize(long fileS)

	{
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		String wrongSize = "0B";
		if (fileS == 0) {
			return wrongSize;
		}

		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		}

		else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		}

		else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "GB";
		}
		return fileSizeString;

	}

	/**
	 * 
	 * 转换文件大小,指定转换的类型
	 * 
	 * @param fileS
	 * 
	 * @param sizeType
	 * 
	 * @return
	 */

	private double FormetFileSize(long fileS, int sizeType)

	{
		DecimalFormat df = new DecimalFormat("#.00");
		double fileSizeLong = 0;
		switch (sizeType) {
		case SIZETYPE_B:
			fileSizeLong = Double.valueOf(df.format((double) fileS));
			break;
		case SIZETYPE_KB:
			fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
			break;
		case SIZETYPE_MB:
			fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
			break;
		case SIZETYPE_GB:
			fileSizeLong = Double.valueOf(df
					.format((double) fileS / 1073741824));
			break;
		default:
			break;
		}
		return fileSizeLong;
	}

	/**
	 * 获取系统存储路径
	 * 
	 * @return
	 */
	public String getRootDirectoryPath() {
		return Environment.getRootDirectory().getAbsolutePath();
	}
}
