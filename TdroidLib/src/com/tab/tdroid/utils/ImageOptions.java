package com.tab.tdroid.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;

/**
 * 图片配置类
 * 
 * @author tab
 * 
 */
public class ImageOptions {
	private final Options decodingOptions;
	private final int ImageFailRes;
	private final int ImageLodingRes;
	private final int ImageForEmptyRes;

	public ImageOptions(Builder builder) {
		decodingOptions = builder.decodingOptions;
		ImageLodingRes = builder.ImageLodingRes;
		ImageFailRes = builder.ImageFailRes;
		ImageForEmptyRes = builder.ImageForEmptyRes;
	}

	public boolean shouldShowImageLodingRes() {
		return ImageLodingRes != 0;
	}

	public int getShowImageLodingRes() {
		return ImageLodingRes;
	}

	public boolean shouldShowImageEmptyRes() {
		return ImageForEmptyRes != 0;
	}

	public int getShowImageEmptyRes() {
		return ImageForEmptyRes;
	}

	public boolean shouldShowImageFailRes() {
		return ImageFailRes != 0;
	}

	public int getShowImageFailRes() {
		return ImageFailRes;
	}

	public Options getDecodingOptions() {
		return decodingOptions;
	}

	public static class Builder {
		private Options decodingOptions = new Options();
		private int ImageFailRes = 0;
		private int ImageLodingRes = 0;
		private int ImageForEmptyRes = 0;

		/**
		 * Sets {@link Bitmap.Config bitmap config} for image decoding. Default
		 * value - {@link Bitmap.Config#ARGB_8888}
		 */
		public Builder bitmapConfig(Bitmap.Config bitmapConfig) {
			if (bitmapConfig != null)
				decodingOptions.inPreferredConfig = bitmapConfig;
			return this;
		}

		public Builder showImageForEmptyUri(int res) {
			ImageForEmptyRes = res;
			return this;
		}

		public Builder showImageLoadingRes(int res) {
			ImageLodingRes = res;
			return this;
		}

		public Builder showImageFail(int res) {
			ImageFailRes = res;
			return this;
		}

		public ImageOptions buid() {
			return new ImageOptions(this);
		}
	}
}
