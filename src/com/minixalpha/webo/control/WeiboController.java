package com.minixalpha.webo.control;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.minixalpha.webo.R;
import com.minixalpha.webo.WeboApplication;
import com.minixalpha.webo.adapter.WeiboItemAdapter;
import com.minixalpha.webo.data.Cache;
import com.minixalpha.webo.utils.Utils;
import com.minixalpha.webo.utils.WeiboAPI;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 控制微博时间线上的逻辑
 * 
 * @author zhaoxk
 * 
 */
public class WeiboController {
	private static final String TAG = WeiboController.class.getName();

	private Activity mViewWeiboActivity;

	private ViewWeiboHelper mViewWeiboHelper;

	private WeiboItemAdapter mStatusAdapter;

	/* 微博内容 */
	private LinkedList<Status> mWeiboList;
	/* 最新的微博list */
	private StatusList mLastStatuses;

	/* 微博 API */
	private WeiboAPI mWeiboAPI;

	/* 获取微博后的回调接口 */
	private RequestListener mRequestCallback;

	private static final int DEFAULT_WEIBO_ITEM_ID = R.layout.item_weibo;

	public WeiboController(ViewWeiboHelper viewWeiboHelper) {
		this(viewWeiboHelper, DEFAULT_WEIBO_ITEM_ID);
	}

	public WeiboController(ViewWeiboHelper viewWeiboHelper, int weiboItemResId) {
		mViewWeiboHelper = viewWeiboHelper;

		mViewWeiboActivity = viewWeiboHelper.getViewActivity();

		mWeiboAPI = WeiboAPI.getInstance();

		// 时间线UI及内容设置
		mWeiboList = new LinkedList<>();
		Log.d(TAG, mWeiboList.toString());

		// context 需要用 listview 所在的　activity，不能用全局 context
		mStatusAdapter = new WeiboItemAdapter(mViewWeiboActivity,
				weiboItemResId, mWeiboList);

		mRequestCallback = new TimelineCallback(WeiboController.this);
	}

	/**
	 * 初始化时间线上的内容
	 * 
	 */
	public void initTimeLine() {
		setTimeLine();
	}

	public WeiboItemAdapter getStatusAdapter() {
		return mStatusAdapter;
	}

	/**
	 * 获取显示微博列表的活动
	 * 
	 * @return 显示微博列表的活动
	 */
	public Activity getViewWeiboActivity() {
		return mViewWeiboActivity;
	}

	public void setLastStatus(StatusList laStatusList) {
		mLastStatuses = laStatusList;
	}

	public StatusList getLastStatus() {
		return mLastStatuses;
	}

	/**
	 * 获取微博数据列表
	 * 
	 * @return 微博数据列表
	 */
	public List<Status> getWeiboList() {
		return mWeiboList;
	}

	/*
	 * 设置时间线上的数据
	 * 
	 * 1. 如果有缓存，先取缓存，设置数据
	 * 
	 * 2. 如果没缓存,使用网络更新
	 */
	private void setTimeLine() {
		if (mWeiboList.isEmpty() && getTimeLineView().isRefreshing() == false) {
			mViewWeiboHelper.actionBeforeLoad();
		}

		String response = mViewWeiboHelper.getCache();
		boolean hasCache = !TextUtils.isEmpty(response);
		if (hasCache) {
			updateTimeLine(response);
			hasCache = true;
		} else {
			requestTimeLine(true);
		}
	}

	/**
	 * 将 json 格式的多条微博转化为　statusList，并更新时间线
	 * 
	 * 如果请求的是新微博，则去除原有微博，否则加在原有微博后面
	 * 
	 * @param response
	 *            json格式的微博列表
	 * @return
	 */
	public StatusList updateTimeLine(String response) {
		// 调用 StatusList#parse 解析字符串成微博列表对象
		if (response == null)
			return null;
		Log.d(TAG, response);
		StatusList statuses = StatusList.parse(response);
		if (statuses != null && statuses.total_number > 0
				&& statuses.statusList != null) {
			Log.d(TAG, "更新条数: " + statuses.statusList.size());
			List<Status> statusList = statuses.statusList;
			if (statusList != null && statusList.size() > 0) {
				mWeiboList.clear();
				mWeiboList.addAll(statusList);
				mStatusAdapter.notifyDataSetChanged();
				mViewWeiboHelper.actionAfterUpdate();
			}
		}
		return statuses;
	}

	public WeiboItemAdapter getAdapter() {
		return mStatusAdapter;
	}

	/**
	 * 联网请求微博 如果网络可用，展示进度条，请求最新微博，最新微博是指比缓存中最近的微博还新的微博 如果网络不可用，给出提示
	 * 
	 * @param isnew
	 *            请求的是最新微博，还是以前的微博
	 */
	private void requestTimeLine(boolean isnew) {
		Log.d(TAG, "requestTimeLine");
		boolean needRefreshComplete = true;
		boolean isNetworkAvailable = Utils.isNetworkAvailable();
		Log.d(TAG, "isNetworkAvailable:" + isNetworkAvailable);
		if (isNetworkAvailable) {
			if (mWeiboAPI.isTokenAvailable()) {
				if (isnew) {
					mViewWeiboHelper.getTimeline(0L, 0L, 10, 1, false, 0,
							false, mRequestCallback);
				} else {
					long oldWeiboID = Long.parseLong(mWeiboList.getLast().id);
					mViewWeiboHelper.getTimeline(0L, oldWeiboID - 1, 10, 1,
							false, 0, false, mRequestCallback);
				}
				needRefreshComplete = false;
			} else {
				Log.d(TAG, "Token is not valid");
			}
		} else {
			Log.d(TAG, "Network is not available");
			Utils.showMsg(R.string.network_not_available);
		}

		if (needRefreshComplete) {
			Log.d(TAG, "run to complete");
			mStatusAdapter.notifyDataSetChanged();
			mViewWeiboHelper.getTimeLineView().onRefreshComplete();
			mViewWeiboHelper.actionAfterUpdate();
		}
	}

	public PullToRefreshListView getTimeLineView() {
		return mViewWeiboHelper.getTimeLineView();
	}

	public OnRefreshListener<ListView> getOnRefreshListener() {
		return new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// 任务要放在异步Task中，不能放在这里
				// onRefreshComplete 放在这里也不行
				new GetLatestDataTask().execute();
			}

		};
	}

	private class GetLatestDataTask extends AsyncTask<Void, Void, String[]> {
		@Override
		protected void onPostExecute(String[] result) {
			/* TODO: 分离 requestTimeLine，区分UI操作与后台耗时操作 */

			if (PullToRefreshBase.Mode.PULL_FROM_START == getTimeLineView()
					.getCurrentMode()) {
				requestTimeLine(true);
			} else {
				requestTimeLine(false);
			}

			super.onPostExecute(result);
		}

		@Override
		protected String[] doInBackground(Void... params) {
			return null;
		}
	}

	public void actionAfterUpdate() {
		mViewWeiboHelper.actionAfterUpdate();
	}
}
