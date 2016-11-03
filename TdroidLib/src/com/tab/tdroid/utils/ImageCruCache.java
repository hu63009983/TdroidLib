package com.tab.tdroid.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * 图片内存管理
 * 
 * @author Tab_tang
 */
@SuppressLint("NewApi")
public class ImageCruCache {

	private static LruCache<String, Bitmap> mMemoryCache;

	private static ImageCruCache mImageLoader;

	public static ImageCruCache getInstance() {
		if (mImageLoader == null) {
			mImageLoader = new ImageCruCache();
		}
		return mImageLoader;
	}

	private ImageCruCache() {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 10;
		try {
			mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					return bitmap.getByteCount();
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemoryCache(key) == null) {
			try {
				mMemoryCache.put(key, bitmap);
			} catch (Exception e) {
			} catch (OutOfMemoryError e) {
			}
		}
	}

	public Bitmap getBitmapFromMemoryCache(String key) {
		return mMemoryCache.get(key);
	}

}
