package com.tab.tdroid.utils;

import com.tab.tdroid.Tdroid;
import com.tab.tdroid.net.HttpCore;
import com.tab.tdroid.net.HttpCore.onProgressLinstener;
import com.tab.tdroid.net.HttpRsq;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
 * 图片加载类
 * 
 * @author tab
 * 
 */
public class ImageLoader {

	protected static ImageView imageview;
	protected static Context mContext;
	protected static String urlStr;
	protected static ImageOptions options;
	protected static int position;
	protected static int imgWidth = 0;
	protected static boolean isHead = false;
	protected static onProgressLinstener listener;

	/**
	 * 屏幕宽度1/5
	 */
	public static int TYPE_SMALL = 1000;
	/**
	 * 屏幕宽度1/3
	 */
	public static int TYPE_MIDDLE = 1001;
	/**
	 * 整个屏幕宽度
	 */
	public static int TYPE_BIG = 1002;

	/**
	 * 全部参数
	 * 
	 * @param context
	 * @param imageView
	 * @param path
	 *            图片路径
	 * @param options
	 *            {@link ImageOptions}
	 * @param isLocation
	 *            是否强制从本地加载 true时不进行路径编码
	 * @param position
	 *            list调用解决闪烁 非list设置-1
	 * @param isHead
	 *            是否是头像
	 * @param sizeType
	 *            {@link ImageLoader.TYPE_SMALL
	 *            ,ImageLoader.TYPE_MIDDLE,ImageLoader.TYPE_BIG} 或者单独设置宽度
	 * @param dontCruche
	 *            不从缓存读取图片
	 */
	public static void loadImage(final Context context,
			final ImageView imageView, final String path, ImageOptions options,
			boolean isLocation, int position, boolean isHead, int sizeType,
			boolean dontCruche, onProgressLinstener listener) {
		loadImageTask(context, imageView, path, options, isLocation, position,
				isHead, sizeType, dontCruche, listener);
	}

	/**
	 * 直接从网络加载图片
	 * 
	 * @param context
	 * @param imageview
	 * @param path
	 * @param sizeType
	 * @param options
	 * @param listener
	 */
	public static void loadImageFromNet(Context context, ImageView imageview,
			String path, int sizeType, ImageOptions options,
			onProgressLinstener listener) {
		ImageLoader.imageview = imageview;
		ImageLoader.mContext = context;
		ImageLoader.imgWidth = sizeType;
		ImageLoader.options = options;
		ImageLoader.listener = listener;
		setSizeType(sizeType);
		if (setemptyUrl(imageview, urlStr)) {
			return;
		}
		loadImageOnNet(path);
	}

	/**
	 * list里面加载头像
	 * 
	 * @param context
	 * @param imageview
	 * @param path
	 * @param type
	 * @param position
	 */
	public static void loadImageToHead(Context context, ImageView imageview,
			String path, int type, int position) {
		loadImageTask(mContext, imageview, path, null, false, position, true,
				type, false, null);
	}

	/**
	 * 加载头像
	 * 
	 * @param context
	 * @param imageview
	 * @param path
	 * @param type
	 */
	public static void loadImageToHead(Context context, ImageView imageview,
			String path, int type) {
		loadImageTask(mContext, imageview, path, null, false, -1, true, type,
				false, null);
	}

	/**
	 * list加载本地图片
	 * 
	 * @param context
	 * @param imageview
	 * @param path
	 * @param type
	 * @param position
	 */
	public static void loadImageOnLocation(Context context,
			ImageView imageview, String path, int type, int position) {
		loadImageTask(mContext, imageview, path, null, true, position, false,
				type, false, null);
	}

	/**
	 * 加载本地图片
	 * 
	 * @param context
	 * @param imageview
	 * @param path
	 * @param type
	 */
	public static void loadImageOnLocation(Context context,
			ImageView imageview, String path, int type) {
		loadImageTask(mContext, imageview, path, null, true, -1, false, type,
				false, null);
	}

	/**
	 * 根据路径加载图片
	 * 
	 * @param context
	 * @param imageview
	 * @param path
	 */
	public static void loadImage(Context context, ImageView imageview,
			String path) {
		loadImageTask(context, imageview, path, null, false, -1, false, 0,
				false, null);
	}

