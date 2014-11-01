package com.minixalpha.control;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sina.weibo.sdk.net.RequestListener;

import android.app.Activity;

public interface ViewWeiboHelper {
	public Activity getViewActivity();

	public PullToRefreshListView getTimeLineView();

	public ViewWeiboHelper getInstance();

	/**
	 * 获取当前登录用户及其所关注用户的最新微博。
	 * 
	 * @param since_id
	 *            若指定此参数，则返回ID比since_id大的微博（即比since_id时间晚的微博），默认为0
	 * @param max_id
	 *            若指定此参数，则返回ID小于或等于max_id的微博，默认为0。
	 * @param count
	 *            单页返回的记录条数，默认为50。
	 * @param page
	 *            返回结果的页码，默认为1。
	 * @param base_app
	 *            是否只获取当前应用的数据。false为否（所有数据），true为是（仅当前应用），默认为false。
	 * @param featureType
	 *            过滤类型ID，0：全部、1：原创、2：图片、3：视频、4：音乐，默认为0。 <li>{@link #FEATURE_ALL}
	 *            <li>{@link #FEATURE_ORIGINAL} <li>{@link #FEATURE_PICTURE} <li>
	 *            {@link #FEATURE_VIDEO} <li>{@link #FEATURE_MUSICE}
	 * @param trim_user
	 *            返回值中user字段开关，false：返回完整user字段、true：user字段仅返回user_id，默认为false。
	 * @param listener
	 *            异步请求回调接口
	 */
	public void getTimeline(long since_id, long max_id, int count, int page,
			boolean base_app, int featureType, boolean trim_user,
			RequestListener listener);

	public String getCache();
	
	public void actionAfterUpdate();
}
