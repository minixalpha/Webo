package com.minixalpha.control;

import android.app.Activity;
import android.view.View;

import com.sina.weibo.sdk.net.RequestListener;

public interface ViewCommentHelper {

	public void requestComment(RequestListener listener);

	/**
	 * 获取 comment listview 所在的 activity
	 * 
	 * @return
	 */
	public Activity getActivity();

	public String getCache();

	public void updateCache(String response);

	/**
	 * 请求的内容已经返回，此时可以关闭进度条
	 */
	public void onRequestComplete();
	
	/**
	 * 请求之前的动作
	 */
	public void beforeRequest();

}
