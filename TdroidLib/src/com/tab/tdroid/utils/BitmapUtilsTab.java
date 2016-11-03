package com.tab.tdroid.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.PorterDuff.Mode;
import android.graphics.Matrix;
import android.graphics.Shader;

/**
 * 图片处理类
 * 
 * @author tab
 * 
 */
public class BitmapUtilsTab {

	/**
	 * 按指定尺寸缩放图片
	 * 
	 * @param bm
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static Bitmap zoomImg(Bitmap bm, String path, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scale = ((float) newWidth) / width;
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		Bitmap newbm = null;
		try {
			newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
			ImageCruCache.getInstance().addBitmapToMemoryCache(path, newbm);
		} catch (Exception e) {
			ULogger.e(e);
			ULogger.e("图片缩放出现错误");
			newbm = bm;
		} catch (OutOfMemoryError e) {
			ULogger.e(e);
			ULogger.e("图片缩放出现错误");
			newbm = bm;
		}
		return newbm;
	}

	/**
	 * 获取本地图片
	 * 
	 * @param path
	 * @param options
	 * @param width
	 *            指定宽度
	 * @return
	 */
	public static Bitmap getBitmapFromLocation(String path, Options options,
			int width) {
		Bitmap tempBitmap = null;
		try {
			if (options == null) {
				options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.RGB_565;
			}

			// TODO 先按width计算inSampleSize 然后取到小点的图片之后再进行固定尺寸缩放
			options.inJustDecodeBounds = true; // 设置只是解码图片的边距，此操作目的是度量图片的实际宽度和高度
			BitmapFactory.decodeFile(path, options);
			int widthold = options.outWidth;
			options.inSampleSize = width == 0 ? 1 : Math
					.round(widthold / width);
			options.inJustDecodeBounds = false;
			tempBitmap = BitmapFactory.decodeFile(path, options);

			if (width != 0) {
				tempBitmap = zoomImg(tempBitmap, path, width);
			} else {
				ImageCruCache.getInstance().addBitmapToMemoryCache(path,
						tempBitmap);
			}

		} catch (Exception e) {
			ULogger.e(e);
			ULogger.e("图片缩放出现错误");
		} catch (OutOfMemoryError e) {
			ULogger.e(e);
			ULogger.e("图片缩放出现错误");
		}

		return tempBitmap;

	}

	/**
	 * 根据宽度获取图片的inSampliSize
	 * 
	 * @param options
	 * @param reqWidth
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth) {
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (width > reqWidth) {
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = widthRatio;
		}
		return inSampleSize;
	}

	/**
	 * 根据宽度获取按比例缩小或者放大的图片
	 * 
	 * @param pathName
	 * @param reqWidth
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(String pathName,
			int reqWidth) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(pathName, options);
	}

	/**
	 * 转换图片成圆形 没有白边
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(String path, Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		ImageCruCache.getInstance().addBitmapToMemoryCache(path, output);
		return output;
	}

	/**
	 * 将图片准转为圆形
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(String path, Bitmap bitmap) {

		int size = bitmap.getWidth() < bitmap.getHeight() ? bitmap.getWidth()
				: bitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(size, size, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, size, size);
		final float roundPx = size / 2;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		ImageCruCache.getInstance().addBitmapToMemoryCache(path, output);
		return getRoundeBitmapWithWhite(path, output);
	}

	/**
	 * 获取白色边框
	 * 
	 * @param path
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getRoundeBitmapWithWhite(String path, Bitmap bitmap) {
		int size = bitmap.getWidth() < bitmap.getHeight() ? bitmap.getWidth()
				: bitmap.getHeight();
		int num = 14;
		int sizebig = size + num;
		Bitmap output = Bitmap.createBitmap(sizebig, sizebig, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = Color.parseColor("#FFFFFF");
		final Paint paint = new Paint();
		final float roundPx = sizebig / 2;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawBitmap(bitmap, num / 2, num / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_ATOP));

		RadialGradient gradient = new RadialGradient(roundPx, roundPx, roundPx,
				new int[] { Color.WHITE, Color.WHITE,
						Color.parseColor("#AAAAAAAA") }, new float[] { 0.f,
						0.97f, 1.0f }, Shader.TileMode.CLAMP);
		paint.setShader(gradient);
		canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		ImageCruCache.getInstance().addBitmapToMemoryCache(path, output);
		return output;
	}

}
