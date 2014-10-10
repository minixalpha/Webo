package com.minixalpha.control;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sina.weibo.sdk.net.RequestListener;

import android.app.Activity;

public interface ViewWeiboHelper {
	public Activity getViewActivity();

	public PullToRefreshListView getTimeLineView();

	public ViewWeiboHelper getInstance();

	public void getTimeline(long since_id, long max_id, int count, int page,
			boolean base_app, int featureType, boolean trim_user,
			RequestListener listener);

	public String getCache();
	
	public void actionAfterUpdate();
}
