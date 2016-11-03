package com.tab.tdroid.view;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class TabPagerAdapter extends PagerAdapter {

	private List<View> views;

	public TabPagerAdapter() {
		this.views = new ArrayList<View>();
	}

	public void setViews(List<View> views) {
		this.views.clear();
		this.views.addAll(views);
	}

	public void clearAll() {
		views.clear();
	}

	public void clearViews() {
		this.views.clear();
	}

	public void addView(View v) {
		this.views.add(v);
	}

	public View getView(int index) {
		return views.get(index);
	}

	public int getViewsSize() {
		return this.views.size();
	}

	public View getViewChild(int i) {
		try {
			return views.get(i);
		} catch (Exception e) {
			// ULogger.e(e);
		}
		return null;

	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(views.get(arg1), 0);
		return views.get(arg1);
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView(views.get(position));
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

}