	/**
	 * 加载图片
	 * 
	 * @param context
	 * @param imageview
	 * @param path
	 * @param type
	 */
	public static void loadImage(Context context, ImageView imageview,
			String path, int type) {
		loadImageTask(context, imageview, path, null, false, -1, false, type,
				false, null);
	}

	/**
	 * 直接加载缓存图片
	 * 
	 * @param imageView
	 *            imageView
	 * @param key
	 *            缓存图片key
	 * @return
	 */
	protected static boolean loadImageCache(String key) {
		Bitmap bitmap = ImageCruCache.getInstance().getBitmapFromMemoryCache(
				key);
		if (bitmap != null) {
			setImageViewImgLast(bitmap, imageview);
		}
		return bitmap != null ? true : false;
	}

	/**
	 * 加载网络图片
	 */
	protected static void loadImageOnNet(String path) {
		startDownLoad(path);
	}

	/**
	 * 加载SDcard图片
	 * 
	 * @param path
	 * @return 加载完成true or false
	 */
	protected static boolean loadImageOnLocation(String path, int width) {

		return setImageViewImgNothing(BitmapUtilsTab.getBitmapFromLocation(
				path, options == null ? null : options.getDecodingOptions(),
				width), imageview);
	}

	/**
	 * 加载本地图片 end
	 * 
	 * @param path
	 * @param width
	 */
	protected static void loadImageOnLocationRes(String path, int width) {
		setImageViewImgLast(BitmapUtilsTab.getBitmapFromLocation(path,
				options == null ? null : options.getDecodingOptions(), width),
				imageview);
	}

	/**
	 * 加载图片Task
	 * 
	 * @param mContext
	 * @param imageView
	 * @param urlStr
	 *            图片路径
	 * @param options
	 *            图片配置类
	 * @param isLocation
	 *            是否是本地图片
	 * @param position
	 *            在listview里adapter的position 解决图片乱闪的问题
	 * @param isHead
	 *            是否是头像 头像进行圆角处理
	 * @param sizeType
	 *            图片的大小
	 * @param dontCrucache
	 *            强制不从缓存加载
	 */
	protected static void loadImageTask(final Context mContext,
			final ImageView imageView, final String urlStr,
			ImageOptions options, boolean isLocation, int position,
			boolean isHead, int sizeType, boolean dontCrucache,
			onProgressLinstener listener) {
		ImageLoader.imageview = imageView;
		ImageLoader.mContext = mContext;
		ImageLoader.options = options;
		ImageLoader.position = position;
		ImageLoader.imgWidth = sizeType;
		ImageLoader.isHead = isHead;
		String httpUrl = urlStr;
		ImageLoader.listener = listener;
		setSizeType(sizeType);
		if (setemptyUrl(imageView, urlStr)) {
			return;
		}
		if (!isLocation) {
			ImageLoader.urlStr = StringUtilsTab.getPicCacheName(urlStr);
		}
		if (position != -1) {
			ImageLoader.imageview.setTag(position + ImageLoader.urlStr);
		}
		if (ImageLoader.options != null
				&& ImageLoader.options.shouldShowImageLodingRes()) {
			setImageViewImg(ImageLoader.options.getShowImageLodingRes(),
					imageView);
		}
		if (isLocation) {
			loadImageOnLocationRes(ImageLoader.urlStr, imgWidth);
			return;
		}

		if (!dontCrucache) {
			if (loadImageCache(ImageLoader.urlStr)) {
				return;
			}
		}
		if (loadImageOnLocation(ImageLoader.urlStr, imgWidth)) {
			return;
		}

		loadImageOnNet(httpUrl);
	}

	protected static void setSizeType(int sizeType) {
		if (sizeType == TYPE_SMALL) {
			ImageLoader.imgWidth = KScreen.screenSize.x / 5;
		} else if (sizeType == TYPE_MIDDLE) {
			ImageLoader.imgWidth = KScreen.screenSize.x / 3;
		} else if (sizeType == TYPE_BIG) {
			ImageLoader.imgWidth = KScreen.screenSize.x;
		}
	}

