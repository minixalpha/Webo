package com.minixalpha.webo.control;

import android.app.Activity;
import com.sina.weibo.sdk.net.RequestListener;

public interface ViewCommentHelper {

	/**
	 * 请求评论列表
	 * 
	 * @param since_id
	 *            若指定此参数，则返回ID比since_id大的评论（即比since_id时间晚的评论），默认为0。
	 * @param max_id
	 *            若指定此参数，则返回ID小于或等于max_id的评论，默认为0。
	 * @param listener
	 *            异步请求回调接口
	 */
	public void requestComment(long since_id, long max_id,
			RequestListener listener);

	/**
	 * 请求评论列表
	 * 
	 * @param listener
	 *            请求完成后的动作
	 */
	public void requestComment(RequestListener listener);

	/**
	 * 获取 comment listview 所在的 activity
	 * 
	 * @return comment listview 所在的 activity
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
