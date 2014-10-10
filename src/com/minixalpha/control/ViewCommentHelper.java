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
	
}