	protected static boolean setemptyUrl(ImageView imageview, String url) {
		boolean isempty = false;
		if (StringUtilsTab.isEmpty(url)) {
			if (ImageLoader.options != null
					&& ImageLoader.options.shouldShowImageEmptyRes()) {
				setImageViewImg(ImageLoader.options.getShowImageEmptyRes(),
						imageview);
			}
			isempty = true;
		}
		return isempty;
	}

	/**
	 * 下载图片
	 */
	protected static void startDownLoad(String path) {
		try {
			Tdroid.service.execute(new downLoadImgRunnable(path));
		} catch (Exception e) {
			ULogger.d("线程太多，不能接受执行");
			ULogger.e(e);
		}
	}

	protected static void onDistory() {
		ULogger.d("onDistory");
		imageview = null;
		mContext = null;
		urlStr = null;
		options = null;
		listener = null;
		mContext = null;
		mHandler = null;

	}

	/**
	 * 设置图片，需要判断tag
	 * 
	 * @param bitmap
	 * @param imageView
	 */
	protected static void setImageViewImgLast(Bitmap bitmap, ImageView imageView) {
		if (bitmap == null) {
			if (ImageLoader.options != null && options.shouldShowImageFailRes()) {
				imageView.setImageResource(options.getShowImageFailRes());
			}
			return;
		}
		ULogger.d("" + bitmap.getWidth());
		if (position > -1) {
			Object tag = imageView.getTag();
			if (tag != null && tag.equals(position + urlStr)) {
				setImageViewImg(bitmap, imageView);
			} else {
				onDistory();
			}
			return;
		}
		setImageViewImg(bitmap, imageView);

	}

	/**
	 * 设置图片
	 * 
	 * @param bitmap
	 * @param imageView
	 */
	private static void setImageViewImg(Bitmap bitmap, ImageView imageView) {
		if (ImageLoader.isHead) {
			imageView.setImageBitmap(BitmapUtilsTab.getRoundedCornerBitmap(
					urlStr, bitmap));
		} else {
			imageView.setImageBitmap(bitmap);
		}
		onDistory();
	}

	/**
	 * 设置图片 没有就返回false 不对imageview进行任何操作
	 * 
	 * @param bitmap
	 * @param imageView
	 * @return
	 */
	protected static boolean setImageViewImgNothing(Bitmap bitmap,
			ImageView imageView) {
		boolean result = false;
		if (bitmap == null) {
			return result;
		}
		result = true;
		setImageViewImg(bitmap, imageView);
		return result;
	}

	/**
	 * 设置图片
	 * 
	 * @param Res
	 * @param imageView
	 */
	protected static void setImageViewImg(int Res, ImageView imageView) {
		imageView.setImageResource(Res);
	}

	protected static Handler mHandler = new Handler(Tdroid.app.getMainLooper()) {
		@Override
		public synchronized void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// 图片下载完成
				try {
					HttpRsq rsq = (HttpRsq) msg.obj;
					if (rsq.result) {
						// loadImage(mContext, imageview, urlStr);
						loadImageOnLocationRes(rsq.data.toString(),
								ImageLoader.imgWidth);
					} else {
						ULogger.e("图片下载失败:" + rsq.toString());
					}
				} catch (Exception e) {
					ULogger.e(e);
				}
				break;
			case 2:
				if (listener != null) {
					listener.progress(msg.arg1);
				}
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 将异步线程改为线程方式 使用线程池管理
	 * 
	 * @author win
	 * 
	 */
	private static class downLoadImgRunnable implements Runnable {

		private String path;

		public downLoadImgRunnable(String path) {
			this.path = path;
		}

		@Override
		public void run() {
			HttpCore core = new HttpCore(path, null);
			core.setOnProgressLinstener(new onProgressLinstener() {
				@Override
				public void progress(int progress) {
					Message msg = new Message();
					msg.what = 2;
					msg.arg1 = progress;
					sendMsg(msg);
				}
			});

			Message msg = new Message();
			msg.what = 1;
			msg.obj = core.downLoad();
			sendMsg(msg);

			Message msgp = new Message();
			msgp.what = 2;
			msgp.arg1 = 100;
			sendMsg(msgp);

		}

		private void sendMsg(Message msg) {
			mHandler.sendMessage(msg);
		}

	}

}
