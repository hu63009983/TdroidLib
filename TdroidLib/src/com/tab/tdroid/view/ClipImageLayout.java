package com.tab.tdroid.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

/**
 * http://blog.csdn.net/lmj623565791/article/details/39761281
 * 
 * @author zhy
 * 
 */
public class ClipImageLayout extends RelativeLayout {

	private ClipZoomImageView mZoomImageView;
	private ClipImageBorderView mClipImageView;

	public ClipImageLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		mZoomImageView = new ClipZoomImageView(context);
		mClipImageView = new ClipImageBorderView(context);

		android.view.ViewGroup.LayoutParams lp = new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		this.addView(mZoomImageView, lp);
		this.addView(mClipImageView, lp);
	}

	/**
	 * 对外公布的方法
	 * 
	 * @param mHorizontalPadding
	 *            ,单位为dp
	 * @param DrawableId
	 *            ,资源图片
	 */
	@SuppressWarnings("deprecation")
	public void init(int mHorizontalPadding, int drawableId) {
		mZoomImageView.setImageDrawable(getResources().getDrawable(drawableId));
		setHorizontalPadding(mHorizontalPadding);
	}

	public void init(int mHorizontalPadding, Uri uri) {
		mZoomImageView.setImageURI(uri);
		setHorizontalPadding(mHorizontalPadding);
	}

	public void init(int mHorizontalPadding, Bitmap bitmap) {
		mZoomImageView.setImageBitmap(bitmap);
		setHorizontalPadding(mHorizontalPadding);
	}

	private void setHorizontalPadding(int mHorizontalPadding) {

		if (mHorizontalPadding != 0) {
			// 计算padding的px
			mHorizontalPadding = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding,
					getResources().getDisplayMetrics());
		}

		mZoomImageView.setHorizontalPadding(mHorizontalPadding);
		mClipImageView.setHorizontalPadding(mHorizontalPadding);
	}

	/**
	 * 裁切图片
	 * 
	 * @return
	 */
	public Bitmap clip() {
		return mZoomImageView.clip();
	}

}
