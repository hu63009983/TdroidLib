package com.tab.tdroid.utils;

import android.os.CountDownTimer;

/**
 * 倒计时类
 * 
 * @author tab
 * 
 */
public class TimerDownTab extends CountDownTimer {
	private TimerDownListener listener;

	public interface TimerDownListener {
		void onTick(long millis);

		void onFinish();
	}

	public void setOnTimerDownListener(TimerDownListener listener) {
		this.listener = listener;
	}

	public TimerDownTab(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
	}

	@Override
	public void onTick(long millisUntilFinished) {
		if (listener != null) {
			listener.onTick(millisUntilFinished);
		}
	}

	@Override
	public void onFinish() {
		if (listener != null) {
			listener.onFinish();
		}
	}

}
