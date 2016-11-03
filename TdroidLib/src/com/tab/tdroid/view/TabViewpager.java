package com.tab.tdroid.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class TabViewpager extends ViewPager {
    private boolean scrollble = true;

    public TabViewpager(Context context) {
	super(context);
    }

    public TabViewpager(Context context, AttributeSet attrs) {
	super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
	if (this.scrollble)
	    try {
		return super.onInterceptTouchEvent(ev);
	    } catch (IllegalArgumentException ex) {
		ex.printStackTrace();
		return false;
	    }
 
	else
	    return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
	if (this.scrollble)
	    try {
		return super.onTouchEvent(ev);
	    } catch (IllegalArgumentException ex) {
		ex.printStackTrace();
		return false;
	    }
	else
	    return false;
    }

    public boolean isScrollble() {
	return scrollble;
    }

    /**
     * 设置是否可手动滑动
     * 
     * @param scrollble
     */
    public void setScrollble(boolean scrollble) {
	this.scrollble = scrollble;
    }

}
