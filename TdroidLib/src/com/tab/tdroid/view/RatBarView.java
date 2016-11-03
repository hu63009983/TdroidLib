package com.tab.tdroid.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

/**
 * 评分
 * 
 * @author admin
 * 
 */
public class RatBarView extends LinearLayout {

	private int numStars = 5;
	private List<ImageView> childList;
	private int currentBar = 0;
	private Context mContext;
	private boolean isClick = true;
	private IStartChange onChangeLintener;
	private int size = 80;

	private int resource = 0;

	public interface IStartChange {
		void getCurrentChangge(int current, View v);
	}

	/**
	 * 
	 * @param listener
	 */
	public void setOnChangeStartLinstener(IStartChange listener) {
		onChangeLintener = listener;
	}

	/**
	 * 设置是否能点击
	 * 
	 * @param click
	 */
	public void setIsClick(boolean click) {
		isClick = click;
	}

	public void setSelectAll() {
		for (int i = 0; i < childList.size(); i++) {
			setImagesSelect(i, false);
		}
	}

	/**
	 * 设置星星数量
	 * 
	 * @param num
	 */
	public void setNumStars(int num) {
		numStars = num;
		childList.clear();
		currentBar = 0;
		this.removeAllViews();
		for (int i = 0; i < num; i++) {
			AddStartImageView(i);
		}
	}

	/**
	 * 获取星星下标
	 * 
	 */
	public int getCurrentIndex() {
		return currentBar;
	}

	/**
	 * 获取当前星点
	 * 
	 */
	public int getCurrentBar() {
		return (currentBar + 1) * 20;
	}

	/**
	 * 设置选中哪个星星
	 * 
	 * @param index
	 *            1开始
	 */
	public void setSelectIndex(int index) {
		if (index >= 20) {
			index = index / 20;
		}

		if (index > 5) {
			index = 5;
		}
		if (index >= 0 && index <= numStars) {
			setImagesSelect(index - 1, false);
		}

	}

	public RatBarView(Context context) {
		super(context);
		iniLayout(context);
	}

	public RatBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		iniLayout(context);
	}

	@SuppressLint("NewApi")
	public RatBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		iniLayout(context);
	}

	private void iniLayout(Context context) {
		mContext = context;
		childList = new ArrayList<ImageView>();
		this.setOrientation(LinearLayout.HORIZONTAL);
		for (int i = 0; i < numStars; i++) {
			AddStartImageView(i);
		}
	}

	public void setStarSize(int size) {
		this.size = size;
	}

	public void setStartRes(int res) {
		this.resource = res;
	}

	private void AddStartImageView(final int i) {
		final ImageView imageView = new ImageView(mContext);
		imageView.setLayoutParams(new LayoutParams(size, size));
		imageView.setScaleType(ScaleType.CENTER_INSIDE);
		if (resource != 0) {
			imageView.setImageResource(resource);
		}
		imageView.setTag(i);

		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isClick && i != 0) {
					String tag = v.getTag().toString();
					setImagesSelect(Integer.parseInt(tag), v.isSelected());
				} else if (i == 0) {
					String tag = v.getTag().toString();
					setImagesSelect(Integer.parseInt(tag), false);
				}

			}
		});
		childList.add(imageView);
		if (i == 0) {
			setImagesSelect(0, false);
		}
		this.addView(imageView);
	}

	private void setImagesSelect(int index, boolean isselect) {
		if (index != -1) {
			if (index > currentBar || index < currentBar) {
				for (int i = 0; i < childList.size(); i++) {
					if (i <= index) {
						childList.get(i).setSelected(true);
					} else {
						childList.get(i).setSelected(false);
					}
					currentBar = index;
				}
			} else {

				childList.get(index).setSelected(!isselect);
				if (isselect) {
					currentBar = index - 1;
				} else {
					currentBar = index;
				}
			}

			if (onChangeLintener != null) {
				onChangeLintener.getCurrentChangge(getCurrentBar(), this);
			}
		} else {
			for (int i = 0; i < childList.size(); i++) {

				childList.get(i).setSelected(false);

				currentBar = 0;
			}
		}
	}

}
