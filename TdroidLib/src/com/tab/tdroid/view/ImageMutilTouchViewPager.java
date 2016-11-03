package com.tab.tdroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

//这个类主要是解决图片浏览器多点快速放大缩小时，出现java.lang.IllegalArgumentException: pointerIndex out of range的问题

public class ImageMutilTouchViewPager extends
		android.support.v4.view.ViewPager {

	public ImageMutilTouchViewPager(Context context) {
		super(context);
	}

	public ImageMutilTouchViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		try {
			return super.onTouchEvent(ev);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return super.onInterceptTouchEvent(ev);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
